package br.com.fences.prototipo02.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Ocorrencia implements Serializable{
	
	private static final long serialVersionUID = 883485166952224851L;
	
	@SerializedName("_id")
	private String id; //-- do banco de dados
	
	@SerializedName("ID_DELEGACIA")
	private String idDelegacia;
	
	@SerializedName("ANO_BO")
	private String anoBo;
	
	@SerializedName("NUM_BO")
	private String numBo;
	
	@SerializedName("NOME_DELEGACIA")
	private String nomeDelegacia;
	
	@SerializedName("DELEG_REFERENCIA_BO")
	private String delegReferenciaBo;
	
	@SerializedName("ANO_REFERENCIA_BO")
	private String anoReferenciaBo;
	
	@SerializedName("NUM_REFERENCIA_BO")
	private String numReferenciaBo;
	
	@SerializedName("NOME_DELEGACIA_REFERENCIA_BO")
	private String nomeDelegaciaReferenciaBo;
	
	@SerializedName("DATA_OCORRENCIA_BO")
	private String dataOcorrenciaBo;
	
	@SerializedName("DATAHORA_REGISTRO_BO")
	private String datahoraRegistroBo;
	
	@SerializedName("FLAG_FLAGRANTE")
	private String flagFlagrante;
	
	@SerializedName("LOGRADOURO")
	private String logradouro;
	
	@SerializedName("NUMERO_LOGRADOURO")
	private String numeroLogradouro;
	
	@SerializedName("COMPLEMENTO")
	private String complemento; //--complemento do logradouro
	
	@SerializedName("CEP")
	private String cep;
	
	@SerializedName("BAIRRO")
	private String bairro;
	
	@SerializedName("CIDADE")
	private String cidade;
	
	@SerializedName("ID_UF")
	private String idUf;
	
	@SerializedName("LATITUDE")
	private String latitude;
	
	@SerializedName("LONGITUDE")
	private String longitude;
	
	@SerializedName("GOOGLE_LATITUDE")
	private String googleLatitude;
	
	@SerializedName("GOOGLE_LONGITUDE")
	private String googleLongitude; 
	
	@SerializedName("GOOGLE_GEOCODER_STATUS")
	private String googleGeocoderStatus; 
	
	@SerializedName("CUSTOM_COMPLEMENTAR_LOCALIZACAO")
	private String customComplementarLocalizacao;
	
	@SerializedName("NATUREZA")
	private List<Natureza> naturezas = new ArrayList<>();
	
	@SerializedName("VEICULO")
	private List<Veiculo> veiculos = new ArrayList<>();
	
	@SerializedName("CARGA")
	private List<Carga> cargas = new ArrayList<>();
	
	@SerializedName("COMPLEMENTAR")
	private transient List<Ocorrencia> complementares = new ArrayList<>(); //--ocorrencias complementares
	
	@Override
	public String toString() {
		return "Ocorrencia [id=" + id + ", idDelegacia=" + idDelegacia
				+ ", anoBo=" + anoBo + ", numBo=" + numBo + ", nomeDelegacia="
				+ nomeDelegacia + ", delegReferenciaBo=" + delegReferenciaBo
				+ ", anoReferenciaBo=" + anoReferenciaBo + ", numReferenciaBo="
				+ numReferenciaBo + ", nomeDelegaciaReferenciaBo="
				+ nomeDelegaciaReferenciaBo + ", dataOcorrenciaBo="
				+ dataOcorrenciaBo + ", datahoraRegistroBo="
				+ datahoraRegistroBo + ", flagFlagrante=" + flagFlagrante
				+ ", logradouro=" + logradouro + ", numeroLogradouro="
				+ numeroLogradouro + ", complemento=" + complemento + ", cep="
				+ cep + ", bairro=" + bairro + ", cidade=" + cidade + ", idUf="
				+ idUf + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", googleLatitude=" + googleLatitude + ", googleLongitude="
				+ googleLongitude + ", googleGeocoderStatus="
				+ googleGeocoderStatus + ", customComplementarLocalizacao="
				+ customComplementarLocalizacao + ", naturezas=" + naturezas
				+ ", veiculos=" + veiculos + ", cargas=" + cargas
				+ ", complementares=" + complementares + "]";
	}
	public String getIdDelegacia() {
		return idDelegacia;
	}
	public void setIdDelegacia(String idDelegacia) {
		this.idDelegacia = idDelegacia;
	}
	public String getAnoBo() {
		return anoBo;
	}
	public void setAnoBo(String anoBo) {
		this.anoBo = anoBo;
	}
	public String getNumBo() {
		return numBo;
	}
	public void setNumBo(String numBo) {
		this.numBo = numBo;
	}
	public String getDelegReferenciaBo() {
		return delegReferenciaBo;
	}
	public void setDelegReferenciaBo(String delegReferenciaBo) {
		this.delegReferenciaBo = delegReferenciaBo;
	}
	public String getAnoReferenciaBo() {
		return anoReferenciaBo;
	}
	public void setAnoReferenciaBo(String anoReferenciaBo) {
		this.anoReferenciaBo = anoReferenciaBo;
	}
	public String getNumReferenciaBo() {
		return numReferenciaBo;
	}
	public void setNumReferenciaBo(String numReferenciaBo) {
		this.numReferenciaBo = numReferenciaBo;
	}
	public String getDataOcorrenciaBo() {
		return dataOcorrenciaBo;
	}
	public void setDataOcorrenciaBo(String dataOcorrenciaBo) {
		this.dataOcorrenciaBo = dataOcorrenciaBo;
	}
	public String getDatahoraRegistroBo() {
		return datahoraRegistroBo;
	}
	public void setDatahoraRegistroBo(String datahoraRegistroBo) {
		this.datahoraRegistroBo = datahoraRegistroBo;
	}
	public String getFlagFlagrante() {
		return flagFlagrante;
	}
	public void setFlagFlagrante(String flagFlagrante) {
		this.flagFlagrante = flagFlagrante;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getNumeroLogradouro() {
		return numeroLogradouro;
	}
	public void setNumeroLogradouro(String numeroLogradouro) {
		this.numeroLogradouro = numeroLogradouro;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getIdUf() {
		return idUf;
	}
	public void setIdUf(String idUf) {
		this.idUf = idUf;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public List<Natureza> getNaturezas() {
		return naturezas;
	}
	public void setNaturezas(List<Natureza> naturezas) {
		this.naturezas = naturezas;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public List<Ocorrencia> getComplementares() {
		return complementares;
	}
	public void setComplementares(
			List<Ocorrencia> complementares) {
		this.complementares = complementares;
	}
	public String getNomeDelegacia() {
		return nomeDelegacia;
	}
	public void setNomeDelegacia(String nomeDelegacia) {
		this.nomeDelegacia = nomeDelegacia;
	}
	public String getNomeDelegaciaReferenciaBo() {
		return nomeDelegaciaReferenciaBo;
	}
	public void setNomeDelegaciaReferenciaBo(String nomeDelegaciaReferenciaBo) {
		this.nomeDelegaciaReferenciaBo = nomeDelegaciaReferenciaBo;
	}
	public List<Veiculo> getVeiculos() {
		return veiculos;
	}
	public void setVeiculos(List<Veiculo> veiculos) {
		this.veiculos = veiculos;
	}
	public List<Carga> getCargas() {
		return cargas;
	}
	public void setCargas(List<Carga> cargas) {
		this.cargas = cargas;
	}
	public String getGoogleLatitude() {
		return googleLatitude;
	}
	public void setGoogleLatitude(String googleLatitude) {
		this.googleLatitude = googleLatitude;
	}
	public String getGoogleLongitude() {
		return googleLongitude;
	}
	public void setGoogleLongitude(String googleLongitude) {
		this.googleLongitude = googleLongitude;
	}
	public String getGoogleGeocoderStatus() {
		return googleGeocoderStatus;
	}
	public void setGoogleGeocoderStatus(String googleGeocoderStatus) {
		this.googleGeocoderStatus = googleGeocoderStatus;
	}
	public String getCustomComplementarLocalizacao() {
		return customComplementarLocalizacao;
	}
	public void setCustomComplementarLocalizacao(
			String customComplementarLocalizacao) {
		this.customComplementarLocalizacao = customComplementarLocalizacao;
	}

	
	
}
