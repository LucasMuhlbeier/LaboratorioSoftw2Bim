package com.example.geston.controller;

import com.example.geston.model.Fornecedor;
import com.example.geston.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // 1. Faz a busca paginada (de 5 em 5)
        Page<Fornecedor> fornecedoresPage = fornecedorRepository.findAll(PageRequest.of(page, size));

        // 2. Passa os atributos para a tela seguindo o padrão
        model.addAttribute("fornecedoresPage", fornecedoresPage);
        model.addAttribute("currentPage", page);

        // Mantém o objeto vazio que você usa para o formulário de cadastro modal/comum
        model.addAttribute("fornecedor", new Fornecedor());

        return "fornecedores";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("fornecedor") Fornecedor fornecedor) {
        fornecedorRepository.save(fornecedor);
        return "redirect:/fornecedores";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id) {
        fornecedorRepository.deleteById(id);
        return "redirect:/fornecedores";
    }
}