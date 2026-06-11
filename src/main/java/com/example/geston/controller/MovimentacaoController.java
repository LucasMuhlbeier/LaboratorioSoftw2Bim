package com.example.geston.controller;

import com.example.geston.model.Produto;
import com.example.geston.model.Usuario;
import com.example.geston.model.ParametroSistema;
import com.example.geston.model.Fornecedor;
import com.example.geston.service.MovimentacaoService;
import com.example.geston.service.ProdutoService;
import com.example.geston.service.FornecedorService;
import com.example.geston.repository.ParametroSistemaRepository;
import com.example.geston.repository.MotivoBaixaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private ParametroSistemaRepository parametroSistemaRepository;

    @Autowired
    private MotivoBaixaRepository motivoBaixaRepository;

    @GetMapping("/entradas")
    public String telaEntradas(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        model.addAttribute("fornecedores", fornecedorService.listarTodos());
        return "entradas";
    }

    @PostMapping("/entradas/salvar")
    @Transactional
    public String salvarEntrada(
            @RequestParam("nome") String nome,
            @RequestParam(value = "fornecedor", required = false) Long fornecedorId,
            @RequestParam("qtdEstoque") Integer quantidade,
            @RequestParam("precoCusto") Double precoCusto,
            @RequestParam("precoVenda") Double precoVenda,
            @RequestParam("dataVencimento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento,
            @RequestParam("dataEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrada,
            HttpSession session) {

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        Optional<Produto> produtoExistente = produtoService.buscarPorNome(nome);
        Produto produto;

        Fornecedor fornecedorReal = null;
        if (fornecedorId != null) {
            fornecedorReal = fornecedorService.listarTodos().stream()
                    .filter(f -> f.getId().equals(fornecedorId))
                    .findFirst()
                    .orElse(null);
        }

        if (produtoExistente.isPresent()) {
            produto = produtoExistente.get();
            produto.setFornecedor(fornecedorReal);
            produto.setPrecoCusto(precoCusto);
            produto.setPrecoVenda(precoVenda);
            produto.setDataVencimento(dataVencimento);
            produtoService.salvar(produto);
        } else {
            produto = new Produto();
            produto.setNome(nome);
            produto.setFornecedor(fornecedorReal);
            produto.setQtdEstoque(0);
            produto.setPrecoCusto(precoCusto);
            produto.setPrecoVenda(precoVenda);
            produto.setDataVencimento(dataVencimento);
            produtoService.salvar(produto);
        }

        movimentacaoService.registrarMovimentacao(produto.getId(), quantidade, "ENTRADA", null, usuarioLogado);

        return "redirect:/entradas";
    }

    @GetMapping("/saidas")
    public String telaSaidas(Model model) {
        model.addAttribute("listaProdutos", produtoService.listarTodos());
        return "saidas";
    }

    @PostMapping("/saidas/salvar")
    @Transactional
    public String salvarSaida(
            @RequestParam("produtoId") Long produtoId,
            @RequestParam("quantidade") Integer quantidade,
            @RequestParam("dataSaida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataSaida,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        Produto produto;
        try {
            produto = produtoService.buscarPorId(produtoId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/saidas";
        }

        if (produto.getQtdEstoque() < quantidade) {
            redirectAttributes.addFlashAttribute("erro", "Estoque insuficiente! Estoque atual: " + produto.getQtdEstoque());
            return "redirect:/saidas";
        }

        Double valorTotalSaida = produto.getPrecoVenda() * quantidade;

        movimentacaoService.registrarMovimentacao(produto.getId(), quantidade, "SAIDA", null, usuarioLogado);
        redirectAttributes.addFlashAttribute("mensagem", "Saída de R$ " + String.format("%.2f", valorTotalSaida) + " registrada com sucesso!");

        return "redirect:/saidas";
    }

    @GetMapping("/baixas")
    public String telaBaixas(Model model) {
        model.addAttribute("listaProdutos", produtoService.listarTodos());
        model.addAttribute("listaMotivos", motivoBaixaRepository.findAll());
        return "baixas";
    }

    @PostMapping("/baixas/salvar")
    @Transactional
    public String salvarBaixa(
            @RequestParam("nome") String nome,
            @RequestParam("quantidade") Integer quantidade,
            @RequestParam("motivo") String motivo,
            @RequestParam(value = "motivoPersonalizado", required = false) String motivoPersonalizado,
            @RequestParam("dataBaixa") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataBaixa,
            @RequestParam("senhaAutenticacao") String senhaAutenticacao,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<ParametroSistema> parametroOpt = parametroSistemaRepository.findById("SENHA_BAIXA");

        if (parametroOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Sistema indisponível: A chave de autorização não foi localizada no banco de dados.");
            return "redirect:/baixas";
        }

        String senhaBanco = parametroOpt.get().getValor();

        if (!senhaBanco.equals(senhaAutenticacao)) {
            redirectAttributes.addFlashAttribute("erro", "Senha de autorização incorreta!");
            return "redirect:/baixas";
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        Optional<Produto> produtoOpt = produtoService.buscarPorNome(nome);

        if (produtoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/baixas";
        }

        Produto produto = produtoOpt.get();

        if (produto.getQtdEstoque() < quantidade) {
            redirectAttributes.addFlashAttribute("erro", "Estoque insuficiente para dar baixa! Estoque atual: " + produto.getQtdEstoque());
            return "redirect:/baixas";
        }

        String motivoFinal = "Outros".equals(motivo) ? motivoPersonalizado : motivo;

        movimentacaoService.registrarMovimentacao(produto.getId(), quantidade, "BAIXA", motivoFinal, usuarioLogado);
        redirectAttributes.addFlashAttribute("mensagem", "Baixa de produto registrada com sucesso!");

        return "redirect:/baixas";
    }
}