package com.ensah.core.services;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.bo.Journal;
import com.ensah.core.bo.ModifiedUsers;

public interface EtudiantService  {

    public boolean findIfEtudiantExists(Long id);
    public  Etudiant getEtudiant(Long id);
    public  boolean updateEtudiantNomPrenomCne(Etudiant etudiant, Journal journal, ModifiedUsers m);
    public  void saveEtudiant(Etudiant etudiant);



}
