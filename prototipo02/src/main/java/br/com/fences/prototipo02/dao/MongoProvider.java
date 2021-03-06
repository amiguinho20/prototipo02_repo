package br.com.fences.prototipo02.dao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.bson.Document;

import br.com.fences.prototipo02.config.AppConfig;
import br.com.fences.prototipo02.util.Verificador;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Named
@ApplicationScoped
public class MongoProvider {

//	private static final String USUARIO = "proto";  
//	private static final String SENHA = "proto12";
//	private static final String HOST = "ds061651.mongolab.com";
//	private static final String PORTA = "61651";
//	private static final String BANCO = "amiguinho";
	private static final String COLECAO_ROUBO_CARGA = "rdo_roubo_carga_recep03"; 
	private static final String COLECAO_ENDERECO_AVULSO = "endereco_avulso";
	
	private MongoClient conexao;
	private MongoDatabase banco;
	private MongoCollection<Document> colecaoRdoRouboCarga;
	private MongoCollection<Document> colecaoEnderecosAvulsos;

	
	@Inject
	private AppConfig appConfig;
	
	@PostConstruct
	public void abrirConexao() 
	{
		System.out.println("Inicio da abertura de conexao geral...");
		
		String dbMongoHost = appConfig.getDbMongoHost();
		String dbMongoPort = appConfig.getDbMongoPort();
		String dbMongoDatabase = appConfig.getDbMongoDatabase();
		String dbMongoUser = appConfig.getDbMongoUser();
		String dbMongoPass = appConfig.getDbMongoPass();
		
		if (Verificador.isValorado(dbMongoUser))
		{
			String uriConexao = String.format("mongodb://%s:%s@%s:%s/%s", dbMongoUser, dbMongoPass, dbMongoHost, dbMongoPort, dbMongoDatabase);
			MongoClientURI uri  = new MongoClientURI(uriConexao); 
			conexao = new MongoClient(uri);
		}
		else
		{
			conexao = new MongoClient(dbMongoHost, Integer.parseInt(dbMongoPort));
		}
		banco = conexao.getDatabase(dbMongoDatabase);
		

		//		colecao = banco.getCollection(COLECAO);
		colecaoRdoRouboCarga = banco.getCollection(COLECAO_ROUBO_CARGA);
		if (colecaoRdoRouboCarga == null)
		{
			banco.createCollection(COLECAO_ROUBO_CARGA);
			colecaoRdoRouboCarga = banco.getCollection(COLECAO_ROUBO_CARGA);
		}
		
		colecaoEnderecosAvulsos = banco.getCollection(COLECAO_ENDERECO_AVULSO);
		if (colecaoEnderecosAvulsos == null)
		{
			banco.createCollection(COLECAO_ENDERECO_AVULSO);
			colecaoEnderecosAvulsos = banco.getCollection(COLECAO_ENDERECO_AVULSO);
		}

		System.out.println("Termino da abertura da conexao geral.");
	    
	}
	
	/**
	 * Fechar a conexao com o banco quando o objeto for destruido.
	 */
	@PreDestroy
	public void fecharConecao()
	{
		conexao.close();
	}
	
	@Produces @ColecaoRouboCarga
	public MongoCollection<Document> getColecaoRouboCarga()
	{
		return colecaoRdoRouboCarga;
	}
	
	@Produces @ColecaoEnderecoAvulso
	public MongoCollection<Document> getColecaoEnderecoAvulso()
	{
		return colecaoEnderecosAvulsos;
	}
}
