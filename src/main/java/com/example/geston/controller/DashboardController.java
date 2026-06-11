package com.example.geston.controller;

import com.example.geston.service.MovimentacaoService;
import com.example.geston.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

@Controller
public class DashboardController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private MovimentacaoService movimentacaoService;

    @GetMapping("/home")
    public String irParaHome(Model model) {
        Double valorTotal = produtoService.calcularValorTotal();
        Integer estoqueBaixoCount = produtoService.contarEstoqueBaixo();
        Integer totalUnidades = produtoService.contarTotalUnidades();

        model.addAttribute("valorTotalEstoque", valorTotal);
        model.addAttribute("qtdEstoqueBaixo", estoqueBaixoCount);
        model.addAttribute("totalUnidades", totalUnidades);


        Pageable limiteCinco = PageRequest.of(0, 5);
        model.addAttribute("ultimasAtividades", movimentacaoService.buscarUltimasAtividades(limiteCinco));

        Integer limiteEstoqueCritico = 5;
        model.addAttribute("listaProdutosBaixos", produtoService.buscarProdutosComEstoqueBaixo(limiteEstoqueCritico));

        LocalDate dataLimiteVencimento = LocalDate.now().plusDays(10);
        model.addAttribute("listaProdutosVencendo", produtoService.buscarProdutosProximosAoVencimento(dataLimiteVencimento));

        return "dashboard";
    }

    @GetMapping("/perfil")
    public String irParaPerfil() {
        return "perfil";
    }
}