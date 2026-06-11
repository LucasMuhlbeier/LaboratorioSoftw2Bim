package com.example.geston.service;

import com.example.geston.dto.ProdutoDTO;
import com.example.geston.model.Produto;
import com.example.geston.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public void salvar(Produto produto) {
        produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
    }

    public Optional<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNome(nome);
    }

    public Double calcularValorTotal() {
        return produtoRepository.findAll().stream()
                .mapToDouble(p -> p.getQtdEstoque() * p.getPrecoVenda())
                .sum();
    }

    public Integer contarEstoqueBaixo() {
        return (int) produtoRepository.findAll().stream()
                .filter(p -> p.getQtdEstoque() <= 5)
                .count();
    }

    public Integer contarTotalUnidades() {
        return produtoRepository.findAll().stream()
                .mapToInt(Produto::getQtdEstoque)
                .sum();
    }

    public List<ProdutoDTO> buscarProdutosComEstoqueBaixo(Integer limite) {
        return produtoRepository.findByQtdEstoqueLessThanEqual(limite).stream()
                .map(p -> new ProdutoDTO(p.getId(), p.getNome(), p.getQtdEstoque(), p.getPrecoVenda(), p.getDataVencimento()))
                .toList();
    }

    public List<ProdutoDTO> buscarProdutosProximosAoVencimento(LocalDate dataLimite) {
        return produtoRepository.findProdutosProximosAoVencimento(dataLimite).stream()
                .map(p -> new ProdutoDTO(p.getId(), p.getNome(), p.getQtdEstoque(), p.getPrecoVenda(), p.getDataVencimento()))
                .toList();
    }
}