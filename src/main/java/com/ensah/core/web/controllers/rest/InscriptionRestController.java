package com.ensah.core.web.controllers.rest;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.bo.Filiere;
import com.ensah.core.bo.Journal;
import com.ensah.core.dao.FiliereDao;
import com.ensah.core.services.JournalService;
import com.ensah.core.services.impl.EtudiantServiceImpl;
import com.ensah.core.services.impl.InscriptionServiceImpl;
import com.ensah.core.services.impl.JournalServiceImpl;
import com.ensah.core.utils.ExcellFileRowObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

@RestController
public class InscriptionRestController {

    @Autowired
    HttpSession session;
    @Autowired
    EtudiantServiceImpl etudiantServiceImpl;
    @Autowired
    FiliereDao filiereDao;
    ExcellFileRowObject targetRow;
    Etudiant targetEtudiant;

    @Autowired
    InscriptionServiceImpl inscriptionService;


    @RequestMapping(value = "/admin/rest/updateInfos/{email}",method = RequestMethod.GET)
    public  String  updateInfos(@PathVariable(name = "email",required = true) String email){

        List<Etudiant> etudiants= (List<Etudiant>) session.getAttribute("badInfos");
        List<ExcellFileRowObject>rows= (List<ExcellFileRowObject>) session.getAttribute("badInfoExcell");
        List<ExcellFileRowObject>rowsWithNoErros= (List<ExcellFileRowObject>) session.getAttribute("inscritspaserreur");
        List<Etudiant> etudiantDejaInscrits= (List<Etudiant>) session.getAttribute("dejaInscrits");


        for(int i=0;i<etudiants.size();i++){
            System.out.println(etudiants.get(i).getEmail());
            if(etudiants.get(i).getEmail().equals(email)){
                targetRow=rows.get(i);
                targetEtudiant=etudiants.get(i);
                Etudiant et=etudiants.get(i);
                ExcellFileRowObject ex=rows.get(i);
                Journal journal=new Journal();
                journal.setEvenement(et.getNom(),et.getPrenom(),et.getCne(),ex.getNom(),ex.getPrenom(), ex.getCne(),session);
                etudiants.get(i).setCne(rows.get(i).getCne());
                etudiants.get(i).setPrenom(rows.get(i).getPrenom());
                etudiants.get(i).setNom(rows.get(i).getNom());
                 etudiantServiceImpl.updateEtudiantNomPrenomCne(etudiants.get(i),journal);

                break;

            }






        }

        rowsWithNoErros.add(targetRow);
        etudiantDejaInscrits.add(targetEtudiant);
        rows.remove(targetRow);
        etudiants.remove(targetEtudiant);


        session.setAttribute("inscritspaserreur",rowsWithNoErros);
        session.setAttribute("dejaInscrits",etudiantDejaInscrits);

        //On verifie voir si toutes les lignes de donnees contradictoires ont ete corrigees
        if(rows.size()>0){
            session.setAttribute("badInfoExcell",rows);
            session.setAttribute("badInfos",etudiants);
        }
        else {
            session.removeAttribute("badInfos");
            session.removeAttribute("badInfoExcell");
        }

        return  "succes";
    }


    @RequestMapping(value = "admin/validerInscriptions/{id}", method = RequestMethod.GET)
    public void validerInscriptionsPost(@PathVariable("id") int id) {



        List<Etudiant> etudiants= (List<Etudiant>) session.getAttribute("dejaInscrits");
        Etudiant etudiant=null;
        for(Etudiant et:etudiants){
            if(et.getIdUtilisateur()==id){
                etudiant=et;
                break;
            }
        }


        inscriptionService.reinscrireEtudiant(etudiant);

    }


    @RequestMapping(value = "admin/InscrireNouvel/{id}", method = RequestMethod.GET)
    public void validerInscriptionsNouveauxPost(@PathVariable("id") int id) {



        List<Etudiant> etudiants= (List<Etudiant>) session.getAttribute("dejaInscrits");
        Etudiant etudiant=null;
        for(Etudiant et:etudiants){
            if(et.getIdUtilisateur()==id){
                etudiant=et;
                break;
            }
        }


        inscriptionService.reinscrireEtudiant(etudiant);

    }





    @RequestMapping (value = "/admin/rest/filiere",method = RequestMethod.GET)
    public   String FakeFunction(){
        for(int i=0;i<24;i++){
            Filiere filiere=new Filiere();
            int annneF=2030;
            int anneFDe=2010;
            filiere.setAnneeaccreditation(anneFDe);

            filiere.setAnneeFinaccreditation(annneF);
            String code ="jhjhjhjh"+i+200;
            filiere.setCodeFiliere(code);
            String titre="t+"+i+300;
            filiere.setTitreFiliere(titre);
            System.out.println(filiere);

            filiereDao.save(filiere);
        }
        return "kkj";
    }

    @RequestMapping("admin/inscrire/{id}")
    public  void inscriptionDefinitive(@PathVariable(name = "id",required = true) Long id){
        Etudiant etudiant=etudiantServiceImpl.getEtudiant(id);

    }
}
