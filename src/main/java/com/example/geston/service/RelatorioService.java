package com.example.geston.service;

import com.example.geston.model.Movimentacao;
import com.example.geston.repository.MovimentacaoRepository;
import com.example.geston.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;


    public Double calcularFaturamentoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoRepository.findAllByDataBetween(inicio, fim).stream()
                .filter(m -> "SAIDA".equals(m.getTipo()))
                .mapToDouble(m -> m.getValorPerdido() != null ? m.getValorPerdido() : 0.0) // Ou sua lógica de valor de venda
                .sum();
    }


    public List<Movimentacao> buscarMovimentacoesPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {

        return movimentacaoRepository.findAllByDataBetween(inicio, fim, pageable).getContent();
    }
}