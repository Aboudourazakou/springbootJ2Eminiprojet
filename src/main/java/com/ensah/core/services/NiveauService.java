package com.ensah.core.services;

import com.ensah.core.bo.Etudiant;

public interface NiveauService {

    public boolean findIfNiveauExists(Long id);


    boolean validerNiveau(Etudiant etudiant, Long id);
}
