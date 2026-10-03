package org.ctw.service;

import org.ctw.dao.AlertaMotorDAO;
import org.ctw.exception.EntidadeNaoEncontradaException;
import org.ctw.model.AlertaMotor;
import org.ctw.model.Motor;

import java.time.LocalDateTime;
import java.util.List;

public class AlertaMotorService {
    private static final List<String> CRITICIDADES_VALIDAS =
            List.of(
                    "Baixa",
                    "Média",
                    "Alta",
                    "Crítica"
            );

    private final AlertaMotorDAO alertaDAO;
    private final MotorService motorService;

    public AlertaMotorService(
            AlertaMotorDAO alertaDAO,
            MotorService motorService
    ) {
        this.alertaDAO = alertaDAO;
        this.motorService = motorService;
    }

    public AlertaMotor cadastrar(AlertaMotor alerta) {
        validarAlerta(alerta);

        alerta.setId(null);
        alerta.setDataAlerta(LocalDateTime.now());
        alerta.setResolvido(false);

        return alertaDAO.inserir(alerta);
    }

    public List<AlertaMotor> listarTodos() {
        return alertaDAO.listarTodos();
    }

    public List<AlertaMotor> listarNaoResolvidos() {
        return alertaDAO.listarNaoResolvidos();
    }

    public List<AlertaMotor> buscarPorCriticidade(String criticidade) {
        validarCriticidade(criticidade);

        return alertaDAO.buscarPorCriticidade(criticidade);
    }

    public void marcarComoResolvido(Integer alertaId) {
        validarId(alertaId);

        alertaDAO.marcarComoResolvido(alertaId);
    }

    public AlertaMotor buscarPorId(Integer id) {
        validarId(id);

        return alertaDAO.buscarPorId(id)
                .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                                "Alerta de ID " + id
                                        + " não encontrado."
                        )
                );
    }

    public void excluir(Integer id) {
        validarId(id);
        buscarPorId(id);

        if (!alertaDAO.excluir(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Alerta não encontrado."
            );
        }
    }

    private void validarAlerta(AlertaMotor alerta) {
        if (alerta == null) {
            throw new IllegalArgumentException(
                    "Os dados do alerta são obrigatórios."
            );
        }

        if (alerta.getMotorId() == null
                || alerta.getMotorId() <= 0) {

            throw new IllegalArgumentException(
                    "O motor é obrigatório."
            );
        }

        if (alerta.getTipoAnomalia() == null) {
            throw new IllegalArgumentException(
                    "O tipo da anomalia é obrigatório."
            );
        }

        if (alerta.getDescricao() == null) {
            throw new IllegalArgumentException(
                    "A descrição é obrigatória."
            );
        }

        validarCriticidade(alerta.getCriticidade());
    }

    private void validarCriticidade(String criticidade) {
        if (!CRITICIDADES_VALIDAS.contains(criticidade)) {
            throw new IllegalArgumentException(
                    "Status inválido. Valores permitidos: "
                            + CRITICIDADES_VALIDAS
            );
        }
    }

    private void validarId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser positivo."
            );
        }
    }
}
