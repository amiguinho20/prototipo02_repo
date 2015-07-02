package br.com.fences.prototipo02.mb;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Messages;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.chart.PieChartModel;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

import br.com.fences.prototipo02.dao.RdoRouboCargaReceptacaoDAO;
import br.com.fences.prototipo02.entity.Filtro;
import br.com.fences.prototipo02.entity.FiltroMapa;
import br.com.fences.prototipo02.entity.Natureza;
import br.com.fences.prototipo02.entity.Ocorrencia;
import br.com.fences.prototipo02.exception.GoogleLimiteAtingidoRuntimeException;
import br.com.fences.prototipo02.exception.GoogleZeroResultsRuntimeException;
import br.com.fences.prototipo02.util.EnderecoGeocodeUtil;
import br.com.fences.prototipo02.util.FormatarData;
import br.com.fences.prototipo02.util.RdoRouboCargaReceptacaoLazyDataModel;
import br.com.fences.prototipo02.util.Verificador;

@Named
@ViewScoped
public class RdoRouboCargaReceptacaoMB implements Serializable{

	private static final long serialVersionUID = 1866941789765596632L;

	@Inject
	private RdoRouboCargaReceptacaoDAO rdoRouboCargaReceptacaoDAO;
	
	@Inject
	private Filtro filtro;
	
	@Inject
	private FiltroMapa filtroMapa;
	
	private Integer contagem;
	private LazyDataModel<Ocorrencia> ocorrenciasResultadoLazy;
	private List<Ocorrencia> ocorrenciasSelecionadas;

	private String centroMapa = "-23.538419906917593, -46.63483794999996";
	private MapModel geoModel;
	
	private enum TipoMarcador { COMPLEMENTAR, RECEPTACAO, ROUBO_CARGA }
	
	//--graficos
	private PieChartModel graficoPizzaNatureza;
	private PieChartModel graficoPizzaFlagrante;
	private PieChartModel graficoPizzaAno;
	private PieChartModel graficoPizzaComplementar;
	
	@PostConstruct
	private void init() {	
		pesquisar();
	} 
	
	public void pesquisar(){
		setOcorrenciasResultadoLazy(new RdoRouboCargaReceptacaoLazyDataModel(rdoRouboCargaReceptacaoDAO, filtro));
		setContagem(getOcorrenciasResultadoLazy().getRowCount());
		montarGraficoPizzaNatureza();
		montarGraficoPizzaAno();
		montarGraficoPizzaFlagrante();
		montarGraficoPizzaComplementar();
		limparMapa();
	}

	public void limpar(){
		filtro = new Filtro();
		limparMapa();
		pesquisar();
	}
	
	public void limparMapa(){
		geoModel = null;
	}

	private void montarGraficoPizzaNatureza()
	{
		graficoPizzaNatureza = new PieChartModel();
        
		graficoPizzaNatureza.set("Roubo de carga", 540);
		graficoPizzaNatureza.set("Receptação", 325);
         
		graficoPizzaNatureza.setTitle("Natureza");
		graficoPizzaNatureza.setLegendPosition("w");
	}
	
	public void montarGraficoPizzaFlagrante()
	{
		graficoPizzaFlagrante = new PieChartModel();
		graficoPizzaFlagrante.setTitle("Flagrante");
		graficoPizzaFlagrante.setLegendPosition("w"); 
		
		Map<String, Integer> resultados = rdoRouboCargaReceptacaoDAO.agregarPorFlagrante(filtro);
		for (Map.Entry<String, Integer> entry : resultados.entrySet() )
		{
			graficoPizzaFlagrante.set(entry.getKey() + " : " + entry.getValue(), entry.getValue());
		}
		
		//graficoPizzaFlagrante.setFill(false);
		graficoPizzaFlagrante.setShowDataLabels(true);
		//graficoPizzaFlagrante.setDiameter(150);
	}
	
	public void montarGraficoPizzaAno()
	{
		graficoPizzaAno = new PieChartModel();
		graficoPizzaAno.setTitle("Ano");
		graficoPizzaAno.setLegendPosition("w"); 
		
		Map<String, Integer> resultados = rdoRouboCargaReceptacaoDAO.agregarPorAno(filtro);
		for (Map.Entry<String, Integer> entry : resultados.entrySet() )
		{
			graficoPizzaAno.set(entry.getKey() + " : " + entry.getValue(), entry.getValue());
		}
		
		//graficoPizzaAno.setFill(false);
		graficoPizzaAno.setShowDataLabels(true);
		//graficoPizzaFlagrante.setDiameter(150);
	}

	public void montarGraficoPizzaComplementar()
	{
		graficoPizzaComplementar = new PieChartModel();
		graficoPizzaComplementar.setTitle("Ocorrências que possuem complemento de recuperação/localização");
		graficoPizzaComplementar.setLegendPosition("w"); 
		
		Map<String, Integer> resultados = rdoRouboCargaReceptacaoDAO.agregarPorComplementar(filtro);
		for (Map.Entry<String, Integer> entry : resultados.entrySet() )
		{
			graficoPizzaComplementar.set(entry.getKey() + " : " + entry.getValue(), entry.getValue());
		}
		
		//graficoPizzaAno.setFill(false);
		graficoPizzaComplementar.setShowDataLabels(true);
		//graficoPizzaFlagrante.setDiameter(150);
	}

	
	public void exibirRegistrosSelecionadosNoMapa()
	{
		geoModel = new DefaultMapModel();  	
		
		//-- caso nao exista geocode, pesquisa no google e grava no banco.
		for (Ocorrencia ocorrencia : ocorrenciasSelecionadas) 
		{
			
			List<Ocorrencia> paiMaisFilhos = new ArrayList<>();
			paiMaisFilhos.add(ocorrencia);
			paiMaisFilhos.addAll(ocorrencia.getComplementares());
			
			for (Ocorrencia ocorAux : paiMaisFilhos)
			{
				//-- nao processar geocode previo com ZERO_RESULTS
				if (Verificador.isValorado(ocorAux.getGoogleGeocoderStatus()))
				{
					if (ocorAux.getGoogleGeocoderStatus().equals("ZERO_RESULTS"))
					{
						continue;
					}
				}
				
				LatLng latLng = EnderecoGeocodeUtil.verificarExistenciaPreviaDeGeocode(ocorAux);
				if (latLng == null)
				{
					try 
					{
						//-- consulta geoCode no google
						latLng = EnderecoGeocodeUtil.gerarGeocode(ocorAux);
					}
					catch (GoogleLimiteAtingidoRuntimeException e)
					{
						Messages.create("Erro na pesquisa do Geocode no google").warn().detail(e.getMessage()).add();
						return;
					}
					catch (GoogleZeroResultsRuntimeException e)
					{
						ocorAux.setGoogleGeocoderStatus("ZERO_RESULTS");
						//-- atualiza no banco
						rdoRouboCargaReceptacaoDAO.substituir(ocorAux);
						System.out.println("atualizado como ZERO_RESULTS [" + formatarOcorrencia(ocorAux) + " " + formatarEndereco(ocorAux) + "]");
					}
					if (latLng != null)
					{
						ocorAux.setGoogleLatitude(Double.toString(latLng.getLat()));
						ocorAux.setGoogleLongitude(Double.toString(latLng.getLng()));
						ocorAux.setGoogleGeocoderStatus("OK");
						//-- atualiza no banco
						rdoRouboCargaReceptacaoDAO.substituir(ocorAux);
						System.out.println("atualizado como OK [" + formatarOcorrencia(ocorAux) + " " + formatarEndereco(ocorAux) + "]");
					}
				}
			}
		}
 
		//-- exibe no mapa apenas ocorrencias que contem geoCode pre-processado
		for (Ocorrencia ocorrencia : ocorrenciasSelecionadas) 
		{
			if (filtroMapa.isExibirApenasLinhas())
			{
				if (!verificarExibicaoDeLinha(ocorrencia))
				{
					continue;
				}
			}
			
			exibirNoMapa(ocorrencia);
			for (Ocorrencia complementar : ocorrencia.getComplementares())
			{
				
				//--- add linha entre pai e filho
				if (filtroMapa.isExibirComplementar())
				{
					boolean exibir = false;
					for (Natureza natureza : complementar.getNaturezas())
					{
						if (natureza.getIdOcorrencia().equals("40") && natureza.getIdEspecie().equals("40"))
						{
							exibir = true;
							break;
						}
					}
					if (exibir)
					{
						exibirNoMapa(complementar);
						
						LatLng latLngPai = EnderecoGeocodeUtil.verificarExistenciaPreviaDeGeocode(ocorrencia);
						LatLng latLngFilho = EnderecoGeocodeUtil.verificarExistenciaPreviaDeGeocode(complementar);
						
						if (latLngPai != null && latLngFilho != null)
						{
							Polyline polyline = new Polyline();
							polyline.getPaths().add(latLngPai);
							polyline.getPaths().add(latLngFilho);
							
							polyline.setStrokeWeight(5);
							polyline.setStrokeColor("#FF0000");
						    polyline.setStrokeOpacity(0.4);
						    
						    if (geoModel != null)
						    {
						    	geoModel.addOverlay(polyline);
						    }
						}
					}
				}
			}
		}
	}
	
	private boolean verificarExibicaoDeLinha(Ocorrencia ocorrencia)
	{
		boolean exibir = false;
		for (Ocorrencia complementar : ocorrencia.getComplementares())
		{
			boolean nat = false;
			for (Natureza natureza : complementar.getNaturezas())
			{
				if (natureza.getIdOcorrencia().equals("40") && natureza.getIdEspecie().equals("40"))
				{
					nat = true;
					break;
				}
			}
			if (nat)
			{
				LatLng latLngPai = EnderecoGeocodeUtil.verificarExistenciaPreviaDeGeocode(ocorrencia);
				LatLng latLngFilho = EnderecoGeocodeUtil.verificarExistenciaPreviaDeGeocode(complementar);
				if (latLngPai != null && latLngFilho != null)
				{
					if (latLngPai.getLat() != latLngFilho.getLat() && latLngPai.getLng() != latLngFilho.getLng() )
					{
						exibir = true;
					}
				}
			}
		}
		return exibir;
	}
	
	private void exibirNoMapa(Ocorrencia ocorrencia)
	{
		LatLng latLng = EnderecoGeocodeUtil.verificarExistenciaPreviaDeGeocode(ocorrencia);
		if (latLng != null)
		{
			String ocorrenciaFormatada = formatarOcorrencia(ocorrencia);
			String enderecoFormatado = formatarEndereco(ocorrencia);
			
			Enum<TipoMarcador> tipoMarcador = identificarMarcador(ocorrencia);
			String iconeMarcador = recuperarMarcador(tipoMarcador);
			
			if (	(!filtroMapa.isExibirComplementar() && tipoMarcador == TipoMarcador.COMPLEMENTAR)	||
					(!filtroMapa.isExibirReceptacao() && tipoMarcador == TipoMarcador.RECEPTACAO) ||
					(!filtroMapa.isExibirRoubo() && tipoMarcador == TipoMarcador.ROUBO_CARGA)
				)
			{
				return;
			}
			else
			{
				//Marker marcaNoMapa = new Marker(latLng, ocorrenciaFormatada + " - " + enderecoFormatado);
				Marker marcaNoMapa = new Marker(latLng, ocorrenciaFormatada + " - " + enderecoFormatado, null, iconeMarcador);
				
				geoModel.addOverlay(marcaNoMapa);
			}
		}	
	}
	
	private String recuperarMarcador(Enum<TipoMarcador> tipoMarcador)
	{
		String urlMarcador = "http://maps.google.com/mapfiles/ms/micons/red-dot.png"; //-- ROUBO_CARGA
		if (tipoMarcador == TipoMarcador.COMPLEMENTAR)
		{
			urlMarcador = "http://maps.google.com/mapfiles/ms/micons/green-dot.png";
		}
		else if (tipoMarcador == TipoMarcador.RECEPTACAO)
		{
			urlMarcador = "http://maps.google.com/mapfiles/ms/micons/blue-dot.png";
		}
		return urlMarcador;
	}
	
	private Enum<TipoMarcador> identificarMarcador(Ocorrencia ocorrencia)
	{
		TipoMarcador tipoMarcador = TipoMarcador.ROUBO_CARGA;
		if (ocorrencia != null)
		{
			if (ocorrencia.getAnoReferenciaBo() != null && !ocorrencia.getAnoReferenciaBo().trim().isEmpty())
			{
				tipoMarcador = TipoMarcador.COMPLEMENTAR;
			}
			else
			{
				for (Natureza natureza : ocorrencia.getNaturezas())
				{
					List<String> naturezasReceptacao = Arrays.asList("180A", "180B", "180C");
					if (naturezasReceptacao.contains(natureza.getIdNatureza()))
					{
						tipoMarcador = TipoMarcador.RECEPTACAO;
						break;
					}
				}
			}
		}
		return tipoMarcador;
	} 
	

	public String formatarOcorrencia(Ocorrencia ocorrencia)
	{
		StringBuilder ocorrenciaFormatada = new StringBuilder();
		ocorrenciaFormatada.append(ocorrencia.getNumBo()); 
		ocorrenciaFormatada.append("/");
		ocorrenciaFormatada.append(ocorrencia.getAnoBo());
		ocorrenciaFormatada.append("/");
		ocorrenciaFormatada.append(ocorrencia.getNomeDelegacia());
		return ocorrenciaFormatada.toString();
	}
	
	public String formatarReferencia(Ocorrencia ocorrencia)
	{
		StringBuilder ocorrenciaFormatada = new StringBuilder();
		ocorrenciaFormatada.append(ocorrencia.getNumReferenciaBo());
		ocorrenciaFormatada.append("/");
		ocorrenciaFormatada.append(ocorrencia.getAnoReferenciaBo());
		ocorrenciaFormatada.append("/");
		ocorrenciaFormatada.append(ocorrencia.getNomeDelegaciaReferenciaBo());
		return ocorrenciaFormatada.toString();
	}
	
	public String formatarEndereco(Ocorrencia ocorrencia)
	{
		String enderecoFormatado = EnderecoGeocodeUtil.formatarEndereco(ocorrencia);
		return enderecoFormatado;
	}
	
	public String formatarData(String original){
		String formatacao = "";
		if (original != null)
		{
			try
			{
				if (original.length() > 10)
				{
					DateFormat dfOrigem = FormatarData.getAnoMesDiaHoraMinutoSegundoConcatenados();
					DateFormat dfDestino = FormatarData.getDiaMesAnoComBarrasEHoraMinutoSegundoComDoisPontos();
					Date data = dfOrigem.parse(original);
					formatacao = dfDestino.format(data);
				}
				else
				{
					DateFormat dfOrigem = FormatarData.getAnoMesDiaContatenado();
					DateFormat dfDestino = FormatarData.getDiaMesAnoComBarras();
					Date data = dfOrigem.parse(original);
					formatacao = dfDestino.format(data);
				}
			}
			catch (ParseException e)
			{
				//@TODO nao tratar - temporario
				e.printStackTrace();
			}
				
		}
		return formatacao;
	}
	
	public String formatarGeocode(Ocorrencia ocorrencia)
	{
		String formatado = "";
		System.err.println("INIBIDO formatarGeocode");
		/*
		LatLng latLng = EnderecoGeocodeUtil.converter(ocorrencia);
		if (latLng != null)
		{
			formatado = latLng.getLat() + ", " + latLng.getLng();
		}
		*/
		return formatado;
	}
	
	public String formatarComplementar(Ocorrencia complementar)
	{
		return formatarOcorrencia(complementar) + " - " + formatarEndereco(complementar);
	}
	
	public String formatarNatureza(Natureza natureza)
	{
		StringBuilder nat = new StringBuilder();
		if (natureza != null)
		{
			List<String> naturezas = new ArrayList<>();
			if (Verificador.isValorado(natureza.getDescrOcorrencia()))
			{
				naturezas.add(natureza.getDescrOcorrencia());
			}
			if (Verificador.isValorado(natureza.getDescrOcorrencia()))
			{
				naturezas.add(natureza.getDescrEspecie());
			}
			if (Verificador.isValorado(natureza.getDescrOcorrencia()))
			{
				naturezas.add(natureza.getDescrSubespecie());
			}
			if (Verificador.isValorado(natureza.getRubricaNatureza()))
			{
				naturezas.add(natureza.getRubricaNatureza());
			}
			if (Verificador.isValorado(natureza.getDescrConduta()))
			{
				naturezas.add(natureza.getDescrConduta());
			}
			for (String aux : naturezas)
			{
				if (!nat.toString().isEmpty())
				{
					nat.append("; ");
				}
				nat.append(aux);
			}
		}
		return nat.toString();
	}
	
	/**
	 * @deprecated
	 * @param complementares
	 * @return
	 */
	public String formatarOcorrenciaComplementar(List<Ocorrencia> complementares)
	{
		String formatacao = "";
		for (Ocorrencia complementar : complementares)
		{
			formatacao = formatarComplementar(complementar);
			break; //-- apenar uma ocorrencia
		} 
		return formatacao;
	}
	
	public Integer getContagem() {
		return contagem;
	}

	public void setContagem(Integer contagem) {
		this.contagem = contagem;
	}

	public LazyDataModel<Ocorrencia> getOcorrenciasResultadoLazy() {
		return ocorrenciasResultadoLazy;
	}

	public void setOcorrenciasResultadoLazy(
			LazyDataModel<Ocorrencia> ocorrenciasResultadoLazy) {
		this.ocorrenciasResultadoLazy = ocorrenciasResultadoLazy;
	}

	public List<Ocorrencia> getOcorrenciasSelecionadas() {
		return ocorrenciasSelecionadas;
	}

	public void setOcorrenciasSelecionadas(List<Ocorrencia> ocorrenciasSelecionadas) {
		this.ocorrenciasSelecionadas = ocorrenciasSelecionadas;
	}

	public String getCentroMapa() {
		return centroMapa;
	}

	public void setCentroMapa(String centroMapa) {
		this.centroMapa = centroMapa;
	}

	public MapModel getGeoModel() {
		return geoModel;
	}

	public void setGeoModel(MapModel geoModel) {
		this.geoModel = geoModel;
	}

	public Filtro getFiltro() {
		return filtro;
	}

	public void setFiltro(Filtro filtro) {
		this.filtro = filtro;
	}

	public FiltroMapa getFiltroMapa() {
		return filtroMapa;
	}

	public void setFiltroMapa(FiltroMapa filtroMapa) {
		this.filtroMapa = filtroMapa;
	}

	public PieChartModel getGraficoPizzaNatureza() {
		return graficoPizzaNatureza;
	}

	public void setGraficoPizzaNatureza(PieChartModel graficoPizzaNatureza) {
		this.graficoPizzaNatureza = graficoPizzaNatureza;
	}

	public PieChartModel getGraficoPizzaFlagrante() {
		return graficoPizzaFlagrante;
	}

	public void setGraficoPizzaFlagrante(PieChartModel graficoPizzaFlagrante) {
		this.graficoPizzaFlagrante = graficoPizzaFlagrante;
	}

	public PieChartModel getGraficoPizzaAno() {
		return graficoPizzaAno;
	}

	public void setGraficoPizzaAno(PieChartModel graficoPizzaAno) {
		this.graficoPizzaAno = graficoPizzaAno;
	}

	public PieChartModel getGraficoPizzaComplementar() {
		return graficoPizzaComplementar;
	}

	public void setGraficoPizzaComplementar(PieChartModel graficoPizzaComplementar) {
		this.graficoPizzaComplementar = graficoPizzaComplementar;
	}
	
	
	
}
