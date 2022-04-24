package com.ensah.core.utils;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.bo.InscriptionAnnuelle;
import com.ensah.core.services.EtudiantService;
import com.ensah.core.services.impl.EtudiantServiceImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@MultipartConfig
public class ExcellImporter {


    static String[] HEADERS = {"ID ETUDIANT","CNE", "NOM", "PRENOM","ID NIVEAU ACTUEL","TYPE"};
    static String SHEET = "etudiants";
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";





    public  static boolean hasExcelFormat(MultipartFile file){
        if(TYPE.equals(file.getContentType())){
            return  true;
        }
        return  false;
    }

    public  void excellFileDataPreprocessing(EtudiantServiceImpl etudiantServiceImpl, InputStream is, HttpSession session){

        List<ExcellFileRowObject> excellFileRowObjectsNotExistsInDatabase=new ArrayList<>();
        List<ExcellFileRowObject> excellFileRowObjectsExistsInDatabaseWithErrors=new ArrayList<>();
        List<Etudiant>alreadyRegisteredStudents=new ArrayList<>();
        List <Etudiant>alreadyRegisteredStudentsWithErrors=new ArrayList<>();


        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();


            int rowNumber = 0;//Ligne 0 correspond aux headers
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                Iterator<Cell> cellsInRow = currentRow.iterator();
                String []excelHeaders=new String[7];
                if(rowNumber==0){
                    rowNumber++;
                    int i=0;
                    while (cellsInRow.hasNext()){
                        Cell currentCell=cellsInRow.next();
                        excelHeaders[i]=currentCell.getStringCellValue();
                        i++;
                    }
                    System.out.println(Arrays.asList(excelHeaders));
                    System.out.println(Arrays.asList(HEADERS));

                   if(i==6){//Si le nombre de colonnes est convenable
                       if(checkFileHeaders(HEADERS,excelHeaders)){//Si les ententes sont les memes
                       continue;
                       }
                       else {
                           System.out.println("Entetes differentes");
                           break;
                       }
                   }
                   else{
                       System.out.println("Le nombre de colonnes n'est pas convenable");
                       break;
                   }

                }
                int cellX=0;
                long id=0,id_niveau=0;
                String nom="",prenom="",cne="",type="";
                while (cellsInRow.hasNext()) {
                    Cell currentCell=cellsInRow.next();

                    switch (cellX){
                        case 0:
                            id=(long) currentCell.getNumericCellValue();
                            break;
                        case 1: cne=currentCell.getStringCellValue();
                            break;
                        case 2:nom=currentCell.getStringCellValue();
                            break;
                        case 3:prenom =currentCell.getStringCellValue();
                            break;
                        case 4: id_niveau=(long)currentCell.getNumericCellValue();
                            break;
                        case 5:
                            type=currentCell.getStringCellValue();
                    }
                    cellX++;
                }



                 ExcellFileRowObject excellFileRowObject=new ExcellFileRowObject(cne,nom,prenom,type,id_niveau,id);

                if(etudiantServiceImpl.findIfEtudiantExists(id)){
                    Etudiant etudiant=etudiantServiceImpl.getEtudiant(id);


                    if(!checkReInscriptionValidity(excellFileRowObject)){
                        System.out.println("Le type d'inscription de M."+excellFileRowObject.getNom()+"" +
                                " existant dans la base est  "+excellFileRowObject.getType()+" Ce qui ne convient pas");
                        break;
                    }
                    if(!excellFileRowObject.getCne().equals(etudiant.getCne()) ||
                            !excellFileRowObject.getNom().equals(etudiant.getNom()) ||
                            !excellFileRowObject.getPrenom().equals(etudiant.getPrenom())){

                          alreadyRegisteredStudentsWithErrors.add(etudiant);
                          excellFileRowObjectsExistsInDatabaseWithErrors.add(excellFileRowObject);
                    }

                    else{
                        alreadyRegisteredStudents.add(etudiant);
                    }
                }
                else{
                    excellFileRowObjectsNotExistsInDatabase.add(excellFileRowObject);
                    if(!checkInscriptionValidity(excellFileRowObject)){
                        System.out.println("Le type d'inscription de M."+excellFileRowObject.getNom()+"" +
                                " "+excellFileRowObject.getType()+" Ce qui ne convient pas");
                        break;
                    }
                }

                rowNumber++;

            }


            session.setAttribute("dejaInscrits",alreadyRegisteredStudents);
            session.setAttribute("pasInscrits",excellFileRowObjectsNotExistsInDatabase);
            session.setAttribute("badInfos",alreadyRegisteredStudentsWithErrors);
            session.setAttribute("badInfoExcell",excellFileRowObjectsExistsInDatabaseWithErrors);

            workbook.close();

        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }

    }


    public  boolean checkFileHeaders(String [] headers,String[] excelHeaders){
        boolean correct=true;
        for(int i=0;i<6;i++){
            if(!headers[i].equals(excelHeaders[i])){
                return  false;
            }
        }
        return  correct;
    }

    public  boolean checkReInscriptionValidity(ExcellFileRowObject row){
             String type=row.getType();
             type=type.toLowerCase();
        return type.equals("reinscription");

    }

    public  boolean checkInscriptionValidity(ExcellFileRowObject row){

        String type=row.getType();
        type=type.toLowerCase();
        return type.equals("inscription");

    }

    public  boolean checkNiveauConvenable(){ return  true;}

}
