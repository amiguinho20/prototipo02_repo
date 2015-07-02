package br.com.fences.prototipo02.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Carga implements Serializable {
	
	private static final long serialVersionUID = -140347927305414495L;

	@SerializedName("ID_TIPO_CARGA")
	private String idTipoCarga;
	
	@SerializedName("ID_UNIDADE")
	private String idUnidade;
	
	@SerializedName("ORIGEM_CARGA")
	private String origemCarga;
	
	@SerializedName("DESTINO_CARGA")
	private String destinoCarga;
	
	@SerializedName("NOTAS_FISCAIS_CARGA")
	private String notasFiscaisCarga;
	
	@SerializedName("VALOR_CARGA")
	private String valorCarga;
	
	@SerializedName("NOME_EMBARCADOR_CARGA")
	private String nomeEmbarcadorCarga;
	
	@SerializedName("LOCAL_EMBARQUE_CARGA")
	private String localEmbarqueCarga;
	
	@SerializedName("FLAG_CARGA_RECUPERADA_CARGA")
	private String flagCargaRecuperadaCarga;
	
	@SerializedName("LOCAL_RECUPERACAO_CARGA")
	private String localRecuperacaoCarga;
	
	@SerializedName("DATA_RECUPERACAO_CARGA")
	private String dataRecuperacaoCarga;
	
	@SerializedName("HORA_RECUPERACAO_CARGA")
	private String horaRecuperacaoCarga;
	
	@SerializedName("VALOR_ESTIMADO_RECUP_CARGA")
	private String valorEstimadoRecupCarga;
	
	@SerializedName("FLAG_SEGURO_CARGA")
	private String flagSeguroCarga;
	
	@SerializedName("SERIE_CARGA")
	private String serieCarga;
	
	@SerializedName("MARCA_CARGA")
	private String marcaCarga;
	
	@SerializedName("ID_MODO_OBJETO")
	private String idModoObjeto;
	
	@SerializedName("DETALHE_CARGA")
	private String detalheCarga;
	
	@SerializedName("ID_SUBTIPO_CARGA")
	private String idSubtipoCarga;
	
	@SerializedName("DESCR_TIPO_CARGA")
	private String descrTipoCarga;
	
	@SerializedName("DESCR_MODO_OBJETO")
	private String descrModoObjeto;
	
	@SerializedName("DESCR_UNIDADE")
	private String descrUnidade;
	
	@Override
	public String toString() {
		return "Carga [idTipoCarga=" + idTipoCarga + ", idUnidade=" + idUnidade
				+ ", origemCarga=" + origemCarga + ", destinoCarga="
				+ destinoCarga + ", notasFiscaisCarga=" + notasFiscaisCarga
				+ ", valorCarga=" + valorCarga + ", nomeEmbarcadorCarga="
				+ nomeEmbarcadorCarga + ", localEmbarqueCarga="
				+ localEmbarqueCarga + ", flagCargaRecuperadaCarga="
				+ flagCargaRecuperadaCarga + ", localRecuperacaoCarga="
				+ localRecuperacaoCarga + ", dataRecuperacaoCarga="
				+ dataRecuperacaoCarga + ", horaRecuperacaoCarga="
				+ horaRecuperacaoCarga + ", valorEstimadoRecupCarga="
				+ valorEstimadoRecupCarga + ", flagSeguroCarga="
				+ flagSeguroCarga + ", serieCarga=" + serieCarga
				+ ", marcaCarga=" + marcaCarga + ", idModoObjeto="
				+ idModoObjeto + ", detalheCarga=" + detalheCarga
				+ ", idSubtipoCarga=" + idSubtipoCarga + ", descrTipoCarga="
				+ descrTipoCarga + ", descrModoObjeto=" + descrModoObjeto
				+ ", descrUnidade=" + descrUnidade + "]";
	}
	public String getIdTipoCarga() {
		return idTipoCarga;
	}
	public void setIdTipoCarga(String idTipoCarga) {
		this.idTipoCarga = idTipoCarga;
	}
	public String getIdUnidade() {
		return idUnidade;
	}
	public void setIdUnidade(String idUnidade) {
		this.idUnidade = idUnidade;
	}
	public String getOrigemCarga() {
		return origemCarga;
	}
	public void setOrigemCarga(String origemCarga) {
		this.origemCarga = origemCarga;
	}
	public String getDestinoCarga() {
		return destinoCarga;
	}
	public void setDestinoCarga(String destinoCarga) {
		this.destinoCarga = destinoCarga;
	}
	public String getNotasFiscaisCarga() {
		return notasFiscaisCarga;
	}
	public void setNotasFiscaisCarga(String notasFiscaisCarga) {
		this.notasFiscaisCarga = notasFiscaisCarga;
	}
	public String getValorCarga() {
		return valorCarga;
	}
	public void setValorCarga(String valorCarga) {
		this.valorCarga = valorCarga;
	}
	public String getNomeEmbarcadorCarga() {
		return nomeEmbarcadorCarga;
	}
	public void setNomeEmbarcadorCarga(String nomeEmbarcadorCarga) {
		this.nomeEmbarcadorCarga = nomeEmbarcadorCarga;
	}
	public String getLocalEmbarqueCarga() {
		return localEmbarqueCarga;
	}
	public void setLocalEmbarqueCarga(String localEmbarqueCarga) {
		this.localEmbarqueCarga = localEmbarqueCarga;
	}
	public String getFlagCargaRecuperadaCarga() {
		return flagCargaRecuperadaCarga;
	}
	public void setFlagCargaRecuperadaCarga(String flagCargaRecuperadaCarga) {
		this.flagCargaRecuperadaCarga = flagCargaRecuperadaCarga;
	}
	public String getLocalRecuperacaoCarga() {
		return localRecuperacaoCarga;
	}
	public void setLocalRecuperacaoCarga(String localRecuperacaoCarga) {
		this.localRecuperacaoCarga = localRecuperacaoCarga;
	}
	public String getDataRecuperacaoCarga() {
		return dataRecuperacaoCarga;
	}
	public void setDataRecuperacaoCarga(String dataRecuperacaoCarga) {
		this.dataRecuperacaoCarga = dataRecuperacaoCarga;
	}
	public String getHoraRecuperacaoCarga() {
		return horaRecuperacaoCarga;
	}
	public void setHoraRecuperacaoCarga(String horaRecuperacaoCarga) {
		this.horaRecuperacaoCarga = horaRecuperacaoCarga;
	}
	public String getValorEstimadoRecupCarga() {
		return valorEstimadoRecupCarga;
	}
	public void setValorEstimadoRecupCarga(String valorEstimadoRecupCarga) {
		this.valorEstimadoRecupCarga = valorEstimadoRecupCarga;
	}
	public String getFlagSeguroCarga() {
		return flagSeguroCarga;
	}
	public void setFlagSeguroCarga(String flagSeguroCarga) {
		this.flagSeguroCarga = flagSeguroCarga;
	}
	public String getSerieCarga() {
		return serieCarga;
	}
	public void setSerieCarga(String serieCarga) {
		this.serieCarga = serieCarga;
	}
	public String getMarcaCarga() {
		return marcaCarga;
	}
	public void setMarcaCarga(String marcaCarga) {
		this.marcaCarga = marcaCarga;
	}
	public String getIdModoObjeto() {
		return idModoObjeto;
	}
	public void setIdModoObjeto(String idModoObjeto) {
		this.idModoObjeto = idModoObjeto;
	}
	public String getDetalheCarga() {
		return detalheCarga;
	}
	public void setDetalheCarga(String detalheCarga) {
		this.detalheCarga = detalheCarga;
	}
	public String getIdSubtipoCarga() {
		return idSubtipoCarga;
	}
	public void setIdSubtipoCarga(String idSubtipoCarga) {
		this.idSubtipoCarga = idSubtipoCarga;
	}
	public String getDescrTipoCarga() {
		return descrTipoCarga;
	}
	public void setDescrTipoCarga(String descrTipoCarga) {
		this.descrTipoCarga = descrTipoCarga;
	}
	public String getDescrModoObjeto() {
		return descrModoObjeto;
	}
	public void setDescrModoObjeto(String descrModoObjeto) {
		this.descrModoObjeto = descrModoObjeto;
	}
	public String getDescrUnidade() {
		return descrUnidade;
	}
	public void setDescrUnidade(String descrUnidade) {
		this.descrUnidade = descrUnidade;
	}

}
