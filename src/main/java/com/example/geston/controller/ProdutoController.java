package com.example.geston.controller;

import com.example.geston.model.Produto;
import com.example.geston.service.ProdutoService;
import com.example.geston.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private FornecedorService fornecedorService;


    @GetMapping("/produtos")
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "produtos";
    }

    @PostMapping("/produtos/salvar")
    public String salvar(Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/entradas";
    }


    @GetMapping("/produtos/editar/{id}")
    public String formularioEditar(@PathVariable("id") Long id, Model model) {
        Produto produto = produtoService.listarTodos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));

        model.addAttribute("produto", produto);
        model.addAttribute("fornecedores", fornecedorService.listarTodos());
        model.addAttribute("produtos", produtoService.listarTodos());
        return "entradas";
    }
}