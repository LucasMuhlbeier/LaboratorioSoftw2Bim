package com.example.geston.repository;

import com.example.geston.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCpfAndSenha(String cpf, String senha);


    Optional<Usuario> findByCpf(String cpf);
}