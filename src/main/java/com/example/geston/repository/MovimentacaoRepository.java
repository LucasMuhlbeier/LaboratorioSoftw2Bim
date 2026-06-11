package com.example.geston.repository;

import com.example.geston.model.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByOrderByDataDesc(Pageable pageable);


    List<Movimentacao> findAllByDataBetween(LocalDateTime inicio, LocalDateTime fim);


    Page<Movimentacao> findAllByDataBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}