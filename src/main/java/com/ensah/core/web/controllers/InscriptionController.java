package com.ensah.core.web.controllers;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.bo.Utilisateur;
import com.ensah.core.services.EtudiantService;
import com.ensah.core.services.impl.EtudiantServiceImpl;
import com.ensah.core.utils.ExcellFileRowObject;
import com.ensah.core.utils.ExcellImporter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
@MultipartConfig
public class InscriptionController {

    @Autowired
    HttpSession session;
    @Autowired
    public EtudiantServiceImpl etudiantServiceImpl;


    @GetMapping("/admin/inscription")
    public  String importExcellGet(){

        return "/admin/inscription";

    }


    @PostMapping("/admin/inscription")
    public  String importExcellPost(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {


        if(ExcellImporter.hasExcelFormat(file))
          {      ExcellImporter excellImporter=new ExcellImporter();
                excellImporter.excellFileDataPreprocessing(etudiantServiceImpl,file.getInputStream(),session);
                if(session.getAttribute("badInfos")!=null){

                    return "redirect:/admin/updateInfos";

                }
                return  "error";
          }
        else {
            System.out.println("Le fichier n'est pas excell");
            return "error";
        }



    }


    @GetMapping("/admin/updateInfos")
    public  String updateInfos(){
        return "admin/updateStudentInfos";
    }

    @PostMapping("/admin/updateInfos/{param}")
    public  void updateInfosPost(@PathVariable String param){
        List<Etudiant>etudiants= (List<Etudiant>) session.getAttribute("badInfos");
        List<ExcellFileRowObject>rows= (List<ExcellFileRowObject>) session.getAttribute("badInfoExcell");

        for(int i=0;i<etudiants.size();i++){
            if(etudiants.get(i).getEmail().equals(param)){
                etudiants.get(i).setCne(rows.get(i).getCne());
                etudiants.get(i).setPrenom(rows.get(i).getPrenom());
                etudiants.get(i).setNom(rows.get(i).getNom());
            }
            etudiantServiceImpl.updateEtudiantNomPrenomCne(etudiants.get(i));
            rows.remove(rows.get(i));
            etudiants.remove(etudiants.get(i));
            break;
        }


    }




}
