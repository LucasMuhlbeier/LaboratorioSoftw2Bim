package com.example.geston.controller;

import com.example.geston.service.RelatorioService;
import com.example.geston.service.MovimentacaoService;
import com.example.geston.service.ProdutoService;
import com.example.geston.service.FornecedorService;
import com.example.geston.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private FornecedorService fornecedorService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping("/relatorios")
    public String exibirRelatorios(
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {

        LocalDate inicioParam = (dataInicio != null) ? dataInicio : LocalDate.now().minusDays(30);
        LocalDate fimParam = (dataFim != null) ? dataFim : LocalDate.now();

        LocalDateTime inicioDT = inicioParam.atStartOfDay();
        LocalDateTime fimDT = fimParam.atTime(LocalTime.MAX);

        Double faturamento = relatorioService.calcularFaturamentoPeriodo(inicioDT, fimDT);
        Pageable limites = PageRequest.of(0, 200, Sort.by("id").descending());

        model.addAttribute("faturamentoPeriodo", faturamento);
        model.addAttribute("dataInicio", inicioParam);
        model.addAttribute("dataFim", fimParam);

        model.addAttribute("historicoMovimentacoes", relatorioService.buscarMovimentacoesPorPeriodo(inicioDT, fimDT, limites));
        model.addAttribute("listaProdutos", produtoService.listarTodos());
        model.addAttribute("listaFornecedores", fornecedorService.listarTodos());

        return "relatorios";
    }

    @GetMapping("/relatorios/exportar")
    public ResponseEntity<byte[]> exportarRelatorio(
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "operacao", required = false) String operacao,
            @RequestParam(value = "filtroEstoque", required = false) String filtroEstoque,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        LocalDate inicioParam = (dataInicio != null) ? dataInicio : LocalDate.now().minusDays(30);
        LocalDate fimParam = (dataFim != null) ? dataFim : LocalDate.now();

        LocalDateTime inicioDT = inicioParam.atStartOfDay();
        LocalDateTime fimDT = fimParam.atTime(LocalTime.MAX);

        Context context = new Context();

        context.setVariable("exportMode", true);

        context.setVariable("tipoSelecionado", tipo);
        context.setVariable("operacaoSelecionada", (operacao != null && !operacao.equals("TODAS")) ? operacao : "");

        Map<String, String[]> paramSimulado = new HashMap<>();
        paramSimulado.put("tipo", new String[]{tipo});
        paramSimulado.put("operacao", new String[]{operacao != null ? operacao : "TODAS"});
        paramSimulado.put("filtroEstoque", new String[]{filtroEstoque != null ? filtroEstoque : "TODOS"});
        paramSimulado.put("dataInicio", new String[]{inicioParam.toString()});
        paramSimulado.put("dataFim", new String[]{fimParam.toString()});
        context.setVariable("param", paramSimulado);

        context.setVariable("faturamentoPeriodo", relatorioService.calcularFaturamentoPeriodo(inicioDT, fimDT));
        context.setVariable("dataInicio", inicioParam);
        context.setVariable("dataFim", fimParam);

        context.setVariable("historicoMovimentacoes", relatorioService.buscarMovimentacoesPorPeriodo(inicioDT, fimDT, PageRequest.of(0, 1000, Sort.by("id").descending())));


        List<Produto> produtosOriginais = produtoService.listarTodos();
        List<Produto> produtosFiltrados;

        if ("ESTOQUE".equals(tipo) && filtroEstoque != null) {
            switch (filtroEstoque) {
                case "MINIMO":

                    produtosFiltrados = produtosOriginais.stream()
                            .filter(p -> p.getQtdEstoque() <= 5)
                            .collect(Collectors.toList());
                    break;
                case "ZERADO":

                    produtosFiltrados = produtosOriginais.stream()
                            .filter(p -> p.getQtdEstoque() == 0)
                            .collect(Collectors.toList());
                    break;
                case "DISPONIVEL":

                    produtosFiltrados = produtosOriginais.stream()
                            .filter(p -> p.getQtdEstoque() > 0)
                            .collect(Collectors.toList());
                    break;
                default:
                    produtosFiltrados = produtosOriginais;
                    break;
            }
        } else {
            produtosFiltrados = produtosOriginais;
        }

        context.setVariable("listaProdutos", produtosFiltrados);


        context.setVariable("listaFornecedores", fornecedorService.listarTodos());

        String htmlConteudo = templateEngine.process("relatorios", context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlConteudo, "/");
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] pdfBytes = os.toByteArray();
        String nomeArquivo = "relatorio_" + tipo.toLowerCase() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}