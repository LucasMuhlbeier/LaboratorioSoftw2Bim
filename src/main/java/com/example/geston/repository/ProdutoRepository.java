package com.example.geston.repository;

import com.example.geston.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByNome(String nome);

    List<Produto> findByQtdEstoqueLessThanEqual(Integer quantidade);

    @Query("SELECT p FROM Produto p WHERE p.dataVencimento <= :dataLimite ORDER BY p.dataVencimento ASC")
    List<Produto> findProdutosProximosAoVencimento(@Param("dataLimite") LocalDate dataLimite);
}