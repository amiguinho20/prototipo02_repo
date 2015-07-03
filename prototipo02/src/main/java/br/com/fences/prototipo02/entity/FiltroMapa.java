package br.com.fences.prototipo02.entity;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class FiltroMapa implements Serializable{

	private static final long serialVersionUID = -882301340484453507L;

	private boolean exibirRoubo = true;
	private boolean exibirReceptacao = true;
	private boolean exibirComplementar = true;
	private boolean exibirApenasLinhas = false;
	
	private boolean exibirAvulsoMercado = false;
	private boolean exibirAvulsoGalpao = false;
	private boolean exibirAvulsoDeposito = false;
	private boolean exibirAvulsoDesmanche = false;
	
	public boolean isExibirRoubo() {
		return exibirRoubo;
	}
	public void setExibirRoubo(boolean exibirRoubo) {
		this.exibirRoubo = exibirRoubo;
	}
	public boolean isExibirReceptacao() {
		return exibirReceptacao;
	}
	public void setExibirReceptacao(boolean exibirReceptacao) {
		this.exibirReceptacao = exibirReceptacao;
	}
	public boolean isExibirComplementar() {
		return exibirComplementar;
	}
	public void setExibirComplementar(boolean exibirComplementar) {
		this.exibirComplementar = exibirComplementar;
	}
	public boolean isExibirApenasLinhas() {
		return exibirApenasLinhas;
	}
	public void setExibirApenasLinhas(boolean exibirApenasLinhas) {
		this.exibirApenasLinhas = exibirApenasLinhas;
	}
	public boolean isExibirAvulsoMercado() {
		return exibirAvulsoMercado;
	}
	public void setExibirAvulsoMercado(boolean exibirAvulsoMercado) {
		this.exibirAvulsoMercado = exibirAvulsoMercado;
	}
	public boolean isExibirAvulsoGalpao() {
		return exibirAvulsoGalpao;
	}
	public void setExibirAvulsoGalpao(boolean exibirAvulsoGalpao) {
		this.exibirAvulsoGalpao = exibirAvulsoGalpao;
	}
	public boolean isExibirAvulsoDeposito() {
		return exibirAvulsoDeposito;
	}
	public void setExibirAvulsoDeposito(boolean exibirAvulsoDeposito) {
		this.exibirAvulsoDeposito = exibirAvulsoDeposito;
	}
	public boolean isExibirAvulsoDesmanche() {
		return exibirAvulsoDesmanche;
	}
	public void setExibirAvulsoDesmanche(boolean exibirAvulsoDesmanche) {
		this.exibirAvulsoDesmanche = exibirAvulsoDesmanche;
	}
	
	
	
}
