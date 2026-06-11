package com.example.geston.repository;

import com.example.geston.model.MotivoBaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotivoBaixaRepository extends JpaRepository<MotivoBaixa, Long> {
}