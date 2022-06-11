package com.ensah.core.bo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class ModifiedUsers {
    @Id
    @GeneratedValue()
    private  long id;
    private  String nom;
    private  String prenom;
    private  String cne;
    private  Long idEtudiant;

}
