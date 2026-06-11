package com.example.geston.controller;

import com.example.geston.model.Usuario;
import com.example.geston.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String index() {
        return "Login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, HttpSession session) {
        String campoCpf = request.getParameter("cpf");
        if (campoCpf == null) {
            campoCpf = request.getParameter("usuario");
        }
        if (campoCpf == null) {
            campoCpf = request.getParameter("login");
        }

        String senhaEnviada = request.getParameter("senha");


        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpfAndSenha(campoCpf, senhaEnviada);

        if (usuarioOpt.isPresent()) {
            session.setAttribute("usuarioLogado", usuarioOpt.get());
            return "redirect:/home";
        }

        return "redirect:/?erro=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}