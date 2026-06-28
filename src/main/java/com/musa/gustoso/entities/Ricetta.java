package com.musa.gustoso.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("RICETTA")
public class Ricetta extends Contenuto {

    private Integer tempoPreparazione;
    private String difficolta;
    private String procedimento;

    @ManyToMany
    @JoinTable(
        name = "ricetta_categoria",
        joinColumns = @JoinColumn(name = "ricetta_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorie = new ArrayList<>();

    // Quando si elimina una ricetta si eliminano in automativo anche i suoi ingredienti e recensioni
    @OneToMany(mappedBy = "ricetta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RicettaIngrediente> ricettaIngredienti = new ArrayList<>();

    @OneToMany(mappedBy = "ricetta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni = new ArrayList<>();

    public Ricetta() {
    }

    public Integer getTempoPreparazione() {
        return tempoPreparazione;
    }

    public void setTempoPreparazione(Integer tempoPreparazione) {
        this.tempoPreparazione = tempoPreparazione;
    }

    public String getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(String difficolta) {
        this.difficolta = difficolta;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public List<Categoria> getCategorie() {
        return categorie;
    }

    public void setCategorie(List<Categoria> categorie) {
        this.categorie = categorie;
    }

    public List<RicettaIngrediente> getRicettaIngredienti() {
        return ricettaIngredienti;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }
}
