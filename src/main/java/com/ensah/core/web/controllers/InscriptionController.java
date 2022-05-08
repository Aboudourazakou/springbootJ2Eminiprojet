package com.ensah.core.web.controllers;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.bo.Filiere;
import com.ensah.core.bo.Utilisateur;
import com.ensah.core.dao.FiliereDao;
import com.ensah.core.dao.NiveauDao;
import com.ensah.core.services.EtudiantService;
import com.ensah.core.services.InscriptionService;
import com.ensah.core.services.NiveauService;
import com.ensah.core.services.impl.EtudiantServiceImpl;
import com.ensah.core.services.impl.NiveauServiceImpl;
import com.ensah.core.utils.ExcellFileRowObject;
import com.ensah.core.utils.ExcellImporter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


@Controller
@MultipartConfig
public class InscriptionController {

    @Autowired
    HttpSession session;
    @Autowired
    NiveauServiceImpl niveauServiceImpl;

    @Autowired
    public EtudiantServiceImpl etudiantServiceImpl;
    @Autowired
    public InscriptionService inscriptionService;


    @GetMapping("/admin/inscription")
    public  String importExcellGet(){

        return "/admin/inscription";

    }


    @PostMapping("/admin/inscription")
    public  String importExcellPost(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {


        if(ExcellImporter.hasExcelFormat(file)) {
            ExcellImporter excellImporter = new ExcellImporter();

            excellImporter.excellFileDataPreprocessing(etudiantServiceImpl, file.getInputStream(), session, niveauServiceImpl);
            if (session.getAttribute("erreur") == null) {//Si tout s'est bien passe

                if (session.getAttribute("badInfos") != null) {//S'il  y a de donnees contradictoires dans le fichier
                    //et la  base de donnees

                    return "redirect:/admin/updateInfos";

                } else {

                    return "redirect:/admin/validerInscriptions";
                }
            } else {
                System.out.println("Erreur non signalee");
                return "erreur";
            }

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

    @GetMapping("/admin/validerInscriptions")
    public String validerInscriptionGet(){
         assainirLaSession();
        return "admin/listInscriptions";
    }
/*
    @PostMapping("admin/validerInscriptions")
    public  void validerInscriptionsPost(){

       List<Etudiant>etudiants= (List<Etudiant>) session.getAttribute("dejaInscrits");
       for(Etudiant et:etudiants){
           inscriptionService.reinscrireEtudiant(et);
       }



    }
*/


    public  void assainirLaSession(){
        //On cherche a savoir s'il y a des infos que l'utilisateur n'a pas mis a jour
        if(session.getAttribute("badInfos")!=null){
            List<ExcellFileRowObject> rows= (List<ExcellFileRowObject>) session.getAttribute("badInfoExcell");
            List<Etudiant>etudiantsWithErrors= (List<Etudiant>) session.getAttribute("badInfos");
            List<ExcellFileRowObject>rowsWithNoErrors= (List<ExcellFileRowObject>) session.getAttribute("inscritspaserreur");
            List<Etudiant>etudiantsInscrits= (List<Etudiant>) session.getAttribute("dejaInscrits");
            //S'il y en a on les considere comme le bon vouloir de user,alors on les deplace
            rowsWithNoErrors.addAll(rows);
            etudiantsInscrits.addAll(etudiantsWithErrors);
            session.removeAttribute("badInfos");
            session.removeAttribute("badInfoExcell");

        }
    }








}
