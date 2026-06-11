package com.example.geston.dto;
@SuppressWarnings("unused")
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String cpf;

    public UsuarioDTO(Long id, String nome, String cpf) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
}