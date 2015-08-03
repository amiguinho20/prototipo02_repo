package br.com.fences.prototipo02.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.mongodb.BasicDBObject;

import br.com.fences.prototipo02.util.FormatarData;
import br.com.fences.prototipo02.util.Verificador;

@Named
//@javax.faces.view.ViewScoped
@SessionScoped
public class FiltroRouboCargaReceptacao implements Serializable{ 

	private static final long serialVersionUID = -7701810214532312594L;
	
	private DateFormat df = FormatarData.getAnoMesDiaContatenado();
	
	
	private Date dataInicial;
	private Date dataFinal;
	private String flagFlagrante;
	private String complemento;
	private String natureza;
	
	public void limpar()
	{
		setDataInicial(null);
		setDataFinal(null);
		setFlagFlagrante("");
		setComplemento("");
		setNatureza("");
	}
	
	public String getDataInicialString(){
		String dataFormatada = "";
		if (dataInicial != null)
		{ 
			dataFormatada = df.format(dataInicial) + "000000";
		}
		return dataFormatada;
	}
	
	public String getDataFinalString(){
		String dataFormatada = "";
		if (dataFinal != null)
		{
			dataFormatada = df.format(dataFinal) + "999999";
		}
		return dataFormatada;
	}

	public BasicDBObject montarPesquisa()
	{
		BasicDBObject pesquisa = new BasicDBObject();
		
		if (dataInicial != null || dataFinal != null)
		{
			BasicDBObject periodo = new BasicDBObject();
			if (dataInicial != null)
			{
				periodo.put("$gt", getDataInicialString());
			}
			if (dataFinal != null)
			{
				periodo.put("$lt", getDataFinalString());
			}
			pesquisa.put("DATAHORA_REGISTRO_BO", periodo);
		}
		if (Verificador.isValorado(flagFlagrante))
		{
			pesquisa.put("FLAG_FLAGRANTE", flagFlagrante);
		}
		if (Verificador.isValorado(complemento))
		{
			if (complemento.equalsIgnoreCase("A"))
			{	//-- ocorrencias que nao possuem complementares
				pesquisa.put("CUSTOM_COMPLEMENTAR_LOCALIZACAO", new BasicDBObject("$exists", false));
			}
			else if (complemento.equalsIgnoreCase("B"))
			{	//-- ocorrencias que possuem complementares
				pesquisa.put("CUSTOM_COMPLEMENTAR_LOCALIZACAO", new BasicDBObject("$exists", true));
			}
			else if (complemento.equalsIgnoreCase("C"))
			{	//-- apenas ocorrencias complementares 
				pesquisa.put("ANO_REFERENCIA_BO", new BasicDBObject("$exists", true));
				pesquisa.put("NATUREZA.ID_OCORRENCIA", "40");
				pesquisa.put("NATUREZA.ID_ESPECIE", "40");
			}
		}		
		if (Verificador.isValorado(natureza))
		{
			if (natureza.equalsIgnoreCase("C"))
			{	
				pesquisa.put("NATUREZA.ID_NATUREZA", 
					new BasicDBObject("$nin", 
						Arrays.asList("180A", "180B", "180C") ));
			}
			else if (natureza.equalsIgnoreCase("R"))
			{	
				pesquisa.put("NATUREZA.ID_NATUREZA", 
					new BasicDBObject("$in", 
							Arrays.asList("180A", "180B", "180C") ));
			}
		}		
		return pesquisa;
	}
	
	
	
	@Override
	public String toString() {
		return montarPesquisa().toString();
	}

	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	

	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getFlagFlagrante() {
		return flagFlagrante;
	}

	public void setFlagFlagrante(String flagFlagrante) {
		this.flagFlagrante = flagFlagrante;
	}

	public String getNatureza() {
		return natureza;
	}

	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}

	
	
}