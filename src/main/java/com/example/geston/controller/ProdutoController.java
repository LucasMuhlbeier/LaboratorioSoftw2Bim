package com.example.geston.controller;

import com.example.geston.model.Produto;
import com.example.geston.service.ProdutoService;
import com.example.geston.service.FornecedorService; // IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/produtos/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("produto", new Produto());


        model.addAttribute("fornecedores", fornecedorService.listarTodos());

        return "entradas";
    }

    @PostMapping("/produtos/salvar")
    public String salvar(Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/produtos";
    }
}