package org.ctw.service;

import org.ctw.dao.TelemetriaDAO;
import org.ctw.exception.EntidadeNaoEncontradaException;
import org.ctw.model.HistoricoTelemetria;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TelemetriaService {
    private final TelemetriaDAO telemetriaDAO;
    private final MotorService motorService;

    public TelemetriaService(
            TelemetriaDAO telemetriaDAO,
            MotorService motorService
    ) {
        this.telemetriaDAO = telemetriaDAO;
        this.motorService = motorService;
    }

    public HistoricoTelemetria cadastrar(
            HistoricoTelemetria telemetria
    ) {
        validarTelemetria(telemetria);

        // Garante que o motor existe.
        motorService.buscarPorId(
                telemetria.getMotorId()
        );

        telemetria.setId(null);

        if (telemetria.getDataHora() == null) {
            telemetria.setDataHora(
                    LocalDateTime.now()
            );
        }

        return telemetriaDAO.inserir(telemetria);
    }

    public List<HistoricoTelemetria> listarUltimas(
            int limite
    ) {
        if (limite <= 0 || limite > 1000) {
            throw new IllegalArgumentException(
                    "O limite deve estar entre 1 e 1000."
            );
        }

        return telemetriaDAO.listarUltimas(limite);
    }

    public HistoricoTelemetria buscarPorId(Long id) {
        validarId(id);

        return telemetriaDAO.buscarPorId(id)
                .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                                "Telemetria de ID "
                                        + id
                                        + " não encontrada."
                        )
                );
    }

    public List<HistoricoTelemetria> buscarPorMotor(
            Integer motorId
    ) {
        motorService.buscarPorId(motorId);

        return telemetriaDAO.buscarPorMotor(motorId);
    }

    public List<HistoricoTelemetria> buscarPorPeriodo(
            Integer motorId,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        motorService.buscarPorId(motorId);

        if (inicio == null || fim == null) {
            throw new IllegalArgumentException(
                    "O período inicial e final "
                            + "é obrigatório."
            );
        }

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser "
                            + "posterior à data final."
            );
        }

        return telemetriaDAO.buscarPorPeriodo(
                motorId,
                inicio,
                fim
        );
    }

    public void atualizar(
            HistoricoTelemetria telemetria
    ) {
        if (telemetria == null) {
            throw new IllegalArgumentException(
                    "Os dados são obrigatórios."
            );
        }

        validarId(telemetria.getId());
        buscarPorId(telemetria.getId());
        validarTelemetria(telemetria);

        motorService.buscarPorId(
                telemetria.getMotorId()
        );

        if (!telemetriaDAO.atualizar(telemetria)) {
            throw new EntidadeNaoEncontradaException(
                    "Telemetria não encontrada."
            );
        }
    }

    public void excluir(Long id) {
        validarId(id);
        buscarPorId(id);

        if (!telemetriaDAO.excluir(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Telemetria não encontrada."
            );
        }
    }
    private void validarTelemetria(
            HistoricoTelemetria telemetria
    ) {
        if (telemetria == null) {
            throw new IllegalArgumentException(
                    "Os dados da telemetria "
                            + "são obrigatórios."
            );
        }

        if (telemetria.getMotorId() == null
                || telemetria.getMotorId() <= 0) {

            throw new IllegalArgumentException(
                    "O motor é obrigatório."
            );
        }

        validarNaoNegativo(
                telemetria.getTemperaturaCarcaca(),
                "A temperatura"
        );

        if (telemetria.getRpmAtual() == null
                || telemetria.getRpmAtual() < 0) {

            throw new IllegalArgumentException(
                    "O RPM não pode ser negativo."
            );
        }

        validarNaoNegativo(
                telemetria.getCorrenteFaseA(),
                "A corrente da fase A"
        );

        validarNaoNegativo(
                telemetria.getCorrenteFaseB(),
                "A corrente da fase B"
        );

        validarNaoNegativo(
                telemetria.getCorrenteFaseC(),
                "A corrente da fase C"
        );

        validarNaoNegativo(
                telemetria.getVibracaoGlobal(),
                "A vibração"
        );
    }

    private void validarNaoNegativo(
            BigDecimal valor,
            String campo
    ) {
        if (valor == null
                || valor.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    campo + " não pode ser negativa."
            );
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser positivo."
            );
        }
    }

}
