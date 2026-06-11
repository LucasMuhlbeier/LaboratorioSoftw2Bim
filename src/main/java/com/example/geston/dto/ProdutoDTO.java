package com.example.geston.dto;

import java.time.LocalDate;
@SuppressWarnings("unused")
public class ProdutoDTO {
    private Long id;
    private String nome;
    private Integer qtdEstoque;
    private Double precoVenda;
    private LocalDate dataVencimento;

    public ProdutoDTO(Long id, String nome, Integer qtdEstoque, Double precoVenda, LocalDate dataVencimento) {
        this.id = id;
        this.nome = nome;
        this.qtdEstoque = qtdEstoque;
        this.precoVenda = precoVenda;
        this.dataVencimento = dataVencimento;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Integer getQtdEstoque() { return qtdEstoque; }
    public Double getPrecoVenda() { return precoVenda; }
    public LocalDate getDataVencimento() { return dataVencimento; }
}