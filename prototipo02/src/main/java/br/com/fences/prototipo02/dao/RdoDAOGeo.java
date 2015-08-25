package br.com.fences.prototipo02.dao;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;




//@Named
//@ApplicationScoped
public class RdoDAOGeo implements Serializable {

	private static final long serialVersionUID = 4022543680970584684L;

	//@Resource(mappedName = "ds/Db2Rdo")
	private DataSource dataSource;

	private DateFormat dfData = FormatarData.getAnoMesDiaContatenado();
	private DateFormat dfDataHora =FormatarData.getAnoMesDiaHoraMinutoSegundoConcatenados();
	
	private List<String> colunasDescartadas = Arrays.asList("ID_DELEGACIA", "ANO_BO", "NUM_BO");
	
	//-- preparedStatements;
	Map<String, PreparedStatement> mapPrepStatement = new HashMap<>();
	
    public static void main(String[] args) {
        String jdbcClassName="com.ibm.db2.jcc.DB2Driver";
        String url="jdbc:db2://10.200.35.240:50000/RDO_PRD";
        String user="CARGA_OMEGA";
        String password="7j4#l1p";
 
        if (args.length < 2 || args.length > 3)
        {
        	System.err.println("Qtd de parametros invalidos...");
            System.err.println("1) yyyymmdd yyyymmdd");
        	System.err.println("2) idDelegacia anoBo numBo");
        	return;
        }
        
        
        Connection connection = null;
        try {
            //Load class into memory
            Class.forName(jdbcClassName);
            //Establish connection
            connection = DriverManager.getConnection(url, user, password);
            //connection.setSchema("DB2APLICATIVOS");
            
            RdoDAOGeo rdoDao = new RdoDAOGeo();
           // String json = rdoDao.consultar(30111, 2015, 1995, connection);
            
            List<String> registros = rdoDao.pesquisar(connection, args);
            String nomeArquivo = "teste.json";
            if (args.length == 1)
            {
            	nomeArquivo = args[0];
            }
            else
            {
            	nomeArquivo = args[1];
            }
            
            rdoDao.criarArquivo(nomeArquivo, registros);
            
        } catch (ClassNotFoundException e) {
        	System.err.println(e);
        } catch (SQLException e) {
        	System.err.println(e);
        } catch (IOException e) {
        	System.err.println(e);
        } catch (Exception e) {
			System.err.println(e);
		}finally{
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                	System.err.println(e);
                }
            }
        }
 
    }
    
    
    public void criarArquivo(String nomeArquivo, List<String> registros) throws IOException
    {
    	String arquivo = "/deploy/omega/recursos/processos/lib/"+ nomeArquivo + ".json";
    	System.out.println("Gerando arquivo [" + arquivo + "]");
        
    	PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo), "UTF-8")));
    	
    	for (String linha : registros)
    	{
    		pw.println(linha);
    	}
    	pw.close();
    	System.out.println("Arquivo gerado com sucesso.");
    }
    
    public List<String> pesquisar(Connection conn, String... param)
    {
    	List<String> registros = new ArrayList<>();
    	
		Statement stmt = null;
		ResultSet rset = null;

		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("");
			
			sql.append("select a.id_delegacia, a.ano_bo, a.num_bo ");
			sql.append("from  ");
			sql.append("    db2aplicativos.tb_bo a  ");
			sql.append("where   ");
			if (param.length == 1)
			{
				sql.append("    a.DATAHORA_REGISTRO_BO =  ");
				sql.append("    TIMESTAMP_FORMAT('"+ param[0] + "', 'YYYYMM')  ");
				
			}
			if (param.length == 2)
			{	
				sql.append("    a.DATAHORA_REGISTRO_BO between  ");
				sql.append("    TIMESTAMP_FORMAT('"+ param[0] + "000000', 'YYYYMMDDHH24MISS') and ");
				sql.append("    TIMESTAMP_FORMAT('" + param[1] + "235959', 'YYYYMMDDHH24MISS') ");
			}
			if (param.length == 3)
			{
				sql.append("    a.id_delegacia = " + param[0] + " ");
				sql.append("and a.ano_bo = " + param[1] + " ");
				sql.append("and a.num_bo = " + param[2] + " ");
			}
			sql.append("and a.latitude is not null ");  
			sql.append("order by a.DATAHORA_REGISTRO_BO");

			
			if (param.length == 1)
			{
				System.out.println("[" + dfDataHora.format(Calendar.getInstance().getTime()) + "] Inicio da pesquisas com [" + param[0] + "]");
			}
			if (param.length == 2)
			{
				System.out.println("[" + dfDataHora.format(Calendar.getInstance().getTime()) + "] Inicio da pesquisas com [" + param[0] + "] e [" + param[1] + "]");
			}
			if (param.length == 3)
			{
				System.out.println("consultando...");
			}
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql.toString());
			System.out.println("[" + dfDataHora.format(Calendar.getInstance().getTime()) + "] Termino da pesquisa e inicio da recuperacao de chaves...");
			
			List<int[]> listaGeral = new ArrayList<>();
			while (rset.next()) {
				
				int idDelegacia = rset.getInt(1);
				int ano = rset.getInt(2);
				int numero = rset.getInt(3);
				
				int[] bo = {idDelegacia, ano, numero};
				listaGeral.add(bo);

			}
			System.out.println("[" + dfDataHora.format(Calendar.getInstance().getTime()) + "] Foram coletadas [" + listaGeral.size() + "] chaves de ocorrencia, inicio da consulta de cada...");

			int indice = 0;
			for (int[] boAux : listaGeral)
			{
				indice++;
				String registro = consultar(boAux[0], boAux[1], boAux[2], conn);
				registros.add(registro);
				
				if ((indice % 20) == 0)
				{
					System.out.println("[" + dfDataHora.format(Calendar.getInstance().getTime()) + "] selecionado [" + indice + "/" + listaGeral.size() + "]...");
				}
			}
			
			System.out.println("[" + dfDataHora.format(Calendar.getInstance().getTime()) + "] Termino das consultas [" + registros.size() + "] registros.");
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			//-- fechar maps de prepare
			for (Map.Entry<String, PreparedStatement> prep : mapPrepStatement.entrySet())
			{
				DaoUtil.fecharRecurso(prep.getValue());
			}
			
			DaoUtil.fecharRecurso(rset, stmt, conn);
			System.out.println("RECURSOS FECHADOS...");
		}
    	return registros;
    }
	
    
   
	public String consultar(int idDelegacia, int ano, int numero, Connection conn){

		StringBuilder json = new StringBuilder();
		
		ResultSet rset = null;

		try {
			final String PREPARE = "tb_bo";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(PREPARE))
			{
				pstm = mapPrepStatement.get(PREPARE);
			}
			else
			{
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT ");
				sql.append("	a.LOGRADOURO logradouro, ");
				sql.append("	a.NUMERO_LOGRADOURO numero, ");
				sql.append("	a.COMPLEMENTO complemento, ");
				sql.append("	a.BAIRRO bairro, ");
				sql.append("	a.CEP cep, ");
				sql.append("	a.CIDADE cidade, ");
				sql.append("	a.ID_UF uf, ");
				sql.append("	a.LATITUDE latitude, ");
				sql.append("	a.LONGITUDE longitude ");
				sql.append("FROM  ");
				sql.append("	db2aplicativos.TB_BO a  ");
				sql.append("WHERE ");
				sql.append("	a.ID_DELEGACIA = ? ");
				sql.append("and a.ANO_BO = ? ");
				sql.append("and a.NUM_BO = ? ");
				
				pstm = conn.prepareStatement(sql.toString());
				mapPrepStatement.put(PREPARE, pstm);
			}
			pstm.setInt(1, idDelegacia);
			pstm.setInt(2, ano);
			pstm.setInt(3, numero);

			rset = pstm.executeQuery();

			if (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				adicionarAtributoJson(atributos, criarAtributoJson("logradouro", converterValorParaString(rset, "logradouro")));
				adicionarAtributoJson(atributos, criarAtributoJson("numero", converterValorParaString(rset, "numero")));
				adicionarAtributoJson(atributos, criarAtributoJson("complemento", converterValorParaString(rset, "complemento")));
				adicionarAtributoJson(atributos, criarAtributoJson("bairro", converterValorParaString(rset, "bairro")));
				adicionarAtributoJson(atributos, criarAtributoJson("cep", converterValorParaString(rset, "cep")));
				adicionarAtributoJson(atributos, criarAtributoJson("cidade", converterValorParaString(rset, "cidade")));
				adicionarAtributoJson(atributos, criarAtributoJson("uf", converterValorParaString(rset, "uf")));
				adicionarAtributoJson(atributos, criarAtributoJson("geocodeStatus", "OK"));
				adicionarAtributoJson(atributos, criarAtributoJson("ultimaAtualizacao", FormatarData
						.getAnoMesDiaHoraMinutoSegundoConcatenados().format(Calendar.getInstance().getTime())));
				
				List<String> geometry = new ArrayList<>();
				adicionarAtributoJson(geometry, criarAtributoJson("type", "Point"));
				
				adicionarAtributoJson(geometry, "\"coordinates\":[" + rset.getDouble("longitude") + ", " + rset.getDouble("latitude") + "]");
					
				
//				adicionarAtributoJson(geometry,
//						criarJsonArray("coordinates", Arrays.asList(converterValorParaString(rset, "longitude"),
//								converterValorParaString(rset, "latitude"))));
				
				String strGeometry = criarJson(geometry);
				
				adicionarAtributoJson(atributos, criarAtributoJsonSemValidacao("geometry", strGeometry));

				json.append(criarJson(atributos));
			}
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json.toString();
	}


	

	//--------------------------------------------- classes tecnicas
	
	/**
	 * @param descargarCols   (descartar colunas do map colunasDescartadas
	 * @param conn            (para criar o PreparedStatement caso necessario)
	 * @param atributoJson    (atributo do json de retorno, ex: NATUREZA, e cache/map do PreparedStatement)
	 * @param prepare         (chave do cache/map de PreparedStatement)
	 * @param queryPreparada  (query para montagem do PreparedStatement caso necessario)
	 * @param chaves          (condicoes da query/sql do PreparedStatement, int ou String)
	 * @return json          
	 * @throws SQLException
	 */
	private String prepararConsultarIterar(Connection conn, String atributoJson, String prepare, String queryPreparada, Object... chaves) throws SQLException 
	{
		String json = "";
		
		try
		{
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(prepare))
			{
				pstm = mapPrepStatement.get(prepare);
			}
			else
			{
				pstm = conn.prepareStatement(queryPreparada);
				mapPrepStatement.put(prepare, pstm);
			}
			int indice = 0;
			for (Object chave : chaves)
			{	
				indice++;
				if (chave instanceof String)
				{
					pstm.setString(indice, chave.toString());
				}
				else
				{
					pstm.setInt(indice, Integer.parseInt(chave.toString()));
				}
			}
			
			ResultSet rset = null;
			try 
			{
				rset = pstm.executeQuery();
				
				List<String> atributosRet = new ArrayList<>();
				while (rset.next()) {
	
					List<String> atributos = new ArrayList<>();
					
					//-- recuperacao das colunas de cabecalho
					ResultSetMetaData rsmd = rset.getMetaData();
					int qtdColuna = rsmd.getColumnCount();
					for (int i = 1; i <= qtdColuna; i++) {
					
						String coluna = rsmd.getColumnName(i);
						
						if (colunasDescartadas.contains(coluna))
						{
							continue;
						}
						
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
					
					json = criarJson(atributos);
					atributosRet.add(json);
				}
				json = criarJsonArray(atributoJson, atributosRet);
			} 
			finally 
			{
				DaoUtil.fecharRecurso(rset);
			}
		
		}
		catch (SQLException e)
		{
			StringBuilder params = new StringBuilder();
			for (Object chave : chaves)
			{
				if (!params.toString().isEmpty())
				{
					params.append(", ");
				}
				params.append(chave.toString());
			}
			System.err.println("Erro no prepararConsultarIterar: " + e.getMessage());
			System.err.println("Na query [" + queryPreparada + "]");
			System.err.println("Com as chaves [" + params.toString() + "]");
			System.err.println("Do atributo [" + atributoJson + "]");
			System.err.println("Do prepare [" + prepare + "]");

			
			System.err.println(e);
			throw e;
		}
		return json;
	}
	
	

	/**
	 * A chave e o valor sao envolvidos por aspas.
	 * @param chave
	 * @param valor
	 * @return
	 */
	private String criarAtributoJson(String chave, String valor){
		String atributo = "";
		if (valor != null && !valor.trim().isEmpty())
		{
			valor = valor.replaceAll("[^\\u0020-\\u007e\\u00a0-\\u00ff]|\\u005c|\\u0022", " ");
			valor = valor.trim();
			atributo =  "\"" + chave + "\":\"" + valor + "\"";
		}
		return atributo;
	}
	
	private String criarAtributoJsonSemValidacao(String chave, String valor){
		String atributo = "";
		if (valor != null && !valor.trim().isEmpty())
		{
			valor = valor.trim();
			atributo =  "\"" + chave + "\":" + valor;
		}
		return atributo;
	}
	
	private void adicionarAtributoJson(List<String> lista, String valor)
	{
		if (valor != null && !valor.trim().isEmpty())
		{
			lista.add(valor);
		}
	}
	
	private String criarJson(List<String> atributos)
	{
		StringBuilder json = new StringBuilder();
		if (atributos != null && !atributos.isEmpty())
		{
			for (String atributo : atributos)
			{
				if (!json.toString().isEmpty())
				{
					json.append(",");
				}
				json.append(atributo);
			}
			json = new StringBuilder("{" + json.toString() + "}");
		}
		return json.toString();
	}
	
	private String criarJsonArray(String chave, List<String> valores)
	{
		StringBuilder json = new StringBuilder();
		if (valores != null && !valores.isEmpty())
		{
			for (String valor : valores)
			{
				if (!json.toString().isEmpty())
				{
					json.append(",");
				}
				json.append(valor);
			}
			json = new StringBuilder("\"" + chave + "\":[" + json.toString() + "]");
		}
		return json.toString();
	}
	
	private String converterValorParaString(ResultSet rset, String coluna) throws SQLException
	{
		String valor = "";
		Object obj = rset.getObject(coluna);
		
		if (obj instanceof Date)
		{
			Date data = rset.getDate(coluna);
			valor = dfData.format(data);
		}
		else if (obj instanceof Timestamp)
		{
			Timestamp dataHora = rset.getTimestamp(coluna);
			valor = dfDataHora.format(dataHora);
		}
		else
		{
			valor = rset.getString(coluna);
		}
		return valor;
	}

	private String horaRdo(String hora) {
    	
        String retorno = null;
        if(hora != null && hora.trim().length() > 0) {
            
            String horaTemp = "00" + String.valueOf(hora.toCharArray(), 0, (hora.indexOf(':'))).trim();
            String horaFormatada = String.valueOf(horaTemp.toCharArray(), (horaTemp.length() - 2), 2);
            
            String minutoTemp = String.valueOf(hora.toCharArray(), (hora.indexOf(':')  + 1), ((hora.length() - 1) - hora.indexOf(':'))).trim();
            String minutoFormatado = (minutoTemp + "00").substring(0, 2);
            
            if(Integer.valueOf(minutoFormatado) > 59) {
                minutoFormatado = ("0" + minutoTemp).substring(0, 2);
            }
            
            retorno = horaFormatada + ":" + minutoFormatado;
        }
        return retorno;
    }    	
	
	public static class DaoUtil {

	    /**
	     * A passagem de parametros deve estar na ordem de fechamento. 
	     * Exemplos: 
	     * resultset, preparedstatement ou statement, connection;
	     * preparedstatement ou statement, connection;
	     * connection;
	     * @param Wrapper (ResultSet, Statement, PreparedStatement, Connection) objetos
	     */
	    public static void fecharRecurso(Wrapper... objetos)
	    {
	        for (Wrapper objeto : objetos)
	        {
	            try
	            {
	                if (objeto instanceof ResultSet)
	                {
	                    ResultSet rset = (ResultSet) objeto;
	                    rset.close();
	                }
	                if (objeto instanceof Statement)
	                {
	                    Statement stmt = (Statement) objeto;
	                    stmt.close();
	                }
	                if (objeto instanceof PreparedStatement)
	                {
	                    PreparedStatement pstm = (PreparedStatement) objeto;
	                    pstm.close();
	                }
	                if (objeto instanceof Connection)
	                {
	                    Connection conn = (Connection) objeto;
	                    conn.close();
	                }
	            }
	            catch(Exception e)
	            {
	                //-- nao tratar
	            }
	        }
	    }	
		
	}
	
	public static class FormatarData {
		
		private static DateFormat tipo01 = new SimpleDateFormat("yyyyMMddHHmmss");
		private static DateFormat tipo02 = new SimpleDateFormat("yyyyMMdd");
		private static DateFormat tipo03 = new SimpleDateFormat("dd/MM/yyyy");
		private static DateFormat tipo04 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		private static DateFormat tipo05 = new SimpleDateFormat("HHmm");
		
		/**
		 * yyyyMMddHHmmss
		 */
		public static DateFormat getAnoMesDiaHoraMinutoSegundoConcatenados()
		{
			return tipo01;
		}
		
		/**
		 * yyyyMMdd
		 */
		public static DateFormat getAnoMesDiaContatenado()
		{
			return tipo02;
		}
		
		/**
		 * dd/MM/yyyy
		 */
		public static DateFormat getDiaMesAnoComBarras()
		{
			return tipo03;
		}
		
		/**
		 * dd/MM/yyyy HH:mm:ss
		 */
		public static DateFormat getDiaMesAnoComBarrasEHoraMinutoSegundoComDoisPontos()
		{
			return tipo04;
		}
		
		/**
		 * HHmm
		 */
		public static DateFormat getHoraMinutoContatenado()
		{
			return tipo05;
		}
	}
	
	public class Endereco {

		private String logradouro;
		private String numero;
		private String complemento;
		private String bairro;
		private String cidade;
		private String uf;
		private String cep;
		private String geocodeStatus;
		private String ultimaAtualizacao;
		private Point geometry = new Point();
		
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
		public String getComplemento() {
			return complemento;
		}
		public void setComplemento(String complemento) {
			this.complemento = complemento;
		}
		public String getBairro() {
			return bairro;
		}
		public void setBairro(String bairro) {
			this.bairro = bairro;
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
		public String getCep() {
			return cep;
		}
		public void setCep(String cep) {
			this.cep = cep;
		}
		public String getGeocodeStatus() {
			return geocodeStatus;
		}
		public void setGeocodeStatus(String geocodeStatus) {
			this.geocodeStatus = geocodeStatus;
		}
		public String getUltimaAtualizacao() {
			return ultimaAtualizacao;
		}
		public void setUltimaAtualizacao(String ultimaAtualizacao) {
			this.ultimaAtualizacao = ultimaAtualizacao;
		}
		public Point getGeometry() {
			return geometry;
		}
		public void setGeometry(Point geometry) {
			this.geometry = geometry;
		}
	}
	
	public class Point{
		
		private String type = "Point";

		private Double[] coordinates = new Double[2];
		
		public Point(){}
		
		public Point(Double longitude, Double latitude){
			setLongitude(longitude);
			setLatitude(latitude);
		}

		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Double[] getCoordinates() {
			return coordinates;
		}
		public void setCoordinates(Double[] coordinates) {
			this.coordinates = coordinates;
		}
		public void setLongitude(Double longitude){
			this.coordinates[0] = longitude;
		}
		public Double getLongitude(){
			return this.coordinates[0];
		}
		public void setLatitude(Double latitude){
			this.coordinates[1] = latitude;
		}
		public Double getLatitude(){
			return this.coordinates[1];
		}
		public void setLngLat(Double longitude, Double latitude)
		{
			setLongitude(longitude);
			setLatitude(latitude);
		}

	}
}
