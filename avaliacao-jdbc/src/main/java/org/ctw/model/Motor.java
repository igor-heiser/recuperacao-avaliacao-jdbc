package org.ctw.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Motor {
    private Integer id;
    private Integer setorId;
    private String codigoAtivo;
    private String fabricante;
    private String modelo;
    private BigDecimal potenciaKw;
    private Integer rpmNominal;
    private LocalDate dataInstalacao;
    private String statusAtual;

    public Motor() {
    }

    public Motor(Integer id,
                 Integer setorId,
                 String codigoAtivo,
                 String fabricante,
                 String modelo,
                 BigDecimal potenciaKw,
                 Integer rpmNominal,
                 LocalDate dataInstalacao,
                 String statusAtual) {
        this.id = id;
        this.setorId = setorId;
        this.codigoAtivo = codigoAtivo;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.potenciaKw = potenciaKw;
        this.rpmNominal = rpmNominal;
        this.dataInstalacao = dataInstalacao;
        this.statusAtual = statusAtual;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSetorId() {
        return setorId;
    }

    public void setSetorId(Integer setorId) {
        this.setorId = setorId;
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

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public BigDecimal getPotenciaKw() {
        return potenciaKw;
    }

    public void setPotenciaKw(BigDecimal potenciaKw) {
        this.potenciaKw = potenciaKw;
    }

    public Integer getRpmNominal() {
        return rpmNominal;
    }

    public void setRpmNominal(Integer rpmNominal) {
        this.rpmNominal = rpmNominal;
    }

    public LocalDate getDataInstalacao() {
        return dataInstalacao;
    }

    public void setDataInstalacao(LocalDate dataInstalacao) {
        this.dataInstalacao = dataInstalacao;
    }

    public String getStatusAtual() {
        return statusAtual;
    }

    public void setStatusAtual(String statusAtual) {
        this.statusAtual = statusAtual;
    }

    @Override
    public String toString() {
        return "Motor{" +
                "id=" + id +
                ", setorId=" + setorId +
                ", codigoAtivo='" + codigoAtivo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", modelo='" + modelo + '\'' +
                ", potenciaKw=" + potenciaKw +
                ", rpmNominal=" + rpmNominal +
                ", dataInstalacao=" + dataInstalacao +
                ", statusAtual='" + statusAtual + '\'' +
                '}';
    }
}
