package com.example.geston.dto;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class MovimentacaoDTO {
    private Long id;
    private String tipo;
    private LocalDateTime data;
    private String nomeUsuario;
    private String motivoBaixa;
    private Double valorPerdido;

    public MovimentacaoDTO(Long id, String tipo, LocalDateTime data, String nomeUsuario, String motivoBaixa, Double valorPerdido) {
        this.id = id;
        this.tipo = tipo;
        this.data = data;
        this.nomeUsuario = nomeUsuario;
        this.motivoBaixa = motivoBaixa;
        this.valorPerdido = valorPerdido;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public LocalDateTime getData() { return data; }
    public String getNomeUsuario() { return nomeUsuario; }
    public String getMotivoBaixa() { return motivoBaixa; }
    public Double getValorPerdido() { return valorPerdido; }
}