package com.example.geston.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String motivoBaixa;
    private Double valorPerdido;
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    @OneToOne(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemMovimentacao item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivoBaixa() {
        return motivoBaixa;
    }

    public void setMotivoBaixa(String motivoBaixa) {
        this.motivoBaixa = motivoBaixa;
    }

    public Double getValorPerdido() {
        return valorPerdido;
    }

    public void setValorPerdido(Double valorPerdido) {
        this.valorPerdido = valorPerdido;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public ItemMovimentacao getItem() {
        return item;
    }

    public void setItem(ItemMovimentacao item) {
        this.item = item;
    }
}