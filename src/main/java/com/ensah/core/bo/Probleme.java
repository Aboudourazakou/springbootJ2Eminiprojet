package com.ensah.core.bo;

import javax.persistence.*;

@Entity
public class Probleme {
    private Long id;

    @OneToOne
    @JoinColumn(name="homme_id")
    private  Homme homme;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }
}
