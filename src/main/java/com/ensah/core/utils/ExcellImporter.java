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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@MultipartConfig
public class ExcellImporter {


    static String[] HEADERs = {"ID ETUDIANT", "CNE", "NOM", "PRENOM","ID NIVEAU ACTUEL","Type"};
    static String SHEET = "etudiants";
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";





    public  static boolean hasExcelFormat(MultipartFile file){
        if(TYPE.equals(file.getContentType())){
            return  true;
        }
        return  false;
    }

    public  void excellFileDataPreprocessing(EtudiantServiceImpl etudiantServiceImpl,InputStream is){

        List<ExcellFileRowObject> excellFileRowObjectsNotExistsInDatabase=new ArrayList<>();
        List<ExcellFileRowObject> excellFileRowObjectsExistsInDatabaseWithErrors=new ArrayList<>();
        List<Etudiant>alreadyRegisteredStudents=new ArrayList<>();
        List <Etudiant>alreadyRegisteredStudentsWithErrors=new ArrayList<>();


        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<InscriptionAnnuelle> inscriptionAnnuelles = new ArrayList<InscriptionAnnuelle>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                Iterator<Cell> cellsInRow = currentRow.iterator();

                System.out.println(rowNumber+"row");
                if(rowNumber==0){
                    rowNumber++;
                    while (cellsInRow.hasNext()){
                        Cell currentCell=cellsInRow.next();
                        System.out.println(currentCell.getStringCellValue());
                    }

                    continue;
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
                    Etudiant etudiant=new Etudiant();
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
                }

                rowNumber++;

            }
            System.out.println("Etudiant dejas inscrits\n\n");
            System.out.println(alreadyRegisteredStudents);
            System.out.println("Etudiants pas encore inscrits\n\n");
            System.out.println(excellFileRowObjectsNotExistsInDatabase);
            System.out.println("Etudiants deja inscrits avec erreur\n\n");
            System.out.println(alreadyRegisteredStudentsWithErrors);
            System.out.println("Son fichier excell est");
            System.out.println(excellFileRowObjectsExistsInDatabaseWithErrors);
            workbook.close();

        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }

    }

}
