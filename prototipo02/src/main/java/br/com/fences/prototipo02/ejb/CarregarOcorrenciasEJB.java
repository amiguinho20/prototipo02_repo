package br.com.fences.prototipo02.ejb;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import br.com.fences.ocorrenciaentidade.chave.OcorrenciaChave;
import br.com.fences.prototipo02.config.AppConfig;
import br.com.fences.prototipo02.dao.RdoRouboCargaReceptacaoDAO;
import br.com.fences.prototipo02.entity.Ocorrencia;
import br.com.fences.prototipo02.util.FormatarData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

@Stateless
public class CarregarOcorrenciasEJB {
	
	@Inject
	private transient Logger logger;
	
	@Inject
	private RdoRouboCargaReceptacaoDAO rdoRouboCargaReceptacaoDAO;
	
	@Inject
	private AppConfig appConfig;
	
	//@Schedule(hour="6, 8, 10, 12, 14, 16, 18, 20", persistent=false)
	public void processar()
	{
		logger.info("inicio do processo...");
		
		final String host = appConfig.getServerBackendHost();
		final String port = appConfig.getServerBackendPort();
		
		
		//-- recuperar a ultima data de registro
		String ultimaDataRegistro = rdoRouboCargaReceptacaoDAO.pesquisarUltimaDataRegistroNaoComplementar();
		
		Date dtUltimaDataDeRegistro = null;
		try {
			dtUltimaDataDeRegistro = FormatarData.getAnoMesDiaHoraMinutoSegundoConcatenados().parse(ultimaDataRegistro);
		} catch (ParseException e1) {
			String msg = "Erro no rdoRouboCargaReceptacaoDAO.formatarDataRegistroInicial " + 
					" e retornou o erro[" + e1.getMessage() + "]. PROCESSO ABORTADO.";
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		Calendar calUltimaDataDeRegistro = Calendar.getInstance();
		calUltimaDataDeRegistro.setTime(dtUltimaDataDeRegistro);
		calUltimaDataDeRegistro.add(Calendar.SECOND, 1); //-- para nao selecionar o ultimo registro carregado.
		String dataDeRegistroInicial = FormatarData.getAnoMesDiaHoraMinutoSegundoConcatenados().format(calUltimaDataDeRegistro.getTime());
		logger.info("Ultima data de registro adicionado 1 segundo a mais: " + dataDeRegistroInicial);
		
		
		logger.info("ultimadata: " + dataDeRegistroInicial);
		
		//-- montar o periodo de pesquisa
		Calendar calDataCorrente = Calendar.getInstance();
		calDataCorrente.add(Calendar.HOUR_OF_DAY, -2); //-- ajuste para o atraso do servidor
		String dataCorrente = FormatarData.getAnoMesDiaHoraMinutoSegundoConcatenados().format(calDataCorrente.getTime());
		String dataRegistroInicial = dataDeRegistroInicial;
		String dataRegistroFinal = dataCorrente;
		
		//-- pesquisar
		Client client = ClientBuilder.newClient();
		WebTarget selecionarChavesTarget = client
				.target("http://" + host + ":"+ port + "/ocorrenciardobackend/rest/" + 
						"rdoextrair/pesquisarRouboDeCarga/{dataRegistroInicial}/{dataRegistroFinal}");
		Response response = selecionarChavesTarget
				.resolveTemplate("dataRegistroInicial", dataRegistroInicial)
				.resolveTemplate("dataRegistroFinal", dataRegistroFinal)
				.request(MediaType.APPLICATION_JSON)
				.get();
			
		String json = response.readEntity(String.class);
		
		Gson gson = new GsonBuilder().create();
		if (json.contains("codigoErro"))
		{
			Erro erro = gson.fromJson(json, Erro.class);
			String msg = "Erro no WebService pesquisarRouboDeCarga " +
					dataRegistroInicial + "/" + dataRegistroFinal + " e retornou o erro[" +
					erro.getCodigoErro() + "/" + erro.getMensagemErro() + "]. PROCESSO ABORTADO.";
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		
		Type collectionType = new TypeToken<Collection<OcorrenciaChave>>(){}.getType();
		Collection<OcorrenciaChave> ocorrenciasChaves = gson.fromJson(json, collectionType);
		
		//-- iterar
		if (ocorrenciasChaves != null)
		{
			WebTarget consultarOcorrenciaTarget = client.target("http://" + host + ":"+ port + "/ocorrenciardobackend/rest/" + 
						"rdoextrair/consultarOcorrencia/{idDelegacia}/{anoBo}/{numBo}");
			
			logger.info("chaves retornadas: " + ocorrenciasChaves.size());
			
			for (OcorrenciaChave ocorrenciaChave : ocorrenciasChaves)
			{
				ocorrenciaChave.setAnoBo(ocorrenciaChave.getAnoBo());
				response = consultarOcorrenciaTarget
						.resolveTemplate("idDelegacia", ocorrenciaChave.getIdDelegacia())
						.resolveTemplate("anoBo", ocorrenciaChave.getAnoBo())
						.resolveTemplate("numBo", ocorrenciaChave.getNumBo())
						.request(MediaType.APPLICATION_JSON)
						.get();
				json = response.readEntity(String.class);
				
				if (json.contains("codigoErro"))
				{
					Erro erro = gson.fromJson(json, Erro.class);
					String msg = "Erro no WebService consultarOcorrencia " +
							ocorrenciaChave.getIdDelegacia() + "/" + ocorrenciaChave.getAnoBo() + "/" +
							ocorrenciaChave.getNumBo() + " e retornou o erro[" +
							erro.getCodigoErro() + "/" + erro.getMensagemErro() + "]. PROCESSO ABORTADO.";
					logger.error(msg);
					throw new RuntimeException(msg);
				}
				else
				{
					Ocorrencia ocorrencia = gson.fromJson(json, Ocorrencia.class);
					try
					{
						rdoRouboCargaReceptacaoDAO.adicionar(ocorrencia);
					}
					catch(Exception e)
					{
						String msg = "Erro no rdoRouboCargaReceptacaoDAO.adicionar " +
								ocorrencia.getIdDelegacia() + "/" + ocorrencia.getAnoBo() + "/" +
								ocorrencia.getNumBo() + " e retornou o erro[" +
								e.getMessage() + "]. PROCESSO ABORTADO.";
						logger.error(msg);
						throw new RuntimeException(msg);
					}
				}
				
			}
			
			try
			{
				rdoRouboCargaReceptacaoDAO.gerarAtributoComplementar();
			}
			catch(Exception e)
			{
				String msg = "Erro no rdoRouboCargaReceptacaoDAO.gerarAtributoComplementar " + 
							" e retornou o erro[" + e.getMessage() + "]. PROCESSO ABORTADO.";
				logger.error(msg);
				throw new RuntimeException(msg);
			}

			try
			{
				rdoRouboCargaReceptacaoDAO.gerarAtributoCustomComplementarLocalizacaoNoPai();
			}
			catch(Exception e)
			{
				String msg = "Erro no rdoRouboCargaReceptacaoDAO.gerarAtributoCustomComplementarLocalizacaoNoPai " + 
							" e retornou o erro[" + e.getMessage() + "]. PROCESSO ABORTADO.";
				logger.error(msg);
				throw new RuntimeException(msg);
			}
			logger.info("processo terminado com sucesso.");
		}
		
	}
	

	public class Erro implements Serializable {
		private static final long serialVersionUID = -6057820737419323540L;
		private Integer codigoErro;
		private String mensagemErro;
		public Erro() {
		}
		public Erro(String mensagemErro) {
			this.mensagemErro = mensagemErro;
		}
		public Erro(Integer codigo, String mensagemErro) {
			this.setCodigoErro(codigo);
			this.mensagemErro = mensagemErro;
		}
		public String getMensagemErro() {
			return mensagemErro;
		}
		public void setMensagemErro(String mensagemErro) {
			this.mensagemErro = mensagemErro;
		}
		public Integer getCodigoErro() {
			return codigoErro;
		}
		public void setCodigoErro(Integer codigoErro) {
			this.codigoErro = codigoErro;
		}
	}


}
