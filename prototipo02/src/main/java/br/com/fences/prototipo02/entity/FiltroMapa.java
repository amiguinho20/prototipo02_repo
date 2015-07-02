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
	
	
	
}
