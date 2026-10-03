package org.ctw.model;

import java.math.BigDecimal;

public class MotorResumo {
    private Integer motorId;
    private String codigoAtivo;
    private String fabricante;
    private String setor;
    private String localizacao;
    private String status;
    private Long quantidadeLeituras;
    private BigDecimal mediaTemperatura;
    private BigDecimal mediaVibracao;
    private Long quantidadeAlertasPendentes;

    public Integer getMotorId() {
        return motorId;
    }

    public void setMotorId(Integer motorId) {
        this.motorId = motorId;
    }

    public String getCodigoAtivo() {
        return codigoAtivo;
    }

    public void setCodigoAtivo(String codigoAtivo) {
        this.codigoAtivo = codigoAtivo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getQuantidadeLeituras() {
        return quantidadeLeituras;
    }

    public void setQuantidadeLeituras(Long quantidadeLeituras) {
        this.quantidadeLeituras = quantidadeLeituras;
    }

    public BigDecimal getMediaTemperatura() {
        return mediaTemperatura;
    }

    public void setMediaTemperatura(
            BigDecimal mediaTemperatura
    ) {
        this.mediaTemperatura = mediaTemperatura;
    }

    public BigDecimal getMediaVibracao() {
        return mediaVibracao;
    }

    public void setMediaVibracao(
            BigDecimal mediaVibracao
    ) {
        this.mediaVibracao = mediaVibracao;
    }
    public Long getQuantidadeAlertasPendentes() {
        return quantidadeAlertasPendentes;
    }

    public void setQuantidadeAlertasPendentes(
            Long quantidadeAlertasPendentes
    ) {
        this.quantidadeAlertasPendentes =
                quantidadeAlertasPendentes;
    }

    @Override
    public String toString() {
        return "MotorResumo{" +
                "motorId=" + motorId +
                ", codigoAtivo='" + codigoAtivo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", setor='" + setor + '\'' +
                ", status='" + status + '\'' +
                ", quantidadeLeituras=" + quantidadeLeituras +
                ", mediaTemperatura=" + mediaTemperatura +
                ", mediaVibracao=" + mediaVibracao +
                '}';
    }
}
