package com.ensah.core.web.controllers.rest;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.bo.Filiere;
import com.ensah.core.bo.Journal;
import com.ensah.core.bo.Utilisateur;
import com.ensah.core.dao.FiliereDao;
import com.ensah.core.services.JournalService;
import com.ensah.core.services.NiveauService;
import com.ensah.core.services.exceptions.InscriptionFailureException;
import com.ensah.core.services.impl.EtudiantServiceImpl;
import com.ensah.core.services.impl.InscriptionServiceImpl;
import com.ensah.core.services.impl.JournalServiceImpl;
import com.ensah.core.utils.ExcellFileRowObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.xml.crypto.Data;
import java.util.*;

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
    NiveauService niveauServiceImpl;

    @Autowired
    InscriptionServiceImpl inscriptionService;


    @RequestMapping(value = "/admin/rest/updateInfos/{email}",method = RequestMethod.GET)
    public  String  updateInfos(@PathVariable(name = "email",required = true) String email){
       try{

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
       } catch (InscriptionFailureException inscriptionFailureException){

       }
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

    @Transactional
    @RequestMapping(value = "admin/InscrireNouvel/{id}", method = RequestMethod.GET)
    public void validerInscriptionsNouveauxPost(@PathVariable("id") Long id) {





        if(etudiantServiceImpl.findIfEtudiantExists(id)){
            System.out.println("Etudiant existe deja dans la base  de donnees.Donc pas queston de l'inscrire");
        }
        else{
            Etudiant etudiant=new Etudiant();
            Utilisateur u=new Utilisateur();
            List<ExcellFileRowObject> excellFileRowObjectsNotExistsInDatabase= (List<ExcellFileRowObject>) session.getAttribute("pasInscrits");
            for(ExcellFileRowObject ex:excellFileRowObjectsNotExistsInDatabase){
                if(ex.getId_etudiant()==id){
                    System.out.println("Je commence car j'ai trouve l'id");

                     etudiant.setIdUtilisateur(id);
                     etudiant.setDateNaissance(new Date());
                     etudiant.setCne(ex.getCne());
                     etudiant.setNom(ex.getNom());
                     etudiant.setPrenom(ex.getPrenom());
                     etudiant.setCne(ex.getCne());
                     etudiant.setEmail(id+"jkfjkdfjk@gmail.com");
                     etudiant.setCin("jksdjkfjk"+id);
                     etudiant.setTelephone("89238932"+id);
                     etudiant.setIdNiveauTemporaire(ex.getId_niveau());
                     if(niveauServiceImpl.checkLevelFaisability(Math.toIntExact(id))){
                         System.out.println("Je commence le pricess");

                         inscriptionService.inscrireEtudiant(etudiant);
                     }else {
                         if (id==12) System.out.println("Cycle d'ingenieur n;est pas niveau precis pour"+etudiant.getPrenom());
                         if(id==5) System.out.println("Genie informatique 3 n'est pas un niveau precis pour "+etudiant.getPrenom());
                         if(id==8) System.out.println("Genie Civil 3 n'est pas un niveau precis pour "+etudiant.getPrenom());
                     }

                }
            }

        }


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


}
