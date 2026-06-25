package com.example.geston.service;

import com.example.geston.model.Movimentacao;
import com.example.geston.model.ItemMovimentacao;
import com.example.geston.model.Produto;
import com.example.geston.model.Usuario;
import com.example.geston.repository.MovimentacaoRepository;
import com.example.geston.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public void registrarMovimentacao(Long produtoId, Integer quantidade, String tipo, String motivo, Usuario usuario) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado ID: " + produtoId));


        if ("ENTRADA".equals(tipo)) {
            produto.setQtdEstoque(produto.getQtdEstoque() + quantidade);
        } else if ("SAIDA".equals(tipo) || "BAIXA".equals(tipo)) {
            produto.setQtdEstoque(produto.getQtdEstoque() - quantidade);
        }

        produtoRepository.save(produto);


        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(tipo);
        movimentacao.setMotivoBaixa(motivo);
        movimentacao.setUsuario(usuario);
        movimentacao.setData(LocalDateTime.now());

        if ("BAIXA".equals(tipo) && produto.getPrecoCusto() != null) {
            movimentacao.setValorPerdido(produto.getPrecoCusto() * quantidade);
        } else {
            movimentacao.setValorPerdido(0.0);
        }


        ItemMovimentacao item = new ItemMovimentacao();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setMovimentacao(movimentacao);

        if ("ENTRADA".equals(tipo)) {
            item.setPrecoUnitario(produto.getPrecoCusto() != null ? produto.getPrecoCusto() : 0.0);
        } else {
            item.setPrecoUnitario(produto.getPrecoVenda() != null ? produto.getPrecoVenda() : 0.0);
        }


        movimentacao.setItem(item);

        movimentacaoRepository.save(movimentacao);
    }

    public Page<Movimentacao> buscarUltimasAtividades(Pageable pageable) {
        return movimentacaoRepository.findAll(pageable);
    }
}