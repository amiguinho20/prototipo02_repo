package br.com.fences.prototipo02.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.primefaces.model.map.LatLng;

import br.com.fences.prototipo02.converter.Converter;
import br.com.fences.prototipo02.converter.OcorrenciaConverter;
import br.com.fences.prototipo02.entity.Filtro;
import br.com.fences.prototipo02.entity.Ocorrencia;
import br.com.fences.prototipo02.util.EnderecoGeocodeUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Named
@ApplicationScoped
public class RdoRouboCargaReceptacaoDAO {     

	@Inject
	private Converter<Ocorrencia> converter;
		
	@Inject @ColecaoRouboCarga
	private MongoCollection<Document> colecao;
	
	
	/**
	 * Consulta pelo id (identificador unico), o "_id"
	 * @param id
	 */
	public Ocorrencia consultar(final String id)
	{
	    Document documento = colecao.find(eq("_id", new ObjectId(id))).first();
	    Ocorrencia ocorrencia = converter.paraObjeto(documento);
	    consultarComplementares(ocorrencia);
	    return ocorrencia;
	}
	
	/**
	 * atualiza a lista por referencia
	 * @param ocorrencia
	 */
	private void consultarComplementares(Ocorrencia ocorrencia)
	{
    	if (ocorrencia.getComplementares() != null && !ocorrencia.getComplementares().isEmpty())
    	{
        	List<Ocorrencia> complementares = new ArrayList<>();
        	for (Ocorrencia complementar : ocorrencia.getComplementares())
        	{
        		complementar = consultar(complementar.getId());
        		complementares.add(complementar);
        	}
        	ocorrencia.setComplementares(complementares);
    	}
	}
	
	/**
	 * @param pesquisa
	 * @return count
	 */
	public int contar(final Filtro filtro)
	{
//		BasicDBObject search = new BasicDBObject("$search", pesquisa);
//	    BasicDBObject text = new BasicDBObject("$text", search); 
	
	    long countL = colecao.count(filtro.montarPesquisa());
	    int countI = (int) countL;
	    
	    return countI;
	}	
	
	/**
	 * Pesquisa com <b>PAGINACAO</b>
	 * @param pesquisa
	 * @param primeiroRegistro
	 * @param registrosPorPagina
	 * @return List<Ocorrencia> paginado
	 */
	public List<Ocorrencia> pesquisarLazy(final Filtro filtro, final int primeiroRegistro, final int registrosPorPagina)
	{
		List<Ocorrencia> ocorrencias = new ArrayList<>();
		
		//-- filtros
//		BasicDBObject search = new BasicDBObject("$search", pesquisa);
//	    BasicDBObject text = new BasicDBObject("$text", search); 
	    
	    //-- ordenacoes
	    BasicDBObject ordenacao = new BasicDBObject("DATAHORA_REGISTRO_BO", -1); 
	    
	   // db.rdo_roubo_carga_receptacao.find({"DATAHORA_REGISTRO_BO":{$gt:"20051229000000", $lt:"20051229999999"}},{"_id":0, "ANO_BO":1, "NUM_BO":1, "ID_DELEGACIA":1, "DATAHORA_REGISTRO_BO":1}).sort({"DATAHORA_REGISTRO_BO": -1}).count();
	    
	    MongoCursor<Document> cursor = colecao.find(filtro.montarPesquisa()).sort(ordenacao).skip(primeiroRegistro).limit(registrosPorPagina).iterator();
	    
	    try {
	        while (cursor.hasNext()) {
	        	Document documento = cursor.next();
	        	Ocorrencia ocorrencia = converter.paraObjeto(documento);
	        	consultarComplementares(ocorrencia);
	        	//pesquisarOcorrenciaComplementar(ocorrencia);
	        	ocorrencias.add(ocorrencia);
	        }
	    } finally {
	        cursor.close();
	    }
	    
	    return ocorrencias;
	}
	
	public String pesquisarUltimaDataRegistroNaoComplementar()
	{
		String datahoraRegistroBo = null;
		
		BasicDBObject pesquisa = new BasicDBObject("ANO_REFERENCIA_BO", new BasicDBObject("$exists", false));
		BasicDBObject projecao = new BasicDBObject("DATAHORA_REGISTRO_BO", 1).append("_id", 0);
		BasicDBObject ordenacao = new BasicDBObject("DATAHORA_REGISTRO_BO", -1);
		
		MongoCursor<Document> cursor = colecao.find(pesquisa).projection(projecao).sort(ordenacao).limit(1).iterator();
	
	    try {
	        if (cursor.hasNext()) {
	        	Document documento = cursor.next();
	        	datahoraRegistroBo = documento.getString("DATAHORA_REGISTRO_BO");
	        }
	    } finally {
	        cursor.close();
	    }
	    return datahoraRegistroBo;
	}
	
	
	
	/**
	 * Substitui (replace) a ocorrencia pelo id
	 * @param ocorrencia
	 */
	public void substituir(Ocorrencia ocorrencia)
	{
		try
		{
			Document documento = converter.paraDocumento(ocorrencia);
			colecao.replaceOne(eq("_id", documento.get("_id")), documento);
		}
		catch (Exception e)
		{
			String msg = "Erro na alteracao. num[" + ocorrencia.getNumBo() + "] ano[" + ocorrencia.getAnoBo() + "] dlg[" + ocorrencia.getIdDelegacia() + "/" + ocorrencia.getNomeDelegacia() + "].";
			System.err.println(msg);
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	}
	
	
	/**
	 * @deprecated
	 * Insere os registros por referencia, e retorna a lista de referencia.
	 * @param ocorrencia
	 * @return
	 */
	public List<Ocorrencia> pesquisarOcorrenciaComplementar(Ocorrencia ocorrencia)
	{
		BasicDBObject pesquisa = new BasicDBObject();
		pesquisa.put("ANO_REFERENCIA_BO", ocorrencia.getAnoBo());
		pesquisa.put("NUM_REFERENCIA_BO", ocorrencia.getNumBo());
		pesquisa.put("DELEG_REFERENCIA_BO", ocorrencia.getIdDelegacia());
		
	    MongoCursor<Document> cursor = colecao.find(pesquisa).iterator();
	    
	    try {
	        while (cursor.hasNext()) {
	        	Document documento = cursor.next();
	        	Ocorrencia ocorrenciaAux = converter.paraObjeto(documento);
	        	ocorrencia.getComplementares().add(ocorrenciaAux);
	        }
	    } finally {
	        cursor.close();
	    }
		return ocorrencia.getComplementares();
	}
	
	//////---- agregacoes
	public Map<String, Integer> agregarPorFlagrante(final Filtro filtro)
	{
		//aggregate(Arrays.asList(
		//        new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
		//        new Document("$group", new Document("_id", "$address.zipcode").append("count", new Document("$sum", 1)))));
		
		Map<String, Integer> resultado = new TreeMap<>();
		BasicDBObject match = new BasicDBObject("$match", filtro.montarPesquisa());
		
		BasicDBObject agrupamento = new BasicDBObject();
		agrupamento.append("_id", "$FLAG_FLAGRANTE");
		agrupamento.append("quantidade", new BasicDBObject("$sum", 1));
		
		BasicDBObject group = new BasicDBObject("$group", agrupamento);
		
		
		MongoCursor<Document> cursor = colecao.aggregate(Arrays.asList(match, group)).iterator();
		while (cursor.hasNext()) 
		{
			Document doc = cursor.next();
			
			String chave = doc.getString("_id");
			Integer valor = doc.getInteger("quantidade", 0);
			
			resultado.put(chave, valor);
		}
		return resultado;
	}
	
	public Map<String, Integer> agregarPorAno(final Filtro filtro)
	{
		//aggregate(Arrays.asList(
		//        new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
		//        new Document("$group", new Document("_id", "$address.zipcode").append("count", new Document("$sum", 1)))));
		
		Map<String, Integer> resultado = new TreeMap<>();
		BasicDBObject match = new BasicDBObject("$match", filtro.montarPesquisa());
		
		BasicDBObject agrupamento = new BasicDBObject();
		agrupamento.append("_id", "$ANO_BO");
		agrupamento.append("quantidade", new BasicDBObject("$sum", 1));
		
		BasicDBObject group = new BasicDBObject("$group", agrupamento);
		
		
		MongoCursor<Document> cursor = colecao.aggregate(Arrays.asList(match, group)).iterator();
		while (cursor.hasNext()) 
		{
			Document doc = cursor.next();
			
			String chave = doc.getString("_id");
			Integer valor = doc.getInteger("quantidade", 0);
			
			resultado.put(chave, valor);
		}
		return resultado;
	}
	
	public Map<String, Integer> agregarPorComplementar(final Filtro filtro)
	{
		//aggregate(Arrays.asList(
		//        new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
		//        new Document("$group", new Document("_id", "$address.zipcode").append("count", new Document("$sum", 1)))));
		
		Map<String, Integer> resultado = new TreeMap<>();
		BasicDBObject match = new BasicDBObject("$match", filtro.montarPesquisa());
		
		BasicDBObject agrupamento = new BasicDBObject();
		agrupamento.append("_id", "$CUSTOM_COMPLEMENTAR_LOCALIZACAO");
		agrupamento.append("quantidade", new BasicDBObject("$sum", 1));
		
		BasicDBObject group = new BasicDBObject("$group", agrupamento);
		
		
		MongoCursor<Document> cursor = colecao.aggregate(Arrays.asList(match, group)).iterator();
		while (cursor.hasNext()) 
		{
			Document doc = cursor.next();
			
			String chave = doc.getString("_id");
			if (chave == null || chave.equals("null"))
			{
				chave = "Não";
			}
			if (chave.equals("S"))
			{
				chave = "Sim";
			}
			Integer valor = doc.getInteger("quantidade", 0);
			
			resultado.put(chave, valor);
		}
		return resultado;
	}
	
	
	///////--- batch
	
	public void gerarAtributoComplementar()
	{
		  
		converter = new OcorrenciaConverter();
		//-- seleciona as ocorrencias que exista ANO_REFERENCIA_BO, NUM_REFERENCIA_BO, DELEG_REFERENCIA_BO
		
		BasicDBObject pesquisa = new BasicDBObject("ANO_REFERENCIA_BO", new BasicDBObject("$exists", true));
		BasicDBObject periodo = new BasicDBObject();
			periodo.put("$gt", "20150100000000"); 
			periodo.put("$lt", "20151299999999");
		pesquisa.put("DATAHORA_REGISTRO_BO", periodo);
		
	    MongoCursor<Document> cursor = colecao.find(pesquisa).iterator();

	    List<String> paiInexistentes = new ArrayList<>();
    	int qtdRegistro = 0;
    	int qtdPaiInexistente = 0;
    	int qtdFilhoNovo = 0;
    	int qtdIrmao = 0;
    	int qtdIrmaoExistente = 0;
	    
	    try {
	    	outroRegistro: //-- label
	        while (cursor.hasNext()) {
	        	qtdRegistro++;
	        	Document documento = cursor.next();
	        	Ocorrencia complementar = converter.paraObjeto(documento);
	        	
	        	//-- selecionar o pai
	        	BasicDBObject pesquisaPai = new BasicDBObject();
	    		pesquisaPai.put("ANO_BO", complementar.getAnoReferenciaBo());
	    		pesquisaPai.put("NUM_BO", complementar.getNumReferenciaBo());
	    		pesquisaPai.put("ID_DELEGACIA", complementar.getDelegReferenciaBo());
	    		
	    		Document documentoPai = colecao.find(pesquisaPai).first();
	    		if (documentoPai != null)
	    		{
		    		Ocorrencia ocorrenciaPai = converter.paraObjeto(documentoPai);
		    		
		    		if (documentoPai.get("COMPLEMENTAR") == null)
		    		{
		    			documentoPai.append("COMPLEMENTAR", Arrays.asList(documento.get("_id")));
		    			qtdFilhoNovo++;
		    		}
		    		else
		    		{
		    			List<ObjectId> complementares = (List<ObjectId>) documentoPai.get("COMPLEMENTAR");
		    			ObjectId id = (ObjectId) documento.get("_id");
		    			
		    			//-- verifica se documento eh existente
		    			boolean inserir = true;
		    			for (ObjectId aux : complementares)
		    			{
		    				if (aux.equals(id))
		    				{
		    					inserir = false;
		    					qtdIrmaoExistente++;
		    					continue outroRegistro; //-- nao insere, vai para o outro registro
		    				}
		    			}
		    			if (inserir)
		    			{
			    			complementares.add(id);
			    			qtdIrmao++;
		    			}
		    		}
		    		
		    		if ((qtdRegistro % 20) == 0)
		    		{
		    		    System.out.println("qtdRegistro[" + qtdRegistro 
		    		    		+ "] qtdPaiInexistente[" + qtdPaiInexistente 
		    		    		+ "] qtdFilhoNvo[" + qtdFilhoNovo 
		    		    		+ "] qtdIrmao[" + qtdIrmao 
		    		    		+ "] qtdIrmaoExistente[" + qtdIrmaoExistente + "]");
		    		    System.out.println("filho [" + complementar.getNumBo() + "/" + complementar.getAnoBo() + "/" + complementar.getIdDelegacia() + "] no pai [" + ocorrenciaPai.getNumBo() + "/" + ocorrenciaPai.getAnoBo() + "/" + ocorrenciaPai.getIdDelegacia() + "]");
		    		    System.out.println(documentoPai.toJson());
		    		}
		    		colecao.replaceOne(eq("_id", documentoPai.get("_id")), documentoPai);
		        	
		        	//ocorrencia.getOcorrenciasComplementares().add(ocorrenciaAux);
	    		}
	    		else
	    		{
	    			qtdPaiInexistente++;
	    			paiInexistentes.add(pesquisaPai.toString());
		    		//System.err.println("NO FILHO [" + complementar.getNumBo() + "/" + complementar.getAnoBo() + "/" + complementar.getIdDelegacia() + "] NAO EXISTE O PAI [" + complementar.getNumReferenciaBo() + "/" + complementar.getAnoReferenciaBo() + "/" + complementar.getDelegReferenciaBo() + "]");
	    		}
	        }
	    } catch(Exception e)
	    {
	    	e.printStackTrace();
	    } 
	    finally {
	        cursor.close();
	    }
	    System.err.println("qtdRegistro[" + qtdRegistro 
	    		+ "] qtdPaiInexistente[" + qtdPaiInexistente 
	    		+ "] qtdFilhoNvo[" + qtdFilhoNovo 
	    		+ "] qtdIrmao[" + qtdIrmao 
	    		+ "] qtdIrmaoExistente[" + qtdIrmaoExistente + "]");
	    
	    for (String pai: paiInexistentes)
	    {
	    	System.out.println(pai);
	    }
	}
	
	public void gerarGoogleGeocode()
	{
		  
		converter = new OcorrenciaConverter();
		//-- seleciona as ocorrencias que exista ANO_REFERENCIA_BO, NUM_REFERENCIA_BO, DELEG_REFERENCIA_BO
		
		BasicDBObject pesquisa = new BasicDBObject("GOOGLE_LATITUDE", new BasicDBObject("$exists", false));
		BasicDBObject periodo = new BasicDBObject();
			periodo.put("$gt", "20040000000000");
			periodo.put("$lt", "20049999999999");
		pesquisa.put("DATAHORA_REGISTRO_BO", periodo);
		
		BasicDBObject ordenacao = new BasicDBObject("DATAHORA_REGISTRO_BO", 1); 
		
	    MongoCursor<Document> cursor = colecao.find(pesquisa).sort(ordenacao).iterator();
	    

    	int qtdRegistro = 0;
    	int qtdExistente = 0;
    	int qtdInexistente = 0;
 	    
	    try {
	        while (cursor.hasNext()) {
	        	qtdRegistro++;
	        	Document documento = cursor.next();
	        	Ocorrencia ocorrencia = converter.paraObjeto(documento);
	        	LatLng latLng = EnderecoGeocodeUtil.converter(ocorrencia);
	        	//Thread.sleep(1000);

	        	if (latLng != null)
	        	{
		        	String latitude = Double.toString(latLng.getLat());
		        	String longitude = Double.toString(latLng.getLng());
		        	
	    			documento.append("GOOGLE_LATITUDE", latitude);
	    			documento.append("GOOGLE_LONGITUDE", longitude);

	    			qtdExistente++;
		    		colecao.replaceOne(eq("_id", documento.get("_id")), documento);
	        	}
	        	else
	        	{
	        		//Thread.sleep(1000);
	    		    System.err.println("geoNaoEncontrado [" + ocorrencia.getNumBo() + "/" + ocorrencia.getAnoBo() + "/" + ocorrencia.getIdDelegacia() + "/" + ocorrencia.getDatahoraRegistroBo() + "]");
	        		qtdInexistente++;
	        	}
	        	
	    		if ((qtdRegistro % 20) == 0)
	    		{
	    		    System.out.println("qtdRegistro[" + qtdRegistro 
	    		    		+ "] qtdExistente[" + qtdExistente 
	    		    		+ "] qtdInexistente[" + qtdInexistente 
	    		    		+ "] ");
	    		    System.out.println("reg [" + ocorrencia.getNumBo() + "/" + ocorrencia.getAnoBo() + "/" + ocorrencia.getIdDelegacia() + "/" + ocorrencia.getDatahoraRegistroBo() + "]");
	    		    System.out.println(documento.toJson());
	    		}
	        }
	    } catch(Exception e)
	    {
	    	e.printStackTrace();
	    } 
	    finally {
	        cursor.close();
	    }
	    System.err.println("qtdRegistro[" + qtdRegistro 
	    		+ "] qtdExistente[" + qtdExistente 
	    		+ "] qtdInexistente[" + qtdInexistente 
	    		+ "] ");
	    
	}
	
	public void gerarAtributoCustomComplementarLocalizacaoNoPai()
	{
		  
		converter = new OcorrenciaConverter();
		//-- seleciona as ocorrencias que exista ANO_REFERENCIA_BO, NUM_REFERENCIA_BO, DELEG_REFERENCIA_BO
		
		BasicDBObject pesquisa = new BasicDBObject("ANO_REFERENCIA_BO", new BasicDBObject("$exists", true));
		BasicDBObject periodo = new BasicDBObject();
			periodo.put("$gt", "20100000000000");
			periodo.put("$lt", "20119999999999");
		pesquisa.put("DATAHORA_REGISTRO_BO", periodo);
		pesquisa.put("NATUREZA.ID_OCORRENCIA", "40");
		pesquisa.put("NATUREZA.ID_ESPECIE", "40");
		
	    MongoCursor<Document> cursor = colecao.find(pesquisa).iterator();

	    List<String> paiInexistentes = new ArrayList<>();
    	int qtdRegistro = 0;
    	int qtdNovo = 0;
    	int qtdExistente = 0;
    	int qtdPaiInexistente = 0;
    	//int qtdIrmao = 0;
    	//int qtdIrmaoExistente = 0;
	    
	    try {
	    	outroRegistro: //-- label
	        while (cursor.hasNext()) {
	        	qtdRegistro++;
	        	Document documento = cursor.next();
	        	Ocorrencia complementar = converter.paraObjeto(documento);
	        	
	        	//-- selecionar o pai
	        	BasicDBObject pesquisaPai = new BasicDBObject();
	    		pesquisaPai.put("ANO_BO", complementar.getAnoReferenciaBo());
	    		pesquisaPai.put("NUM_BO", complementar.getNumReferenciaBo());
	    		pesquisaPai.put("ID_DELEGACIA", complementar.getDelegReferenciaBo());
	    		
	    		Document documentoPai = colecao.find(pesquisaPai).first();
	    		if (documentoPai != null)
	    		{
		    		if (documentoPai.get("CUSTOM_COMPLEMENTAR_LOCALIZACAO") == null)
		    		{
		    			documentoPai.append("CUSTOM_COMPLEMENTAR_LOCALIZACAO", "S");
			    		colecao.replaceOne(eq("_id", documentoPai.get("_id")), documentoPai);
		    			qtdNovo++;
		    		}
		    		else
		    		{
		    			qtdExistente++;
		    		}
		    		
		    		if ((qtdRegistro % 20) == 0)
		    		{
		    			Ocorrencia pai = converter.paraObjeto(documentoPai);
		    		    System.out.println("qtdRegistro[" + qtdRegistro 
		    		    		+ "] qtdNovo[" + qtdNovo 
		    		    		+ "] qtdPaiInexistente[" + qtdPaiInexistente 
		    		    		+ "] qtdExistente[" + qtdExistente + "]");
		    		    System.out.println("filho [" + complementar.getNumBo() + "/" + complementar.getAnoBo() + "/" + complementar.getIdDelegacia() + "] no pai [" + pai.getNumBo() + "/" + pai.getAnoBo() + "/" + pai.getIdDelegacia() + "]");
		    		    System.out.println(documentoPai.toJson());
		    		}
		        	
		        	//ocorrencia.getOcorrenciasComplementares().add(ocorrenciaAux);
	    		}
	    		else
	    		{
	    			qtdPaiInexistente++;
	    			paiInexistentes.add(pesquisaPai.toString());
		    		//System.err.println("NO FILHO [" + complementar.getNumBo() + "/" + complementar.getAnoBo() + "/" + complementar.getIdDelegacia() + "] NAO EXISTE O PAI [" + complementar.getNumReferenciaBo() + "/" + complementar.getAnoReferenciaBo() + "/" + complementar.getDelegReferenciaBo() + "]");
	    		}
	        }
	    } catch(Exception e)
	    {
	    	e.printStackTrace();
	    } 
	    finally {
	        cursor.close();
	    }
	    System.err.println("qtdRegistro[" + qtdRegistro 
	    		+ "] qtdNovo[" + qtdNovo 
	    		+ "] qtdPaiInexistente[" + qtdPaiInexistente 
	    		+ "] qtdExistente[" + qtdExistente + "]");

	    
	    for (String pai: paiInexistentes)
	    {
	    	System.out.println(pai);
	    }
	}
	
	
	
}
