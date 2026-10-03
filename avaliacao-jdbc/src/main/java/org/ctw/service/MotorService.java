package org.ctw.service;

import org.ctw.dao.MotorDAO;
import org.ctw.exception.EntidadeNaoEncontradaException;
import org.ctw.model.Motor;
import org.ctw.model.MotorResumo;

import java.math.BigDecimal;
import java.util.List;

public class MotorService {
    private static final List<String> STATUS_VALIDOS =
            List.of(
                    "Operando",
                    "Alerta",
                    "Manutenção",
                    "Inativo"
            );

    private final MotorDAO motorDAO;

    public MotorService(MotorDAO motorDAO) {
        this.motorDAO = motorDAO;
    }

    public List<Motor> listarTodos() {
        return motorDAO.listarTodos();
    }

    public Motor buscarPorId(Integer id) {
        validarId(id);

        return motorDAO.buscarPorId(id)
                .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                                "Motor de ID " + id
                                        + " não encontrado."
                        )
                );
    }

    public Motor cadastrar(Motor motor) {
        validarMotor(motor);

        motor.setId(null);

        return motorDAO.inserir(motor);
    }

    public void atualizar(Motor motor) {
        validarId(motor.getId());
        validarMotor(motor);
        buscarPorId(motor.getId());

        if (!motorDAO.atualizar(motor)) {
            throw new EntidadeNaoEncontradaException(
                    "Motor não encontrado."
            );
        }
    }

    public void excluir(Integer id) {
        validarId(id);
        buscarPorId(id);

        if (!motorDAO.excluir(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Motor não encontrado."
            );
        }
    }

    public List<Motor> filtrar(
            String fabricante,
            String status
    ) {
        if (fabricante == null || fabricante.isBlank()) {
            throw new IllegalArgumentException(
                    "O fabricante é obrigatório."
            );
        }

        validarStatus(status);

        return motorDAO.filtrar(
                fabricante.trim(),
                status
        );
    }

    public List<MotorResumo> gerarResumo() {
        return motorDAO.gerarResumo();
    }

    private void validarMotor(Motor motor) {
        if (motor == null) {
            throw new IllegalArgumentException(
                    "Os dados do motor são obrigatórios."
            );
        }

        if (motor.getSetorId() == null
                || motor.getSetorId() <= 0) {

            throw new IllegalArgumentException(
                    "O setor é obrigatório."
            );
        }

        if (motor.getCodigoAtivo() == null
                || motor.getCodigoAtivo().isBlank()) {

            throw new IllegalArgumentException(
                    "O código do ativo é obrigatório."
            );
        }

        if (motor.getPotenciaKw() == null
                || motor.getPotenciaKw()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A potência deve ser positiva."
            );
        }

        if (motor.getRpmNominal() == null
                || motor.getRpmNominal() <= 0) {

            throw new IllegalArgumentException(
                    "O RPM deve ser positivo."
            );
        }

        validarStatus(motor.getStatusAtual());
    }

    private void validarId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser positivo."
            );
        }
    }

    private void validarStatus(String status) {
        if (!STATUS_VALIDOS.contains(status)) {
            throw new IllegalArgumentException(
                    "Status inválido. Valores permitidos: "
                            + STATUS_VALIDOS
            );
        }
    }
}
