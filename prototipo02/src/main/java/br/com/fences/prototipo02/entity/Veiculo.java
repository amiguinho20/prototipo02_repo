package br.com.fences.prototipo02.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Veiculo implements Serializable{

	@SerializedName("CONT_VEICULO")
	private String contVeiculo;
	
	@SerializedName("PLACA_VEICULO")
	private String placaVeiculo;
	
	@SerializedName("CHASSIS_VEICULO")
	private String chassisVeiculo;
	
	@SerializedName("COD_RENAVAM")
	private String codRenavam;
	
	@SerializedName("ID_TIPO_VEICULO")
	private String idTipoVeiculo;
	
	@SerializedName("ID_COMBUSTIVEL")
	private String idCombustivel;
	
	@SerializedName("ID_COR_VEICULO")
	private String idCorVeiculo;
	
	@SerializedName("ID_MARCA_VEICULO")
	private String idMarcaVeiculo;
	
	@SerializedName("CIDADE_VEICULO")
	private String cidadeVeiculo;
	
	@SerializedName("UF_VEICULO")
	private String ufVeiculo;
	
	@SerializedName("NOME_PROPRIETARIO_VEICULO")
	private String nomeProprietarioVeiculo;
	
	@SerializedName("ID_OCORRENCIA_VEICULO")
	private String idOcorrenciaVeiculo;
	
	@SerializedName("ID_TIPOLOCAL")
	private String idTipolocal;
	
	@SerializedName("FLAG_SEGURO_VEICULO")
	private String flagSeguroVeiculo;
	
	@SerializedName("SEGURADORA_VEICULO")
	private String seguradoraVeiculo;
	
	@SerializedName("APOLICE_VEICULO")
	private String apoliceVeiculo;
	
	@SerializedName("LOGOTIPO_VEICULO")
	private String logotipoVeiculo;
	
	@SerializedName("FLAG_ESCOLTA_VEICULO")
	private String flagEscoltaVeiculo;
	
	@SerializedName("TEMPO_ABANDONO_VEICULO")
	private String tempoAbandonoVeiculo;
	
	@SerializedName("DOCUMENTOS_LEVADOS")
	private String documentosLevados;
	
	@SerializedName("ANO_FABRICACAO")
	private String anoFabricacao;
	
	@SerializedName("ANO_MODELO")
	private String anoModelo;
	
	@SerializedName("OBSERVACAO_VEICULO")
	private String observacaoVeiculo;
	
	@SerializedName("CHASSIS_REMARCADO")
	private String chassisRemarcado;
	
	@SerializedName("FLAG_BUSCA_RDO")
	private String flagBuscaRdo;
	
	@SerializedName("FLAG_BUSCA_DETRAN")
	private String flagBuscaDetran;
	
	@SerializedName("CONT_PESSOA")
	private String contPessoa;
	
	@SerializedName("COD_MUNICIPIO")
	private String codMunicipio;
	
	@SerializedName("FLAG_PLACA_ANTIGA")
	private String flagPlacaAntiga;
	
	@SerializedName("DESCR_TIPO_VEICULO")
	private String descrTipoVeiculo;
	
	@SerializedName("DESCR_COMBUSTIVEL")
	private String descrCombustivel;
	
	@SerializedName("DESCR_COR_VEICULO")
	private String descrCorVeiculo;
	
	@SerializedName("DESCR_MARCA_VEICULO")
	private String descrMarcaVeiculo;
	
	@SerializedName("DESCR_OCORRENCIA_VEICULO")
	private String descrOcorrenciaVeiculo;
	
	@SerializedName("DESCR_TIPOLOCAL")
	private String descrTipolocal;
	
	@Override
	public String toString() {
		return "Veiculo [contVeiculo=" + contVeiculo + ", placaVeiculo="
				+ placaVeiculo + ", chassisVeiculo=" + chassisVeiculo
				+ ", codRenavam=" + codRenavam + ", idTipoVeiculo="
				+ idTipoVeiculo + ", idCombustivel=" + idCombustivel
				+ ", idCorVeiculo=" + idCorVeiculo + ", idMarcaVeiculo="
				+ idMarcaVeiculo + ", cidadeVeiculo=" + cidadeVeiculo
				+ ", ufVeiculo=" + ufVeiculo + ", nomeProprietarioVeiculo="
				+ nomeProprietarioVeiculo + ", idOcorrenciaVeiculo="
				+ idOcorrenciaVeiculo + ", idTipolocal=" + idTipolocal
				+ ", flagSeguroVeiculo=" + flagSeguroVeiculo
				+ ", seguradoraVeiculo=" + seguradoraVeiculo
				+ ", apoliceVeiculo=" + apoliceVeiculo + ", logotipoVeiculo="
				+ logotipoVeiculo + ", flagEscoltaVeiculo="
				+ flagEscoltaVeiculo + ", tempoAbandonoVeiculo="
				+ tempoAbandonoVeiculo + ", documentosLevados="
				+ documentosLevados + ", anoFabricacao=" + anoFabricacao
				+ ", anoModelo=" + anoModelo + ", observacaoVeiculo="
				+ observacaoVeiculo + ", chassisRemarcado=" + chassisRemarcado
				+ ", flagBuscaRdo=" + flagBuscaRdo + ", flagBuscaDetran="
				+ flagBuscaDetran + ", contPessoa=" + contPessoa
				+ ", codMunicipio=" + codMunicipio + ", flagPlacaAntiga="
				+ flagPlacaAntiga + ", descrTipoVeiculo=" + descrTipoVeiculo
				+ ", descrCombustivel=" + descrCombustivel
				+ ", descrCorVeiculo=" + descrCorVeiculo
				+ ", descrMarcaVeiculo=" + descrMarcaVeiculo
				+ ", descrOcorrenciaVeiculo=" + descrOcorrenciaVeiculo
				+ ", descrTipolocal=" + descrTipolocal + "]";
	}
	public String getContVeiculo() {
		return contVeiculo;
	}
	public void setContVeiculo(String contVeiculo) {
		this.contVeiculo = contVeiculo;
	}
	public String getPlacaVeiculo() {
		return placaVeiculo;
	}
	public void setPlacaVeiculo(String placaVeiculo) {
		this.placaVeiculo = placaVeiculo;
	}
	public String getChassisVeiculo() {
		return chassisVeiculo;
	}
	public void setChassisVeiculo(String chassisVeiculo) {
		this.chassisVeiculo = chassisVeiculo;
	}
	public String getCodRenavam() {
		return codRenavam;
	}
	public void setCodRenavam(String codRenavam) {
		this.codRenavam = codRenavam;
	}
	public String getIdTipoVeiculo() {
		return idTipoVeiculo;
	}
	public void setIdTipoVeiculo(String idTipoVeiculo) {
		this.idTipoVeiculo = idTipoVeiculo;
	}
	public String getIdCombustivel() {
		return idCombustivel;
	}
	public void setIdCombustivel(String idCombustivel) {
		this.idCombustivel = idCombustivel;
	}
	public String getIdCorVeiculo() {
		return idCorVeiculo;
	}
	public void setIdCorVeiculo(String idCorVeiculo) {
		this.idCorVeiculo = idCorVeiculo;
	}
	public String getIdMarcaVeiculo() {
		return idMarcaVeiculo;
	}
	public void setIdMarcaVeiculo(String idMarcaVeiculo) {
		this.idMarcaVeiculo = idMarcaVeiculo;
	}
	public String getCidadeVeiculo() {
		return cidadeVeiculo;
	}
	public void setCidadeVeiculo(String cidadeVeiculo) {
		this.cidadeVeiculo = cidadeVeiculo;
	}
	public String getUfVeiculo() {
		return ufVeiculo;
	}
	public void setUfVeiculo(String ufVeiculo) {
		this.ufVeiculo = ufVeiculo;
	}
	public String getNomeProprietarioVeiculo() {
		return nomeProprietarioVeiculo;
	}
	public void setNomeProprietarioVeiculo(String nomeProprietarioVeiculo) {
		this.nomeProprietarioVeiculo = nomeProprietarioVeiculo;
	}
	public String getIdOcorrenciaVeiculo() {
		return idOcorrenciaVeiculo;
	}
	public void setIdOcorrenciaVeiculo(String idOcorrenciaVeiculo) {
		this.idOcorrenciaVeiculo = idOcorrenciaVeiculo;
	}
	public String getIdTipolocal() {
		return idTipolocal;
	}
	public void setIdTipolocal(String idTipolocal) {
		this.idTipolocal = idTipolocal;
	}
	public String getFlagSeguroVeiculo() {
		return flagSeguroVeiculo;
	}
	public void setFlagSeguroVeiculo(String flagSeguroVeiculo) {
		this.flagSeguroVeiculo = flagSeguroVeiculo;
	}
	public String getSeguradoraVeiculo() {
		return seguradoraVeiculo;
	}
	public void setSeguradoraVeiculo(String seguradoraVeiculo) {
		this.seguradoraVeiculo = seguradoraVeiculo;
	}
	public String getApoliceVeiculo() {
		return apoliceVeiculo;
	}
	public void setApoliceVeiculo(String apoliceVeiculo) {
		this.apoliceVeiculo = apoliceVeiculo;
	}
	public String getLogotipoVeiculo() {
		return logotipoVeiculo;
	}
	public void setLogotipoVeiculo(String logotipoVeiculo) {
		this.logotipoVeiculo = logotipoVeiculo;
	}
	public String getFlagEscoltaVeiculo() {
		return flagEscoltaVeiculo;
	}
	public void setFlagEscoltaVeiculo(String flagEscoltaVeiculo) {
		this.flagEscoltaVeiculo = flagEscoltaVeiculo;
	}
	public String getTempoAbandonoVeiculo() {
		return tempoAbandonoVeiculo;
	}
	public void setTempoAbandonoVeiculo(String tempoAbandonoVeiculo) {
		this.tempoAbandonoVeiculo = tempoAbandonoVeiculo;
	}
	public String getDocumentosLevados() {
		return documentosLevados;
	}
	public void setDocumentosLevados(String documentosLevados) {
		this.documentosLevados = documentosLevados;
	}
	public String getAnoFabricacao() {
		return anoFabricacao;
	}
	public void setAnoFabricacao(String anoFabricacao) {
		this.anoFabricacao = anoFabricacao;
	}
	public String getAnoModelo() {
		return anoModelo;
	}
	public void setAnoModelo(String anoModelo) {
		this.anoModelo = anoModelo;
	}
	public String getObservacaoVeiculo() {
		return observacaoVeiculo;
	}
	public void setObservacaoVeiculo(String observacaoVeiculo) {
		this.observacaoVeiculo = observacaoVeiculo;
	}
	public String getChassisRemarcado() {
		return chassisRemarcado;
	}
	public void setChassisRemarcado(String chassisRemarcado) {
		this.chassisRemarcado = chassisRemarcado;
	}
	public String getFlagBuscaRdo() {
		return flagBuscaRdo;
	}
	public void setFlagBuscaRdo(String flagBuscaRdo) {
		this.flagBuscaRdo = flagBuscaRdo;
	}
	public String getFlagBuscaDetran() {
		return flagBuscaDetran;
	}
	public void setFlagBuscaDetran(String flagBuscaDetran) {
		this.flagBuscaDetran = flagBuscaDetran;
	}
	public String getContPessoa() {
		return contPessoa;
	}
	public void setContPessoa(String contPessoa) {
		this.contPessoa = contPessoa;
	}
	public String getCodMunicipio() {
		return codMunicipio;
	}
	public void setCodMunicipio(String codMunicipio) {
		this.codMunicipio = codMunicipio;
	}
	public String getFlagPlacaAntiga() {
		return flagPlacaAntiga;
	}
	public void setFlagPlacaAntiga(String flagPlacaAntiga) {
		this.flagPlacaAntiga = flagPlacaAntiga;
	}
	public String getDescrTipoVeiculo() {
		return descrTipoVeiculo;
	}
	public void setDescrTipoVeiculo(String descrTipoVeiculo) {
		this.descrTipoVeiculo = descrTipoVeiculo;
	}
	public String getDescrCombustivel() {
		return descrCombustivel;
	}
	public void setDescrCombustivel(String descrCombustivel) {
		this.descrCombustivel = descrCombustivel;
	}
	public String getDescrCorVeiculo() {
		return descrCorVeiculo;
	}
	public void setDescrCorVeiculo(String descrCorVeiculo) {
		this.descrCorVeiculo = descrCorVeiculo;
	}
	public String getDescrMarcaVeiculo() {
		return descrMarcaVeiculo;
	}
	public void setDescrMarcaVeiculo(String descrMarcaVeiculo) {
		this.descrMarcaVeiculo = descrMarcaVeiculo;
	}
	public String getDescrOcorrenciaVeiculo() {
		return descrOcorrenciaVeiculo;
	}
	public void setDescrOcorrenciaVeiculo(String descrOcorrenciaVeiculo) {
		this.descrOcorrenciaVeiculo = descrOcorrenciaVeiculo;
	}
	public String getDescrTipolocal() {
		return descrTipolocal;
	}
	public void setDescrTipolocal(String descrTipolocal) {
		this.descrTipolocal = descrTipolocal;
	}

	
	
}
