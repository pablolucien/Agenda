package org.pclg.condominio;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object use to generate xml data for Transport Order document.
 */
@XmlRootElement
public class EncajerasReportingData {
    private String numeroRecibo1;
    private String numeroRecibo2;
    private String numeroRecibo3;
    private String numeroRecibo4;
    private String numeroRecibo5;
    private String numeroRecibo6;
    private String fechaCorta;
    private String fechaLarga;
    private String fechaInicio;
    private String fechaFin;
    private String saldoInicioBanco;
    private String saldoFinBanco;
    private String saldoInicioCash;
    private String saldoFinCash;
    private String totalInicio;
    private String totalFin;
    private String ingresosComunidad;
    private String ingresosAgua;
    private String gastos;
    private String pendienteComunidad;
    private String pendienteAgua;
    private String pendienteTotal;
    private String outputPath;

    public String getNumeroRecibo1() {
        return numeroRecibo1;
    }

    public void setNumeroRecibo1(String numeroRecibo1) {
        this.numeroRecibo1 = numeroRecibo1;
    }

    public String getNumeroRecibo2() {
        return numeroRecibo2;
    }

    public void setNumeroRecibo2(String numeroRecibo2) {
        this.numeroRecibo2 = numeroRecibo2;
    }

    public String getNumeroRecibo3() {
        return numeroRecibo3;
    }

    public void setNumeroRecibo3(String numeroRecibo3) {
        this.numeroRecibo3 = numeroRecibo3;
    }

    public String getNumeroRecibo4() {
        return numeroRecibo4;
    }

    public void setNumeroRecibo4(String numeroRecibo4) {
        this.numeroRecibo4 = numeroRecibo4;
    }

    public String getNumeroRecibo5() {
        return numeroRecibo5;
    }

    public void setNumeroRecibo5(String numeroRecibo5) {
        this.numeroRecibo5 = numeroRecibo5;
    }

    public String getNumeroRecibo6() {
        return numeroRecibo6;
    }

    public void setNumeroRecibo6(String numeroRecibo6) {
        this.numeroRecibo6 = numeroRecibo6;
    }

    public String getFechaCorta() {
        return fechaCorta;
    }

    public void setFechaCorta(String fechaCorta) {
        this.fechaCorta = fechaCorta;
    }

    public String getFechaLarga() {
        return fechaLarga;
    }

    public void setFechaLarga(String fechaLarga) {
        this.fechaLarga = fechaLarga;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getSaldoInicioBanco() {
        return saldoInicioBanco;
    }

    public void setSaldoInicioBanco(String saldoInicioBanco) {
        this.saldoInicioBanco = saldoInicioBanco;
    }

    public String getSaldoFinBanco() {
        return saldoFinBanco;
    }

    public void setSaldoFinBanco(String saldoFinBanco) {
        this.saldoFinBanco = saldoFinBanco;
    }

    public String getSaldoInicioCash() {
        return saldoInicioCash;
    }

    public void setSaldoInicioCash(String saldoInicioCash) {
        this.saldoInicioCash = saldoInicioCash;
    }

    public String getSaldoFinCash() {
        return saldoFinCash;
    }

    public void setSaldoFinCash(String saldoFinCash) {
        this.saldoFinCash = saldoFinCash;
    }

    public String getTotalInicio() {
        return totalInicio;
    }

    public void setTotalInicio(String totalInicio) {
        this.totalInicio = totalInicio;
    }

    public String getTotalFin() {
        return totalFin;
    }

    public void setTotalFin(String totalFin) {
        this.totalFin = totalFin;
    }

    public String getIngresosComunidad() {
        return ingresosComunidad;
    }

    public void setIngresosComunidad(String ingresosComunidad) {
        this.ingresosComunidad = ingresosComunidad;
    }

    public String getIngresosAgua() {
        return ingresosAgua;
    }

    public void setIngresosAgua(String ingresosAgua) {
        this.ingresosAgua = ingresosAgua;
    }

    public String getGastos() {
        return gastos;
    }

    public void setGastos(String gastos) {
        this.gastos = gastos;
    }

    public String getPendienteComunidad() {
        return pendienteComunidad;
    }

    public void setPendienteComunidad(String pendienteComunidad) {
        this.pendienteComunidad = pendienteComunidad;
    }

    public String getPendienteAgua() {
        return pendienteAgua;
    }

    public void setPendienteAgua(String pendienteAgua) {
        this.pendienteAgua = pendienteAgua;
    }

    public String getPendienteTotal() {
        return pendienteTotal;
    }

    public void setPendienteTotal(String pendienteTotal) {
        this.pendienteTotal = pendienteTotal;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
