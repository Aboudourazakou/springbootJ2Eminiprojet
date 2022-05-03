package com.ensah.core.services.impl;

import com.ensah.core.bo.*;
import com.ensah.core.bo.Module;
import com.ensah.core.dao.InscriptionDao;
import com.ensah.core.dao.ModuleDao;
import com.ensah.core.dao.NiveauDao;
import com.ensah.core.services.InscriptionService;
import com.ensah.core.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InscriptionServiceImpl implements InscriptionService {

    @Autowired
    InscriptionDao inscriptionDao;
    @Autowired
    ModuleService moduleService;
    @Autowired
    NiveauDao niveauDao;

    List<Integer>niveauxPossiblesCp2= Arrays.asList(3,6,9,13,16,23,26);
    List<Integer>niveauxOptions=Arrays.asList(17,18,19,20,29);

    


    @Override
    public void reinscrireEtudiant(Etudiant etudiant) {
        int niveau= Math.toIntExact(etudiant.getIdNiveauTemporaire());
        System.out.println(etudiant.getIdNiveauTemporaire());
        Niveau niveau1= niveauDao.getById(etudiant.getIdNiveauTemporaire());

        System.out.println(niveau1);

        List<InscriptionAnnuelle>inscs=etudiant.getInscriptions();
        InscriptionAnnuelle derniereInscriptionAnnuelle=inscs.get(inscs.size()-1);

        //Si la derniere inscription a une date anterieure
        if(derniereInscriptionAnnuelle.getAnnee()<new Date().getYear()){
            //On verifie si l'etudiant etait admis
            if(derniereInscriptionAnnuelle.getValidation().equals("oui")){
                if(niveauxPossiblesCp2.contains(niveau)){
                    InscriptionAnnuelle inscriptionAnnuelle=new InscriptionAnnuelle();

                    //On l'inscrit d'abord au cycle d'ingenieur
                    process(niveau1,etudiant,1);
                    //S'il est admis
                    //On l'inscrit dans la nouvelle filiere
                    process(niveau1,etudiant,2);
                }
                //On verifie s'il veut s'inscrire dans les niveaux d'options
                else if(niveauxOptions.contains(niveau)){
                    //Inscrire dans la filiere
                    process(niveau1,etudiant,3);
                    //Inscrire dans l'option
                    process(niveau1,etudiant,4);

                }
                else{
                    process(niveau1,etudiant,5);
                }

            }
            else {


            }

        }
        else{
            System.out.println("Cet etudiant est deja inscrit  pour cette annne"+etudiant.getPrenom());
        }

    }


    public  void process(Niveau niveau,Etudiant etudiant,int i){
        InscriptionAnnuelle inscriptionAnnuelle=new InscriptionAnnuelle();

        inscriptionAnnuelle.setEtudiant(etudiant);
        inscriptionAnnuelle.setType("REINSCRIPTION");
        inscriptionAnnuelle.setAnnee(new Date().getYear());
        inscriptionAnnuelle.setValidation("non");

        //Si l'etudiant quitte les prepas  pour un cycle d'ingenieur,on l'inscrit au cycle d'ingenieur
        if(i==1){
            Niveau n=new Niveau();
            n.setIdNiveau(12L);
            inscriptionAnnuelle.setNiveau(n);

        }
        else{

           //Cette condition pour des filieres  a option:derniere annnee
            if(i==3) {
                Long idT = etudiant.getIdNiveauTemporaire();
                //GC3
                if (idT == 17 || idT == 18) {
                    Niveau n = new Niveau();
                    n.setIdNiveau(8L);
                    inscriptionAnnuelle.setNiveau(n);
                }
                //GI3
                else if (idT == 19 || idT == 20 || idT == 29) {
                    Niveau n = new Niveau();
                    n.setIdNiveau(5L);
                    inscriptionAnnuelle.setNiveau(n);

                }
                }
                else{

                    inscriptionAnnuelle.setNiveau(niveau);
                    Set<InscriptionModule>inscM=new HashSet<>();
                    for(Module m:niveau.getModules()){
                        InscriptionModule inscriptionModule=new InscriptionModule();
                        inscriptionModule.setModule(m);
                        inscM.add(inscriptionModule);
                    }
                    inscriptionAnnuelle.setInscriptionModules(inscM);

                }




        }

        inscriptionDao.save(inscriptionAnnuelle);

    }
}
