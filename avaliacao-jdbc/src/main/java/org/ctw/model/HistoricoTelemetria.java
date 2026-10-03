package org.ctw.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistoricoTelemetria {
    private Long id;
    private Integer motorId;
    private LocalDateTime dataHora;
    private BigDecimal temperaturaCarcaca;
    private Integer rpmAtual;
    private BigDecimal correnteFaseA;
    private BigDecimal correnteFaseB;
    private BigDecimal correnteFaseC;
    private BigDecimal vibracaoGlobal;

    public HistoricoTelemetria() {
    }

    public HistoricoTelemetria(Long id,
                               Integer motorId,
                               LocalDateTime dataHora,
                               BigDecimal temperaturaCarcaca,
                               Integer rpmAtual,
                               BigDecimal correnteFaseA,
                               BigDecimal correnteFaseB,
                               BigDecimal correnteFaseC,
                               BigDecimal vibracaoGlobal) {
        this.id = id;
        this.motorId = motorId;
        this.dataHora = dataHora;
        this.temperaturaCarcaca = temperaturaCarcaca;
        this.rpmAtual = rpmAtual;
        this.correnteFaseA = correnteFaseA;
        this.correnteFaseB = correnteFaseB;
        this.correnteFaseC = correnteFaseC;
        this.vibracaoGlobal = vibracaoGlobal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMotorId() {
        return motorId;
    }

    public void setMotorId(Integer motorId) {
        this.motorId = motorId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public BigDecimal getTemperaturaCarcaca() {
        return temperaturaCarcaca;
    }

    public void setTemperaturaCarcaca(BigDecimal temperaturaCarcaca) {
        this.temperaturaCarcaca = temperaturaCarcaca;
    }

    public Integer getRpmAtual() {
        return rpmAtual;
    }

    public void setRpmAtual(Integer rpmAtual) {
        this.rpmAtual = rpmAtual;
    }

    public BigDecimal getCorrenteFaseA() {
        return correnteFaseA;
    }

    public void setCorrenteFaseA(BigDecimal correnteFaseA) {
        this.correnteFaseA = correnteFaseA;
    }

    public BigDecimal getCorrenteFaseB() {
        return correnteFaseB;
    }

    public void setCorrenteFaseB(BigDecimal correnteFaseB) {
        this.correnteFaseB = correnteFaseB;
    }

    public BigDecimal getCorrenteFaseC() {
        return correnteFaseC;
    }

    public void setCorrenteFaseC(BigDecimal correnteFaseC) {
        this.correnteFaseC = correnteFaseC;
    }

    public BigDecimal getVibracaoGlobal() {
        return vibracaoGlobal;
    }

    public void setVibracaoGlobal(BigDecimal vibracaoGlobal) {
        this.vibracaoGlobal = vibracaoGlobal;
    }

    @Override
    public String toString() {
        return "HistoricoTelemetria{" +
                "id=" + id +
                ", motorId=" + motorId +
                ", dataHora=" + dataHora +
                ", temperaturaCarcaca=" + temperaturaCarcaca +
                ", rpmAtual=" + rpmAtual +
                ", correnteFaseA=" + correnteFaseA +
                ", correnteFaseB=" + correnteFaseB +
                ", correnteFaseC=" + correnteFaseC +
                ", vibracaoGlobal=" + vibracaoGlobal +
                '}';
    }
}
