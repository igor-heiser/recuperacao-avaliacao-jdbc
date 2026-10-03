package org.ctw.view;

import org.ctw.model.*;
import org.ctw.service.AlertaMotorService;
import org.ctw.service.MotorService;
import org.ctw.service.SetorService;
import org.ctw.service.TelemetriaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuConsole{
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;
    private final SetorService setorService;
    private final MotorService motorService;
    private final TelemetriaService telemetriaService;
    private final AlertaMotorService alertaService;

    public MenuConsole(Scanner scanner, SetorService setorService, MotorService motorService, TelemetriaService telemetriaService, AlertaMotorService alertaService) {
        this.scanner = scanner;
        this.setorService = setorService;
        this.motorService = motorService;
        this.telemetriaService = telemetriaService;
        this.alertaService = alertaService;
    }


    public void iniciar() {
        int opcao;

        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            try {
                executarOpcao(opcao);
            } catch (RuntimeException e) {
                System.out.println(
                        "\nErro: " + e.getMessage()
                );
            }

        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("""
            
            ==========================================
                 SISTEMA DE TELEMETRIA INDUSTRIAL
            ==========================================
            
            SETORES
            1  - Cadastrar setor
            2  - Listar setores
            3  - Buscar setor por ID
            4  - Pesquisar setor por nome
            5  - Atualizar setor
            6  - Excluir setor
            
            MOTORES
            7  - Cadastrar motor
            8  - Listar motores
            9  - Buscar motor por ID
            10 - Atualizar status do motor
            11 - Excluir motor
            12 - Filtrar motores
            13 - Exibir resumo dos motores
            
            TELEMETRIAS
            14 - Cadastrar telemetria
            15 - Listar últimas telemetrias
            16 - Buscar telemetria por ID
            17 - Listar telemetrias de um motor
            18 - Filtrar telemetrias por período
            19 - Atualizar telemetria
            20 - Excluir telemetria
            
            ALERTAS
            21 - Cadastrar alerta
            22 - Listar todos os alertas
            23 - Listar alertas não resolvidos
            24 - Filtrar alertas por criticidade
            25 - Marcar alerta como resolvido
            26 - Buscar alerta por ID
            27 - Excluir alerta
            
            0  - Sair
            """);
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
           case 1 -> cadastrarSetor();
            case 2 -> listarSetores();
            case 3 -> buscarSetor();
            case 4 -> pesquisarSetorPorNome();
            case 5 -> atualizarSetor();
            case 6 -> excluirSetor();

            case 7 -> cadastrarMotor();
            case 8 -> listarMotores();
            case 9 -> buscarMotor();
            case 10 -> atualizarStatus();
            case 11 -> excluirMotor();
            case 12 -> filtrarMotores();
            case 13 -> exibirResumo();

            case 14 -> cadastrarTelemetria();
            case 15 -> listarUltimasTelemetrias();
            case 16 -> buscarTelemetria();
            case 17 -> listarTelemetriasDoMotor();
            case 18 -> filtrarTelemetriasPorPeriodo();
            case 19 -> atualizarTelemetria();
            case 20 -> excluirTelemetria();

            case 21 -> cadastrarAlerta();
            case 22 -> listarAlertas();
            case 23 -> listarAlertasNaoResolvidos();
            case 24 -> filtrarAlertasPorCriticidade();
            case 25 -> marcarAlertaComoResolvido();
            case 26 -> buscarAlertaPorId();
            case 27 -> excluirAlerta();

            case 0 -> System.out.println(
                    "Aplicação finalizada."
            );

            default -> System.out.println(
                    "Opção inválida."
            );
        }
    }

    private void listarMotores() {
        var motores = motorService.listarTodos();

        if (motores.isEmpty()) {
            System.out.println("Nenhum motor cadastrado.");
            return;
        }

        motores.forEach(System.out::println);
    }

    private void buscarMotor() {
        int id = lerInteiro("Informe o ID: ");

        Motor motor = motorService.buscarPorId(id);

        System.out.println(motor);
    }

    private void cadastrarMotor() {
        Motor motor = new Motor();

        motor.setSetorId(
                lerInteiro("ID do setor: ")
        );

        System.out.print("Código do ativo: ");
        motor.setCodigoAtivo(
                scanner.nextLine().trim()
        );

        System.out.print("Fabricante: ");
        motor.setFabricante(
                scanner.nextLine().trim()
        );

        System.out.print("Modelo: ");
        motor.setModelo(
                scanner.nextLine().trim()
        );

        motor.setPotenciaKw(
                lerDecimal("Potência em kW: ")
        );

        motor.setRpmNominal(
                lerInteiro("RPM nominal: ")
        );

        motor.setDataInstalacao(
                lerData("Data de instalação (dd/MM/yyyy): ")
        );

        System.out.print(
                "Status [Operando/Alerta/Manutenção/Inativo]: "
        );

        motor.setStatusAtual(
                scanner.nextLine().trim()
        );

        Motor cadastrado =
                motorService.cadastrar(motor);

        System.out.println(
                "Motor cadastrado com ID "
                        + cadastrado.getId()
        );
    }

    private void atualizarStatus() {
        int id = lerInteiro("ID do motor: ");

        Motor motor = motorService.buscarPorId(id);

        System.out.print("Novo status: ");
        motor.setStatusAtual(
                scanner.nextLine().trim()
        );

        motorService.atualizar(motor);

        System.out.println(
                "Status atualizado com sucesso."
        );
    }

    private void excluirMotor() {
        int id = lerInteiro("ID do motor: ");

        System.out.print(
                "Confirma a exclusão? [S/N]: "
        );

        String confirmacao =
                scanner.nextLine().trim();

        if (confirmacao.equalsIgnoreCase("S")) {
            motorService.excluir(id);

            System.out.println(
                    "Motor excluído com sucesso."
            );
        }
    }

    private void filtrarMotores() {
        System.out.print("Fabricante: ");
        String fabricante =
                scanner.nextLine().trim();

        System.out.print("Status: ");
        String status =
                scanner.nextLine().trim();

        motorService.filtrar(fabricante, status)
                .forEach(System.out::println);
    }

    private void exibirResumo() {
        for (MotorResumo resumo :
                motorService.gerarResumo()) {

            System.out.println(resumo);
        }
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);

            String entrada = scanner.nextLine();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Informe um número inteiro válido."
                );
            }
        }
    }

    private BigDecimal lerDecimal(String mensagem) {
        while (true) {
            System.out.print(mensagem);

            String entrada = scanner.nextLine()
                    .replace(",", ".");

            try {
                return new BigDecimal(entrada);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Informe um valor decimal válido."
                );
            }
        }
    }

    private LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem);

            String entrada = scanner.nextLine();

            try {
                return LocalDate.parse(
                        entrada,
                        FORMATO_DATA
                );
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Data inválida. Utilize dd/MM/yyyy."
                );
            }
        }
    }
//ALERTAS
    private void cadastrarAlerta() {
        AlertaMotor alerta = new AlertaMotor();

        alerta.setMotorId(
                lerInteiro("ID do motor: ")
        );

        System.out.print("Tipo da anomalia: ");
        alerta.setTipoAnomalia(
                scanner.nextLine().trim()
        );

        System.out.print(
                "Criticidade [Baixa/Média/Alta/Crítica]: "
        );
        alerta.setCriticidade(
                scanner.nextLine().trim()
        );

        System.out.print("Descrição: ");
        alerta.setDescricao(
                scanner.nextLine().trim()
        );

        // O Service definirá a data e o status inicial.
        AlertaMotor cadastrado =
                alertaService.cadastrar(alerta);

        System.out.println(
                "Alerta cadastrado com ID "
                        + cadastrado.getId()
        );
    }
    private void listarAlertas() {
        var alertas = alertaService.listarTodos();

        if (alertas.isEmpty()) {
            System.out.println(
                    "Nenhum alerta cadastrado."
            );
            return;
        }

        alertas.forEach(System.out::println);
    }

    private void listarAlertasNaoResolvidos() {
        var alertas =
                alertaService.listarNaoResolvidos();

        if (alertas.isEmpty()) {
            System.out.println(
                    "Não existem alertas pendentes."
            );
            return;
        }

        alertas.forEach(System.out::println);
    }
    private void filtrarAlertasPorCriticidade() {
        System.out.print("Criticidade: ");

        String criticidade =
                scanner.nextLine().trim();

        var alertas =
                alertaService.buscarPorCriticidade(
                        criticidade
                );

        if (alertas.isEmpty()) {
            System.out.println(
                    "Nenhum alerta encontrado."
            );
            return;
        }

        alertas.forEach(System.out::println);
    }
    private void marcarAlertaComoResolvido() {
        int alertaId =
                lerInteiro("ID do alerta: ");

        alertaService.marcarComoResolvido(alertaId);

        System.out.println(
                "Alerta marcado como resolvido."
        );
    }

    private void buscarAlertaPorId() {
        int id = lerInteiro("Informe o ID: ");

        AlertaMotor alerta = alertaService.buscarPorId(id);

        System.out.println(alerta);
    }

    private void excluirAlerta() {
        int id = lerInteiro("ID do alerta: ");

        System.out.print(
                "Confirma a exclusão? [S/N]: "
        );

        String confirmacao =
                scanner.nextLine().trim();

        if (confirmacao.equalsIgnoreCase("S")) {
            alertaService.excluir(id);

            System.out.println(
                    "Alerta excluído com sucesso."
            );
        }
    }

    //TELEMETRIA
    private void cadastrarTelemetria() {
        HistoricoTelemetria telemetria =
                lerDadosTelemetria();

        HistoricoTelemetria cadastrada =
                telemetriaService.cadastrar(telemetria);

        System.out.println(
                "Telemetria cadastrada com ID "
                        + cadastrada.getId()
        );
    }

    private HistoricoTelemetria lerDadosTelemetria() {
        HistoricoTelemetria telemetria =
                new HistoricoTelemetria();

        telemetria.setMotorId(
                lerInteiro("ID do motor: ")
        );

        telemetria.setTemperaturaCarcaca(
                lerDecimal("Temperatura da carcaça: ")
        );

        telemetria.setRpmAtual(
                lerInteiro("RPM atual: ")
        );

        telemetria.setCorrenteFaseA(
                lerDecimal("Corrente da fase A: ")
        );

        telemetria.setCorrenteFaseB(
                lerDecimal("Corrente da fase B: ")
        );

        telemetria.setCorrenteFaseC(
                lerDecimal("Corrente da fase C: ")
        );

        telemetria.setVibracaoGlobal(
                lerDecimal("Vibração global: ")
        );

        telemetria.setDataHora(LocalDateTime.now());

        return telemetria;
    }

    private void listarUltimasTelemetrias() {
        int limite = lerInteiro(
                "Quantidade de registros: "
        );

        var telemetrias =
                telemetriaService.listarUltimas(limite);

        if (telemetrias.isEmpty()) {
            System.out.println(
                    "Nenhuma telemetria encontrada."
            );
            return;
        }

        telemetrias.forEach(System.out::println);
    }

    private long lerLong(String mensagem) {
        while (true) {
            System.out.print(mensagem);

            try {
                return Long.parseLong(
                        scanner.nextLine().trim()
                );
            } catch (NumberFormatException e) {
                System.out.println(
                        "Informe um número inteiro válido."
                );
            }
        }
    }

    private void buscarTelemetria() {
        long id = lerLong("ID da telemetria: ");

        HistoricoTelemetria telemetria =
                telemetriaService.buscarPorId(id);

        System.out.println(telemetria);
    }

    private void listarTelemetriasDoMotor() {
        int motorId = lerInteiro("ID do motor: ");

        var telemetrias =
                telemetriaService.buscarPorMotor(motorId);

        if (telemetrias.isEmpty()) {
            System.out.println(
                    "O motor não possui telemetrias."
            );
            return;
        }

        telemetrias.forEach(System.out::println);
    }

    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    private LocalDateTime lerDataHora(String mensagem) {
        while (true) {
            System.out.print(mensagem);

            String valor = scanner.nextLine().trim();

            try {
                return LocalDateTime.parse(
                        valor,
                        FORMATO_DATA_HORA
                );
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Data inválida. "
                                + "Use dd/MM/yyyy HH:mm."
                );
            }
        }
    }

    private void filtrarTelemetriasPorPeriodo() {
        int motorId = lerInteiro("ID do motor: ");

        LocalDateTime inicio = lerDataHora(
                "Início (dd/MM/yyyy HH:mm): "
        );

        LocalDateTime fim = lerDataHora(
                "Fim (dd/MM/yyyy HH:mm): "
        );

        var telemetrias =
                telemetriaService.buscarPorPeriodo(
                        motorId,
                        inicio,
                        fim
                );

        if (telemetrias.isEmpty()) {
            System.out.println(
                    "Nenhuma telemetria encontrada "
                            + "no período."
            );
            return;
        }

        telemetrias.forEach(System.out::println);
    }

    private void atualizarTelemetria() {
        long id = lerLong("ID da telemetria: ");

        HistoricoTelemetria existente =
                telemetriaService.buscarPorId(id);

        System.out.println(
                "Registro atual: " + existente
        );

        HistoricoTelemetria novosDados =
                lerDadosTelemetria();

        novosDados.setId(id);

        System.out.print(
                "Alterar também a data e hora? [S/N]: "
        );

        String resposta =
                scanner.nextLine().trim();

        if (resposta.equalsIgnoreCase("S")) {
            novosDados.setDataHora(
                    lerDataHora(
                            "Data e hora "
                                    + "(dd/MM/yyyy HH:mm): "
                    )
            );
        } else {
            novosDados.setDataHora(
                    existente.getDataHora()
            );
        }

        telemetriaService.atualizar(novosDados);

        System.out.println(
                "Telemetria atualizada com sucesso."
        );
    }

    private void excluirTelemetria() {
        long id = lerLong("ID da telemetria: ");

        HistoricoTelemetria telemetria =
                telemetriaService.buscarPorId(id);

        System.out.println(telemetria);

        System.out.print(
                "Confirma a exclusão? [S/N]: "
        );

        String resposta =
                scanner.nextLine().trim();

        if (!resposta.equalsIgnoreCase("S")) {
            System.out.println(
                    "Exclusão cancelada."
            );
            return;
        }

        telemetriaService.excluir(id);

        System.out.println(
                "Telemetria excluída com sucesso."
        );
    }


    //##SETOR##
    private void cadastrarSetor() {
        Setor setor = new Setor();

        System.out.print("Nome do setor: ");
        setor.setNome(scanner.nextLine());

        System.out.print("Localização: ");
        setor.setLocalizacao(scanner.nextLine());

        Setor cadastrado =
                setorService.cadastrar(setor);

        System.out.println(
                "Setor cadastrado com ID "
                        + cadastrado.getId()
        );
    }

    private void listarSetores() {
        var setores = setorService.listarTodos();

        if (setores.isEmpty()) {
            System.out.println(
                    "Nenhum setor cadastrado."
            );
            return;
        }

        System.out.println("\nSETORES CADASTRADOS");
        System.out.println("------------------------------");

        setores.forEach(System.out::println);
    }

    private void buscarSetor() {
        int id = lerInteiro("ID do setor: ");

        Setor setor = setorService.buscarPorId(id);

        System.out.println(setor);
    }

    private void pesquisarSetorPorNome() {
        System.out.print(
                "Digite parte do nome do setor: "
        );

        String nome = scanner.nextLine();

        var setores =
                setorService.buscarPorNome(nome);

        if (setores.isEmpty()) {
            System.out.println(
                    "Nenhum setor encontrado."
            );
            return;
        }

        setores.forEach(System.out::println);
    }

    private void atualizarSetor() {
        int id = lerInteiro("ID do setor: ");

        Setor setor = setorService.buscarPorId(id);

        System.out.println("Nome atual: " + setor.getNome());
        System.out.print(
                "Novo nome ou Enter para manter: "
        );

        String nome = scanner.nextLine().trim();

        if (!nome.isBlank()) {
            setor.setNome(nome);
        }

        System.out.println(
                "Localização atual: "
                        + setor.getLocalizacao()
        );

        System.out.print(
                "Nova localização ou Enter para manter: "
        );

        String localizacao =
                scanner.nextLine().trim();

        if (!localizacao.isBlank()) {
            setor.setLocalizacao(localizacao);
        }

        setorService.atualizar(setor);

        System.out.println(
                "Setor atualizado com sucesso."
        );
    }

    private void excluirSetor() {
        int id = lerInteiro("ID do setor: ");

        Setor setor = setorService.buscarPorId(id);

        System.out.println(
                "Setor selecionado: " + setor
        );

        System.out.print(
                "Confirma a exclusão? [S/N]: "
        );

        String confirmacao =
                scanner.nextLine().trim();

        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println(
                    "Exclusão cancelada."
            );
            return;
        }

        setorService.excluir(id);

        System.out.println(
                "Setor excluído com sucesso."
        );
    }
}
