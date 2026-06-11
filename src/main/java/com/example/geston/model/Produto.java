package com.example.geston.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "qtd_estoque", nullable = false)
    private Integer qtdEstoque = 0;

    @Column(name = "estq_minimo", nullable = false)
    private Integer estqMinimo = 0;

    private Double preco = 0.0;

    @Column(name = "preco_custo")
    private Double precoCusto = 0.0;

    @Column(name = "preco_venda")
    private Double precoVenda = 0.0;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento = LocalDate.now().plusYears(1);

    // ====================================================================
    // MODIFICADO: Transforma a String em um relacionamento real com Fornecedor
    // ====================================================================
    @ManyToOne
    @JoinColumn(name = "fornecedor", referencedColumnName = "id")
    private Fornecedor fornecedor;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(Integer qtdEstoque) { this.qtdEstoque = qtdEstoque; }
    public Integer getEstqMinimo() { return estqMinimo; }
    public void setEstqMinimo(Integer estqMinimo) { this.estqMinimo = estqMinimo; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    public Double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(Double precoCusto) { this.precoCusto = precoCusto; }
    public Double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(Double precoVenda) { this.precoVenda = precoVenda; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    // Atualizado para usar o objeto Fornecedor
    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
}