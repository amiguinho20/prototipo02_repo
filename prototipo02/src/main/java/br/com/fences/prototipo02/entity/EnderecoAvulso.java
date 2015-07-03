package br.com.fences.prototipo02.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class EnderecoAvulso implements Serializable{

	private static final long serialVersionUID = 6708411077276497587L;
	
	@SerializedName("_id")
	private String id;
	
	private String razaoSocial;
	private String logradouro;
	private String numero;
	private String bairro;
	private String cep;
	private String cidade;
	private String uf;

	private String tipo; //1)mercado 2)galpao 3)deposito 4)desmanche
	private String googleLatitude;
	private String googleLongitude; 
	private String googleGeocoderStatus; 
	private String indicadorAtivo;
	private String ultimaAtualizacao;
	
	
	@Override
	public String toString() {
		return "EnderecoAvulso [id=" + id + ", razaoSocial=" + razaoSocial
				+ ", logradouro=" + logradouro + ", numero=" + numero
				+ ", bairro=" + bairro + ", cep=" + cep + ", cidade=" + cidade
				+ ", uf=" + uf + ", tipo=" + tipo + ", googleLatitude="
				+ googleLatitude + ", googleLongitude=" + googleLongitude
				+ ", googleGeocoderStatus=" + googleGeocoderStatus
				+ ", indicadorAtivo=" + indicadorAtivo + ", ultimaAtualizacao="
				+ ultimaAtualizacao + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	public String getIndicadorAtivo() {
		return indicadorAtivo;
	}
	public void setIndicadorAtivo(String indicadorAtivo) {
		this.indicadorAtivo = indicadorAtivo;
	}
	public String getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}
	public void setUltimaAtualizacao(String ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

}
