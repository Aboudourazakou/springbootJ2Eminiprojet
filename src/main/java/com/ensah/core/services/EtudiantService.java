package com.ensah.core.services;

import com.ensah.core.bo.Etudiant;

public interface EtudiantService  {

    public boolean findIfEtudiantExists(Long id);
    public  Etudiant getEtudiant(Long id);
}
