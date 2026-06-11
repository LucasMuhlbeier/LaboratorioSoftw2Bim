package com.example.geston.repository;

import com.example.geston.model.ItemMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMovimentacaoRepository extends JpaRepository<ItemMovimentacao, Long> {
}