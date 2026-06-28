package com.musa.gustoso.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ARTICOLO")
public class Articolo extends Contenuto {

    private Integer tempoDiLettura;
    private String corpo;

    public Articolo() {
    }

    public Integer getTempoDiLettura() {
        return tempoDiLettura;
    }
    public void setTempoDiLettura(Integer tempoDiLettura) {
        this.tempoDiLettura = tempoDiLettura;
    }
    public String getCorpo() {
        return corpo;
    }
    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

}