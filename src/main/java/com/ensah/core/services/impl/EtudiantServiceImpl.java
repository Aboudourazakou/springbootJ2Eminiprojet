package com.ensah.core.services.impl;

import com.ensah.core.bo.Etudiant;
import com.ensah.core.dao.EtudiantDao;
import com.ensah.core.services.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EtudiantServiceImpl  implements EtudiantService {

    @Autowired
    EtudiantDao etudiantDao;
     public EtudiantServiceImpl(){
         System.out.println("Je suis la");
     }
    @Override
    public boolean findIfEtudiantExists(Long id) {
        return  etudiantDao.existsById(id);
    }
    public  Etudiant getEtudiant(Long id){
        return  etudiantDao.getById(id);
    }
}
