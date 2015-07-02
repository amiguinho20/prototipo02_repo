package br.com.fences.prototipo02.mb;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Messages;
import org.omnifaces.util.Messages.Message;
import org.primefaces.model.LazyDataModel;

import br.com.fences.prototipo02.dao.EnderecoAvulsoDAO;
import br.com.fences.prototipo02.entity.EnderecoAvulso;
import br.com.fences.prototipo02.entity.Filtro;
import br.com.fences.prototipo02.entity.Ocorrencia;
import br.com.fences.prototipo02.util.EnderecoAvulsoLazyDataModel;
import br.com.fences.prototipo02.util.EnderecoGeocodeUtil;
import br.com.fences.prototipo02.util.Verificador;

@Named
@ViewScoped
public class EnderecoAvulsoMB implements Serializable{

	private static final long serialVersionUID = 1866941789765596632L;

	@Inject
	private EnderecoAvulsoDAO enderecoAvulsoDAO;
	
	@Inject
	private Filtro filtro;
	
//	@Inject
//	private FiltroMapa filtroMapa;
	
	private Integer contagem;
	private LazyDataModel<EnderecoAvulso> enderecosAvulsosResultadoLazy;
	private List<EnderecoAvulso> enderecosAvulsosSelecionados;

//	private String centroMapa = "-23.538419906917593, -46.63483794999996";
//	private MapModel geoModel;

	//-- inclusao/alteracao
	private EnderecoAvulso enderecoAvulso = new EnderecoAvulso();
	
	private boolean detalhe = false;

	@PostConstruct
	private void init() {	
		pesquisar();
	} 
	
	public void pesquisar(){
		setEnderecosAvulsosResultadoLazy(new EnderecoAvulsoLazyDataModel(enderecoAvulsoDAO, filtro));
		setContagem(getEnderecosAvulsosResultadoLazy().getRowCount());

		limparMapa();
	}

	public void limpar(){
		filtro = new Filtro();
		limparMapa();
		pesquisar();  
	}
	
	public String incluir(){
		if (enderecoAvulso != null && !Verificador.isValorado(enderecoAvulso.getId()))
		{ 
			enderecoAvulsoDAO.adicionar(enderecoAvulso);
			enderecoAvulso = new EnderecoAvulso();
			detalhe = false; 
			Messages.addGlobalInfo("Inclusão realizada com sucesso.");
		}
		return "enderecoAvulso";
	}
	
	public String alterar(){
		if (enderecoAvulso != null && Verificador.isValorado(enderecoAvulso.getId()))
		{   
			enderecoAvulsoDAO.substituir(enderecoAvulso);
			enderecoAvulso = new EnderecoAvulso();
			detalhe = false;
			Messages.addGlobalInfo("Alteração realizada com sucesso.");	
		}
		return "enderecoAvulso";
	}
	  
	public void limparMapa(){
//		geoModel = null;
	}


	
	private void exibirNoMapa(Ocorrencia ocorrencia)
	{
//		LatLng latLng = null;
//		if (latLng != null)
//		{
//			Marker marcaNoMapa = new Marker(latLng, "descricao");
//				
//			geoModel.addOverlay(marcaNoMapa);
//		}	
	}
	
	public String formatarEndereco(Ocorrencia ocorrencia)
	{
		String enderecoFormatado = EnderecoGeocodeUtil.formatarEndereco(ocorrencia);
		return enderecoFormatado;
	}
	
	public Integer getContagem() {
		return contagem;
	}

	public void setContagem(Integer contagem) {
		this.contagem = contagem;
	}


//	public String getCentroMapa() {
//		return centroMapa;
//	}
//
//	public void setCentroMapa(String centroMapa) {
//		this.centroMapa = centroMapa;
//	}
//
//	public MapModel getGeoModel() {
//		return geoModel;
//	}
//
//	public void setGeoModel(MapModel geoModel) {
//		this.geoModel = geoModel;
//	}

	public Filtro getFiltro() {
		return filtro;
	}

	public void setFiltro(Filtro filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<EnderecoAvulso> getEnderecosAvulsosResultadoLazy() {
		return enderecosAvulsosResultadoLazy;
	}

	public void setEnderecosAvulsosResultadoLazy(
			LazyDataModel<EnderecoAvulso> enderecosAvulsosResultadoLazy) {
		this.enderecosAvulsosResultadoLazy = enderecosAvulsosResultadoLazy;
	}

	public List<EnderecoAvulso> getEnderecosAvulsosSelecionados() {
		return enderecosAvulsosSelecionados;
	}

	public void setEnderecosAvulsosSelecionados(
			List<EnderecoAvulso> enderecosAvulsosSelecionados) {
		this.enderecosAvulsosSelecionados = enderecosAvulsosSelecionados;
	}

	public EnderecoAvulso getEnderecoAvulso() {
		return enderecoAvulso;
	}

	public void setEnderecoAvulso(EnderecoAvulso enderecoAvulso) {
		this.enderecoAvulso = enderecoAvulso;
	}

	public boolean isDetalhe() {
		return detalhe;
	}

	public void setDetalhe(boolean detalhe) {
		this.detalhe = detalhe;
	}

//	public FiltroMapa getFiltroMapa() {
//		return filtroMapa;
//	}
//
//	public void setFiltroMapa(FiltroMapa filtroMapa) {
//		this.filtroMapa = filtroMapa;
//	}

	
}
