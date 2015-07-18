package br.com.fences.prototipo02.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.bson.Document;
import org.bson.types.ObjectId;

import br.com.fences.prototipo02.converter.Converter;
import br.com.fences.prototipo02.entity.EnderecoAvulso;
import br.com.fences.prototipo02.entity.FiltroEnderecoAvulso;
import br.com.fences.prototipo02.util.FormatarData;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Named
@ApplicationScoped
public class EnderecoAvulsoDAO {     

	
	@Inject
	private Converter<EnderecoAvulso> converter;
	
	@Inject @ColecaoEnderecoAvulso
	private MongoCollection<Document> colecao;
	
	
	/**
	 * Consulta pelo id (identificador unico), o "_id"
	 * @param id
	 */
	public EnderecoAvulso consultar(final String id)
	{
	    Document documento = colecao.find(eq("_id", new ObjectId(id))).first();
	    EnderecoAvulso enderecoAvulso = converter.paraObjeto(documento);
	    return enderecoAvulso;
	}
	
	/**
	 * Contagem de pesquisa 
	 * @param pesquisa
	 * @return count
	 */
	public int contar(final FiltroEnderecoAvulso filtro)
	{
	    long countL = colecao.count(filtro.montarPesquisa());
	    int countI = (int) countL;
	    return countI;
	}	
	
	/**
	 * Pesquisa com <b>PAGINACAO</b>
	 * @param pesquisa
	 * @param primeiroRegistro
	 * @param registrosPorPagina
	 * @return List<EnderecoAvulso> paginado
	 */
	public List<EnderecoAvulso> pesquisarLazy(final FiltroEnderecoAvulso filtro, final int primeiroRegistro, final int registrosPorPagina)
	{
		List<EnderecoAvulso> enderecosAvulsos = new ArrayList<>();
		
	    //-- ordenacoes
//	    BasicDBObject ordenacao = new BasicDBObject("DATAHORA_REGISTRO_BO", -1); 
	    
//	    MongoCursor<Document> cursor = colecao.find(filtro.montarPesquisa()).sort(ordenacao).skip(primeiroRegistro).limit(registrosPorPagina).iterator();

	    MongoCursor<Document> cursor = colecao.find(filtro.montarPesquisa()).skip(primeiroRegistro).limit(registrosPorPagina).iterator();

		
	    try {
	        while (cursor.hasNext()) {
	        	Document documento = cursor.next();
	        	EnderecoAvulso enderecoAvulso = converter.paraObjeto(documento);
	        	enderecosAvulsos.add(enderecoAvulso);
	        }
	    } finally {
	        cursor.close();
	    }
	    
	    return enderecosAvulsos;
	}
	
	public List<EnderecoAvulso> pesquisarAtivoPorTipo(List<String> tipos)
	{
		List<EnderecoAvulso> enderecosAvulsos = new ArrayList<>();
		BasicDBObject pesquisa = new BasicDBObject();
		
		if (tipos != null && !tipos.isEmpty())
		{
			pesquisa.put("tipo", 
					new BasicDBObject("$in", tipos ));
			pesquisa.put("indicadorAtivo", "Sim");

			MongoCursor<Document> cursor = colecao.find(pesquisa).iterator();
			
		    try {
		        while (cursor.hasNext()) {
		        	Document documento = cursor.next();
		        	EnderecoAvulso enderecoAvulso = converter.paraObjeto(documento);
		        	enderecosAvulsos.add(enderecoAvulso);
		        }
		    } finally {
		        cursor.close();
		    }
		}
		
		return enderecosAvulsos;
	}
	
	/**
	 * Substitui (replace) o enderecoAvulso pelo id
	 * @param ocorrencia
	 */
	public void substituir(EnderecoAvulso enderecoAvulso)
	{
		try
		{
			enderecoAvulso.setUltimaAtualizacao(dataHoraCorrente());
			Document documento = converter.paraDocumento(enderecoAvulso);
			colecao.replaceOne(eq("_id", documento.get("_id")), documento);
		}
		catch (Exception e)
		{
			String msg = "Erro na alteracao. log[" + enderecoAvulso.getLogradouro() + "].";
			System.err.println(msg);
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	} 
	
	public void adicionar(EnderecoAvulso enderecoAvulso)
	{
		try
		{
			enderecoAvulso.setUltimaAtualizacao(dataHoraCorrente());
			Document documento = converter.paraDocumento(enderecoAvulso);
			colecao.insertOne(documento);
		}
		catch (Exception e)
		{
			String msg = "Erro na inclusao unica. log[" + enderecoAvulso.getLogradouro() + "].";
			System.err.println(msg);
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	}
	
	public void adicionar(List<EnderecoAvulso> enderecosAvulsos)
	{
		try
		{
			List<Document> documentos = new ArrayList<>();
			for (EnderecoAvulso enderecoAvulso : enderecosAvulsos)
			{
				enderecoAvulso.setUltimaAtualizacao(dataHoraCorrente());
				Document documento = converter.paraDocumento(enderecoAvulso);
				documentos.add(documento);
			}
			colecao.insertMany(documentos);
		}
		catch (Exception e)
		{
			String msg = "Erro na inclusão em lote. log[" + e.getMessage() + "].";
			System.err.println(msg);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void remover(EnderecoAvulso enderecoAvulso)
	{
		try
		{
			Document documento = converter.paraDocumento(enderecoAvulso);
			colecao.deleteOne(eq("_id", documento.get("_id")));
		}
		catch (Exception e)
		{
			String msg = "Erro na alteracao. log[" + enderecoAvulso.getLogradouro() + "].";
			System.err.println(msg);
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	}
	
	private String dataHoraCorrente()
	{
		String ultimaAtualizacao = FormatarData.getAnoMesDiaHoraMinutoSegundoConcatenados().format(new Date());
		return ultimaAtualizacao; 
	}
	
	
	//////---- agregacoes
	public Map<String, Integer> agregarPorFlagrante(final FiltroEnderecoAvulso filtro)
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
	
	public Map<String, Integer> agregarPorAno(final FiltroEnderecoAvulso filtro)
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
	
	public Map<String, Integer> agregarPorComplementar(final FiltroEnderecoAvulso filtro)
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
}
