package com.example.geston.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ParametroSistema {

    @Id
    private String chave;
    private String valor;

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}