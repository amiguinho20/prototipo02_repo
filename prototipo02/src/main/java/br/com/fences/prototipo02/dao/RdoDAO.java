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
public class RdoDAO implements Serializable {

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
            
            RdoDAO rdoDao = new RdoDAO();
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
			//sql.append("and a.ano_referencia_bo is null "); //-- que nao seja complementar  
//			sql.append("and exists (  ");
//			sql.append("    select b.*  ");
//			sql.append("    from   db2aplicativos.tb_bo_natureza b ");
//			sql.append("    where ");
//			sql.append("	a.ano_bo = b.ano_bo and a.id_delegacia = b.id_delegacia and a.num_bo = b.num_bo and  ");
//			sql.append("	(   ");
//			sql.append("	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22010 and b.id_natureza = '155A' and b.id_conduta = 9)  ");
//			sql.append("or	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22010 and b.id_natureza = '155B' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22010 and b.id_natureza = '156' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22020 and b.id_natureza = '157' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22070 and b.id_natureza = '180A' and b.id_conduta = 1) ");
//			sql.append("or	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22070 and b.id_natureza = '180B' and b.id_conduta = 1) ");
//			sql.append("or	(b.id_ocorrencia = 10 and b.id_especie = 10 and b.id_subespecie = 22070 and b.id_natureza = '180C' and b.id_conduta = 1) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 10 and b.id_natureza = '155A' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 10 and b.id_natureza = '155B' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 10 and b.id_natureza = '156' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 20 and b.id_natureza = '157' and b.id_conduta = 9) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 70 and b.id_natureza = '180A' and b.id_conduta = 1) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 70 and b.id_natureza = '180B' and b.id_conduta = 1) ");
//			sql.append("or	(b.id_ocorrencia = 20 and b.id_especie = 20 and b.id_subespecie = 70 and b.id_natureza = '180C' and b.id_conduta = 1) ");
//			sql.append("	) ");
//			sql.append(") ");
//
			//conn = dataSource.getConnection();
			
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
				
				//List<int[]> listaComplementos = new ArrayList<>();
				//int[] bo = {idDelegacia, ano, numero};
				//listaComplementos.add(bo);
				
				//-- nao adicionar complementares por aqui, ele vira pela query
				//listarComplementares(idDelegacia, ano, numero, listaComplementos, conn);
				
				//for (int[] boAux : listaComplementos)
				//{
				//	String registro = consultar(boAux[0], boAux[1], boAux[2], conn);
				//	registros.add(registro);
				//}

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
	
    public void listarComplementares(int idDelegacia, int ano, int numero, List<int[]> listaComplementos, Connection conn){
    	
		Statement stmt = null;
		ResultSet rset = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("");
			
			sql.append("select a.id_delegacia, a.ano_bo, a.num_bo ");
			sql.append("from  ");
			sql.append("    db2aplicativos.tb_bo a  ");
			sql.append("where   ");
			sql.append("    a.ano_referencia_bo = " + ano + " ");
			sql.append("and a.NUM_REFERENCIA_BO = " + numero + " ");
			sql.append("and a.DELEG_REFERENCIA_BO = " + idDelegacia + " ");
			
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql.toString());
			
			while (rset.next()) {
				
				int idDelegaciaNovo = rset.getInt(1);
				int anoNovo = rset.getInt(2);
				int numeroNovo = rset.getInt(3);
				int[] bo = {idDelegaciaNovo, anoNovo, numeroNovo};
				
				listaComplementos.add(bo);

				//-- recursivo
				listarComplementares(idDelegaciaNovo, anoNovo, numeroNovo, listaComplementos, conn);
			}

		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset, stmt);
		}

    	
    }
    
    
	/**
	 * 
	 * colunas do * (22.06.2015)
	 * ID_DELEGACIA
	 * ANO_BO
	 * NUM_BO
	 * ID_TIPO_BO
	 * DELEG_REFERENCIA_BO
	 * ANO_REFERENCIA_BO
	 * NUM_REFERENCIA_BO
	 * DATA_OCORRENCIA_BO
	 * DATA_INICIAL_OCORR_INCERTA
	 * DATA_FINAL_OCORR_INCERTA
	 * HORA_OCORRENCIA_BO
	 * ID_PERIODO
	 * DATA_COMUNICACAO_BO
	 * HORA_COMUNICACAO_BO
	 * DATAHORA_REGISTRO_BO
	 * DATAHORA_IMPRESSAO_BO
	 * FLAG_FLAGRANTE
	 * PROVA_BO
	 * HISTORICO_BO
	 * FLAG_REPRESENTACAO_BO
	 * FLAG_EXPORTACAO
	 * FLAG_RDO_FINALIZADO
	 * RG_USUARIO
	 * RG_DELEGADO
	 * DELEG_ORIGEM_BO
	 * ANO_ORIGEM_BO
	 * NUM_ORIGEM_BO
	 * FLAG_CONCURSO_CRIME
	 * ID_TIPOLOCAL
	 * ID_SUBTIPOLOCAL
	 * ID_LOGRADOURO
	 * LOGRADOURO
	 * NUMERO_LOGRADOURO
	 * COMPLEMENTO
	 * CEP
	 * BAIRRO
	 * CIDADE
	 * ID_UF
	 * ID_CIRCUNSCRICAO
	 * ID_LOGRADOURO_REFERENCIA
	 * LOGRADOURO_REFERENCIA
	 * NUMERO_LOGRADOURO_REFERENCIA
	 * ID_PROVIDENCIA
	 * ID_EXAME
	 * ID_SOLUCAO
	 * FLAG_REFERENCIA
	 * DATAHORA_EXPORTACAO
	 * RG_DIGITADOR
	 * LUMINOSIDADE
	 * FLAG_AUTORIA_BO
	 * VERSAO
	 * NUM_MENSAGEM_BO
	 * NUM_OFICIO_BO
	 * DATAHORA_EXP_OMEGA
	 * FLAG_EXP_OMEGA
	 * FLAG_DEFENSORIA
	 * DATA_HORA_DEFENSORIA
	 * DATA_HORA_CARGA_DEFENSORIA
	 * DT_DEFENSORIA_CERTIFICACAO
	 * FLAG_CERTIFICACAO
	 * FLAG_CONFIDENCIAL
	 * ID_APRESENTACAO_OCORR
	 * FLAG_FORMA_APRESENTA
	 * FLAG_OUTRO_LUGAR_APRESENTA
	 * TEMPO_PERMANENCIA_APRESENTA
	 * LOCAL_APRES_ANTES
	 * APRESENTA_OCORR_OUTROS
	 * LATITUDE
	 * LONGITUDE
	 * FLAG_EXPORTACAO_CAP
	 * DATAHORA_EXPORTACAO_CAP
	 * ID_CIRCUNSCRICAO_CONF
	 * DATA_SYNCH_OFFLINE
	 * RG_USUARIO_SYNCH_OFFLINE
	 * NUM_OFFLINE
	 * MOTIVO_TRANSFERENCIA
	 * LATITUDE_REF
	 * LONGITUDE_REF
	 * ID_LOGRADOURO_GEO_REF
	 * ID_LOGRADOURO_GEO
	 * HISTORICO_RESUMIDO
	 * ID_MUNICIPIO
	 * 
	 * colunas de join
	 * NOME_DELEGACIA
	 * DESCR_TIPO_BO
	 * DESCR_CIRCUNSCRICAO
	 * DESCR_PERIODO
	 * DESCR_TIPOLOCAL
	 * DESCR_SUBTIPOLOCAL
	 * NOME_UF
	 * DESCR_PROVIDENCIA
	 * DESCR_EXAME
	 * DESCR_SOLUCAO
	 * 
	 * 
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param connec
	 * @return
	 */
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
				sql.append("	a.*, ");
				sql.append("	b.NOME_DELEGACIA,  ");
				sql.append("	c.DESCR_TIPO_BO, ");
				sql.append("	d.DESCR_CIRCUNSCRICAO, ");
				sql.append("	e.DESCR_PERIODO, ");
				sql.append("	f.DESCR_TIPOLOCAL, ");
				sql.append("	g.DESCR_SUBTIPOLOCAL,  ");
				sql.append("	l.NOME_UF, ");
				sql.append("	h.DESCR_PROVIDENCIA,  ");
				sql.append("	i.DESCR_EXAME, ");
				sql.append("	j.DESCR_SOLUCAO,  ");
				sql.append("	bref.NOME_DELEGACIA NOME_DELEGACIA_REFERENCIA_BO,  ");
				sql.append("	borigem.NOME_DELEGACIA NOME_DELEGACIA_ORIGEM_BO ");
				sql.append("FROM  ");
				sql.append("	db2aplicativos.TB_BO a  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_DELEGACIA bref ON a.DELEG_REFERENCIA_BO = bref.ID_DELEGACIA  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_DELEGACIA borigem ON a.DELEG_ORIGEM_BO = borigem.ID_DELEGACIA  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_PERIODO e ON a.ID_PERIODO = e.ID_PERIODO  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_SUBTIPOLOCAL g ON a.ID_SUBTIPOLOCAL = g.ID_SUBTIPOLOCAL  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_PROVIDENCIA h ON a.ID_PROVIDENCIA = h.ID_PROVIDENCIA  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_EXAME i ON a.ID_EXAME = i.ID_EXAME  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_SOLUCAO j ON a.ID_SOLUCAO = j.ID_SOLUCAO  ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_UF l ON a.ID_UF = l.ID_UF,  ");
				sql.append("	db2aplicativos.TB_DELEGACIA b, ");
				sql.append("	db2aplicativos.TB_TIPO_BO c,  ");
				sql.append("	db2aplicativos.TB_CIRCUNSCRICAO d,  ");
				sql.append("	db2aplicativos.TB_TIPOLOCAL f  ");
				sql.append("WHERE ");
				sql.append("	a.ID_DELEGACIA = b.ID_DELEGACIA  ");
				sql.append("AND a.ID_TIPO_BO = c.ID_TIPO_BO  ");
				sql.append("AND a.ID_CIRCUNSCRICAO = d.ID_CIRCUNSCRICAO ");
				sql.append("AND a.ID_TIPOLOCAL = g.ID_TIPOLOCAL  ");
				sql.append("AND g.ID_TIPOLOCAL = f.ID_TIPOLOCAL ");
				sql.append("and	a.ID_DELEGACIA = ? ");
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
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);

					//-- recuperacao do valor no resultSet
					String valor = converterValorParaString(rset, coluna);
					
					//-- tratamento para hora
					if (coluna.equals("HORA_OCORRENCIA_BO"))
					{
						valor = horaRdo(valor);
					}
					
					String atributo = criarAtributoJson(coluna, valor);
					
					adicionarAtributoJson(atributos, atributo);
					
				}
				adicionarAtributoJson(atributos, consultarNaturezas(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarVeiculos(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarPessoas(idDelegacia, ano, numero, conn));

				adicionarAtributoJson(atributos, consultarObjetos(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarCargas(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarArmas(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarEmpresas(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarEntorpecentes(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarModusOperandis(idDelegacia, ano, numero, conn));
				adicionarAtributoJson(atributos, consultarEspolios(idDelegacia, ano, numero, conn));

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

	/**
	 * 
	 * colunas do * (22.06.2015)
	 * ID_DELEGACIA (chave excluida)
	 * ANO_BO (chave excluida)
	 * NUM_BO (chave excluida)
	 * CONT_NATUREZA
	 * ID_OCORRENCIA
	 * ID_ESPECIE
	 * ID_SUBESPECIE
	 * ID_NATUREZA
	 * FLAG_STATUS
	 * ID_CONDUTA
	 * 
	 * colunas de join
	 * DESCR_OCORRENCIA
	 * DESCR_ESPECIE
	 * DESCR_SUBESPECIE
	 * RUBRICA_NATUREZA
	 * DESCR_CONDUTA
	 * 
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param connec
	 * @return
	 */
	private String consultarNaturezas(int idDelegacia, int ano, int numero, Connection conn)
	{
		String json = "";
		
		ResultSet rset = null;

		try {
			final String PREPARE = "tb_bo_natureza";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(PREPARE))
			{
				pstm = mapPrepStatement.get(PREPARE);
			}
			else
			{
				StringBuilder sql = new StringBuilder();
				sql.append("");
				
				sql.append("select ");
			    sql.append("    a.*, ");
			    sql.append("    (select y.descr_ocorrencia from db2aplicativos.tb_ocorrencia y where y.id_ocorrencia = a.id_ocorrencia) DESCR_OCORRENCIA,  ");
			    sql.append("    (select y.descr_especie from db2aplicativos.tb_especie y where y.id_ocorrencia = a.id_ocorrencia and y.id_especie = a.id_especie) DESCR_ESPECIE,  ");
			    sql.append("    (select y.descr_subespecie from db2aplicativos.tb_subespecie y where y.id_ocorrencia = a.id_ocorrencia and y.id_especie = a.id_especie and y.id_subespecie = a.id_subespecie) DESCR_SUBESPECIE,  ");
			    sql.append("    (select y.rubrica from db2aplicativos.tb_natureza y where y.id_ocorrencia = a.id_ocorrencia and y.id_especie = a.id_especie and y.id_subespecie = a.id_subespecie and y.id_natureza = a.id_natureza) RUBRICA_NATUREZA,  ");
			    sql.append("    (select y.descr_conduta from db2aplicativos.tb_conduta y where y.id_ocorrencia = a.id_ocorrencia and y.id_especie = a.id_especie and y.id_subespecie = a.id_subespecie and y.id_natureza = a.id_natureza and y.id_conduta = a.id_conduta) DESCR_CONDUTA  ");
			    sql.append("from  ");
			    sql.append("    db2aplicativos.tb_bo_natureza a ");
			    sql.append("where ");
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
			
			List<String> naturezas = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);
					
					if (!colunasDescartadas.contains(coluna)){
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
				}
				
				int contNatureza = rset.getInt("CONT_NATUREZA");
				int idOcorrencia = rset.getInt("ID_OCORRENCIA");
				int idEspecie = rset.getInt("ID_ESPECIE");
				int idSubespecie = rset.getInt("ID_SUBESPECIE");
				String idNatureza = rset.getString("ID_NATUREZA");
				
				adicionarAtributoJson(
						atributos,
						consultarNaturezasDesdobramentos(idDelegacia, ano,
								numero, contNatureza, idOcorrencia, idEspecie,
								idSubespecie, idNatureza, conn));	
				
				adicionarAtributoJson(
						atributos,
						consultarNaturezasCircunstancias(idDelegacia, ano,
								numero, contNatureza, idOcorrencia, idEspecie,
								idSubespecie, idNatureza, conn));		
				
				json = criarJson(atributos);
				naturezas.add(json);
			}
			json = criarJsonArray("NATUREZA", naturezas);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}
	
	/**
	 * bo.natureza.desdobramento
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contNatureza
	 * @param idOcorrencia
	 * @param idEspecie
	 * @param idSubespecie
	 * @param idNatureza
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarNaturezasDesdobramentos(int idDelegacia, int ano,
			int numero, int contNatureza, int idOcorrencia, int idEspecie,
			int idSubespecie, String idNatureza, Connection conn)
			throws SQLException
	{
		final String PREPARE = "tb_bo_desdobramento";
		
		String json = "";
		
		PreparedStatement pstm = null;
		if (mapPrepStatement.containsKey(PREPARE))
		{
			pstm = mapPrepStatement.get(PREPARE);
		}
		else
		{
			StringBuilder sql = new StringBuilder();
			sql.append("select ");
			sql.append("	a.ID_DESDOBRAMENTO, ");
			sql.append("	b.DESCR_DESDOBRAMENTO ");
			sql.append("from ");
			sql.append("	db2aplicativos.tb_bo_desdobramento a, ");
			sql.append("	db2aplicativos.tb_desdobramento b ");
			sql.append("where ");
			sql.append("	a.id_delegacia = ? ");
			sql.append("and a.ano_bo = ? ");
			sql.append("and a.num_bo = ? ");
			sql.append("and a.cont_natureza = ? ");
			sql.append("and	a.id_ocorrencia = ? ");
			sql.append("and	a.id_especie = ? ");
			sql.append("and a.id_subespecie = ? ");
			sql.append("and a.id_natureza = ? ");    
			sql.append("and	b.id_ocorrencia = a.id_ocorrencia ");
			sql.append("and	b.id_especie = a.id_especie ");
			sql.append("and b.id_subespecie = a.id_subespecie ");
			sql.append("and	b.id_natureza = a.id_natureza ");
			sql.append("and	b.id_desdobramento = a.id_desdobramento ");
			sql.append("order by 1");
			
			pstm = conn.prepareStatement(sql.toString());
			mapPrepStatement.put(PREPARE, pstm);
		}
		pstm.setInt(1, idDelegacia);
		pstm.setInt(2, ano);
		pstm.setInt(3, numero);
		pstm.setInt(4, contNatureza);
		pstm.setInt(5, idOcorrencia);
		pstm.setInt(6, idEspecie);
		pstm.setInt(7, idSubespecie);
		pstm.setString(8, idNatureza);
		
		ResultSet rset = null;

		try 
		{
			rset = pstm.executeQuery();
			
			List<String> desdobramentos = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);

					//-- recuperacao do valor no resultSet
					String valor = converterValorParaString(rset, coluna);
					String atributo = criarAtributoJson(coluna, valor);
					adicionarAtributoJson(atributos, atributo);
				}
				int idDesdobramento = rset.getInt("ID_DESDOBRAMENTO");
				
				adicionarAtributoJson(
						atributos,
						consultarNaturezasDesdobramentosModalidades(
								idDelegacia, ano, numero, contNatureza,
								idOcorrencia, idEspecie, idSubespecie,
								idNatureza, idDesdobramento, conn));				
				
				json = criarJson(atributos);
				desdobramentos.add(json);
			}
			json = criarJsonArray("DESDOBRAMENTO", desdobramentos);
		} 
		finally 
		{
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}
	
	/**
	 * bo.natureza.desdobramento.modalidade
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contNatureza
	 * @param idOcorrencia
	 * @param idEspecie
	 * @param idSubespecie
	 * @param idNatureza
	 * @param idDesdobramento
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarNaturezasDesdobramentosModalidades(int idDelegacia, int ano,
			int numero, int contNatureza, int idOcorrencia, int idEspecie,
			int idSubespecie, String idNatureza, int idDesdobramento, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_MODALIDADE, ");
		sql.append("	b.RUBRICA ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_modalidade a, ");
		sql.append("	db2aplicativos.tb_modalidade b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_natureza = ? ");
		sql.append("and	a.id_ocorrencia = ? ");
		sql.append("and	a.id_especie = ? ");
		sql.append("and a.id_subespecie = ? ");
		sql.append("and a.id_natureza = ? ");  
		sql.append("and a.id_desdobramento = ? "); 
		sql.append("and	b.id_ocorrencia = a.id_ocorrencia ");
		sql.append("and	b.id_especie = a.id_especie ");
		sql.append("and b.id_subespecie = a.id_subespecie ");
		sql.append("and	b.id_natureza = a.id_natureza ");
		sql.append("and	b.id_desdobramento = a.id_desdobramento ");
		sql.append("and	b.id_modalidade = a.id_modalidade ");
		sql.append("order by 1");
		
		json = prepararConsultarIterar(conn, "MODALIDADE",
				"tb_bo_modalidade", sql.toString(), idDelegacia, ano,
				numero, contNatureza, idOcorrencia, idEspecie, idSubespecie,
				idNatureza, idDesdobramento);

		return json;
		
	}

	/**
	 * bo.natureza.circunstancia
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contNatureza
	 * @param idOcorrencia
	 * @param idEspecie
	 * @param idSubespecie
	 * @param idNatureza
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarNaturezasCircunstancias(int idDelegacia, int ano,
			int numero, int contNatureza, int idOcorrencia, int idEspecie,
			int idSubespecie, String idNatureza, Connection conn)
			throws SQLException
	{
		
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_CIRCUNSTANCIA, ");
		sql.append("	b.DESCR_CIRCUNSTANCIA ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_circunstancia a, ");
		sql.append("	db2aplicativos.tb_circunstancia b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_natureza = ? ");
		sql.append("and	a.id_ocorrencia = ? ");
		sql.append("and	a.id_especie = ? ");
		sql.append("and a.id_subespecie = ? ");
		sql.append("and a.id_natureza = ? ");   
		sql.append("and	b.id_ocorrencia = a.id_ocorrencia ");
		sql.append("and	b.id_especie = a.id_especie ");
		sql.append("and b.id_subespecie = a.id_subespecie ");
		sql.append("and	b.id_natureza = a.id_natureza ");
		sql.append("and	b.id_circunstancia = a.id_circunstancia ");
		sql.append("order by 1");
		
		json = prepararConsultarIterar(conn, "CIRCUNSTANCIA",
				"tb_bo_circunstancia", sql.toString(), idDelegacia, ano,
				numero, contNatureza, idOcorrencia, idEspecie, idSubespecie,
				idNatureza);

		return json;
	}

	
	/**
	 * bo.veiculo
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param connec
	 * @return
	 */
	private String consultarVeiculos(int idDelegacia, int ano, int numero, Connection conn)
	{
		String json = "";
		
		ResultSet rset = null;

		try {
			final String PREPARE = "tb_bo_veiculo";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(PREPARE))
			{
				pstm = mapPrepStatement.get(PREPARE);
			}
			else
			{			
				StringBuilder sql = new StringBuilder();
				sql.append("");
				
				sql.append("SELECT  ");
				sql.append("    a.*, ");
				sql.append("    b.DESCR_TIPO_VEICULO, ");
				sql.append("    p.DESCR_COMBUSTIVEL, ");
				sql.append("    d.DESCR_COR_VEICULO, ");
				sql.append("    c.DESCR_MARCA_VEICULO, ");
				sql.append("    q.DESCR_OCORRENCIA_VEICULO, ");
				sql.append("    r.DESCR_TIPOLOCAL ");
				sql.append("FROM  db2aplicativos.TB_BO_VEICULO a   ");
				sql.append("LEFT OUTER JOIN  db2aplicativos.TB_TIPO_VEICULO b  ON  a.ID_TIPO_VEICULO  = b.ID_TIPO_VEICULO     ");
				sql.append("LEFT OUTER JOIN  db2aplicativos.TB_MARCA_VEICULO c  ON  a.ID_MARCA_VEICULO  = c.ID_MARCA_VEICULO     ");
				sql.append("LEFT OUTER JOIN  db2aplicativos.TB_COR_VEICULO d  ON  a.ID_COR_VEICULO  = d.ID_COR_VEICULO    "); 
				sql.append("LEFT OUTER JOIN  db2aplicativos.TB_COMBUSTIVEL p  ON  a.ID_COMBUSTIVEL  = p.ID_COMBUSTIVEL   ");  
				sql.append("LEFT OUTER JOIN  db2aplicativos.TB_OCORRENCIA_VEICULO q  ON  a.ID_OCORRENCIA_VEICULO  = q.ID_OCORRENCIA_VEICULO    "); 
				sql.append("LEFT OUTER JOIN  db2aplicativos.TB_TIPOLOCAL r  ON  a.ID_TIPOLOCAL  = r.ID_TIPOLOCAL ");
				sql.append("WHERE	  ");
				sql.append("	a.ID_DELEGACIA = ? ");
				sql.append("and a.ANO_BO = ? ");
				sql.append("and a.NUM_BO = ? ");
				sql.append("ORDER BY a.CONT_VEICULO ");
				
				pstm = conn.prepareStatement(sql.toString());
				mapPrepStatement.put(PREPARE, pstm);
			}
			pstm.setInt(1, idDelegacia);
			pstm.setInt(2, ano);
			pstm.setInt(3, numero);

			rset = pstm.executeQuery();
			
			List<String> veiculos = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);
					
					if (!colunasDescartadas.contains(coluna))
					{
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
				}
				int contVeiculo = rset.getInt("CONT_VEICULO");
				adicionarAtributoJson(atributos, consultarVeiculosSegurancas(idDelegacia, ano, numero, contVeiculo, conn));
				adicionarAtributoJson(atributos, consultarVeiculosAcessos(idDelegacia, ano, numero, contVeiculo, conn));
				adicionarAtributoJson(atributos, consultarVeiculosAcionamentos(idDelegacia, ano, numero, contVeiculo, conn));
				adicionarAtributoJson(atributos, consultarVeiculosInstrumentos(idDelegacia, ano, numero, contVeiculo, conn));
				adicionarAtributoJson(atributos, consultarVeiculosCargas(idDelegacia, ano, numero, contVeiculo, conn));
				
				json = criarJson(atributos);
				veiculos.add(json);
			}
			if (!veiculos.isEmpty())
			{
				json = criarJsonArray("VEICULO", veiculos);
			}
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}
	
	/**
	 * bo.pessoa
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param connec
	 * @return
	 */
	private String consultarPessoas(int idDelegacia, int ano, int numero, Connection conn)
	{
		String json = "";
		
		ResultSet rset = null;

		try {
			
			final String prepare = "tb_bo_pessoa";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(prepare))
			{
				pstm = mapPrepStatement.get(prepare);
			}
			else
			{
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT ");
				sql.append("	a.*, ");
				sql.append("	e.DESCR_TIPO_PESSOA, ");
				sql.append("	h.DESCR_ESTADO_CIVIL,  ");
				sql.append("	ufrg.NOME_UF UF_RG,  ");
				sql.append("	ufcart.NOME_UF UF_CART, ");
				sql.append("	f.DESCR_PROFISSAO,  ");
				sql.append("	i.DESCR_GRAU_INSTRUCAO,  ");
				sql.append("	b.PESO_CARACTERISTICA, ");
				sql.append("	b.ALTURA_CARACTERISTICA,  ");	
				sql.append("	b.COMPLEICAO_CARACTERISTICA, ");
				sql.append("	c.DESCR_TIPO_CABELO,  ");
				sql.append("	b.ID_TIPO_CABELO, ");
				sql.append("	b.COMPRIMENTO_CABELO_CARACT,  ");
				sql.append("	b.ID_COR_CABELO, ");
				sql.append("	dcabelo.COR DESCR_COR_CABELO,  ");
				sql.append("	b.ID_COR_OLHO,  ");
				sql.append("	dolho.COR DESCR_COR_OLHO, "); 
				sql.append("	b.ID_COR_CUTIS,  ");
				sql.append("	dcutis.COR DESCR_COR_CUTIS,  ");
				sql.append("	b.OBSERVACOES  ");
				sql.append("FROM  ");
				sql.append("	db2aplicativos.TB_BO_PESSOA a ");
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_CARACTERISTICA_FISICA b ON ");
				sql.append("		a.ID_DELEGACIA = b.ID_DELEGACIA  ");
				sql.append("	AND	a.ANO_BO = b.ANO_BO ");
				sql.append("	AND	a.NUM_BO = b.NUM_BO  ");
				sql.append("	AND	a.CONT_PESSOA = b.CONT_PESSOA  ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_TIPO_CABELO c ON ");
				sql.append("		b.ID_TIPO_CABELO = c.ID_TIPO_CABELO  ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_COR dcabelo ON b.ID_COR_CABELO = dcabelo.ID_COR  ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_COR dolho ON b.ID_COR_OLHO = dolho.ID_COR ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_COR dcutis ON b.ID_COR_CUTIS = dcutis.ID_COR  ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_PROFISSAO f ON a.ID_PROFISSAO = f.ID_PROFISSAO  ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_ESTADO_CIVIL h ON a.ID_ESTADO_CIVIL = h.ID_ESTADO_CIVIL "); 
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_GRAU_INSTRUCAO i ON a.ID_GRAU_INSTRUCAO = i.ID_GRAU_INSTRUCAO "); 
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_UF ufrg ON a.RG_UF = ufrg.ID_UF  ");
				sql.append("LEFT OUTER JOIN db2aplicativos.TB_UF ufcart ON a.UF_CART_PROF = ufcart.ID_UF, ");
				sql.append("	db2aplicativos.TB_TIPO_PESSOA e ");
				sql.append("WHERE  ");
				sql.append("	a.ID_TIPO_PESSOA = e.ID_TIPO_PESSOA ");
				sql.append("AND	a.ID_DELEGACIA = ?  ");
				sql.append("AND	a.ANO_BO = ?  ");
				sql.append("AND	a.NUM_BO = ? ");
				sql.append("ORDER BY a.CONT_PESSOA ");
				
				pstm = conn.prepareStatement(sql.toString());
				mapPrepStatement.put(prepare, pstm);
			}
			pstm.setInt(1, idDelegacia);
			pstm.setInt(2, ano);
			pstm.setInt(3, numero);
			
			rset = pstm.executeQuery();
			
			List<String> veiculos = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);
					
					if (!colunasDescartadas.contains(coluna))
					{
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
				}
				int contPessoa = rset.getInt("CONT_PESSOA");
				adicionarAtributoJson(atributos, consultarPessoasAdornos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasDeformidades(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasPatologias(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasBancos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasCartaoCreditos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasEnderecos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasTelefones(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasVestuarios(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasNaturezas(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasLocalFrequentas(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasToxicos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasJogos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasCondutores(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasVitimas(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasAutores(idDelegacia, ano, numero, contPessoa, conn));
				
				
				json = criarJson(atributos);
				veiculos.add(json);
			}
			if (!veiculos.isEmpty())
			{
				json = criarJsonArray("PESSOA", veiculos);
			}
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}

	/**
	 * bo.pessoa.vitima
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 */
	private String consultarPessoasVitimas(int idDelegacia, int ano, int numero, int contPessoa, Connection conn)
	{
		String json = "";
		
		ResultSet rset = null;

		try {
			final String prepare = "tb_pessoa_vitima";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(prepare))
			{
				pstm = mapPrepStatement.get(prepare);
			}
			else
			{
				
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT  ");
				sql.append("	a.FLAG_VITIMA_FATAL,  ");
				sql.append("	a.FLAG_A_CR_VITIM,  ");
				sql.append("	a.FLAG_BEBIDA_ALCOOLICA_VITIMA,  ");
				sql.append("	a.FLAG_DIVIDA_VITIMA,  ");
				sql.append("	a.ID_RELIGIAO,  ");
				sql.append("	d.DESCR_RELIGIAO, "); 
				sql.append("	a.TIPO_SANGUINEO_VITIMA,  ");
				sql.append("	a.FLAG_INTERNADO_VITIMA,  ");
				sql.append("	a.LOCAL_INTERNACAO_VITIMA,  ");
				sql.append("	a.FLAG_TRANSPORTADA_VITIMA, "); 
				sql.append("	a.LOCAL_CATIVEIRO_VITIMA, "); 
				sql.append("	a.LOCAL_ABANDONO_VITIMA,  ");
				sql.append("	a.DATA_LIBERTACAO_VITIMA, "); 
				sql.append("	a.HORA_LIBERTACAO_VITIMA,  ");
				sql.append("	a.FLAG_DESP_ANTERIOR_V,  ");
				sql.append("	a.RELACIONAMENTO_FAMILIA_VITIMA,  ");
				sql.append("	a.FLAG_SEGURO_VIDA_VITIMA,  ");
				sql.append("	a.BENEFICIARIOS_VITIMA,  ");
				sql.append("	a.FLAG_EM_SERVICO,  ");
				sql.append("	a.ID_POSICAO_CADAVER,  ");
				sql.append("	e.DESCR_POSICAO_CADAVER,  ");
				sql.append("	a.TEMPO_PROVAVEL_MORTE,  ");
				sql.append("	b.FLAG_SEQUESTRO_VITIMA_CARGA,  ");
				sql.append("	b.FLAG_CARCERE_VITIMA_CARGA,  ");
				sql.append("	b.FLAG_VITIMA_ANTES_VITIMA_CARGA,  ");
				sql.append("	b.CIRCUNSC_ANTES_VITIMA_CARG,  ");
				sql.append("	b.LOCAL_ANTES_VITIMA_CARGA,  ");
				sql.append("	b.DATA_ANTES_VITIMA_CARGA,  ");
				sql.append("	b.ID_TIPO_CARGA_ANTES, "); 
				sql.append("	c.DESCR_TIPO_CARGA  ");
				sql.append("FROM   "); 
				sql.append("	db2aplicativos.TB_PESSOA_VITIMA a  ");  
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_RELIGIAO d ON a.ID_RELIGIAO = d.ID_RELIGIAO   ");   
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_POSICAO_CADAVER e ON a.ID_POSICAO_CADAVER = e.ID_POSICAO_CADAVER   ");   
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_VITIMA_CARGA b ON   ");
				sql.append("		a.ID_DELEGACIA = b.ID_DELEGACIA   ");
				sql.append("	AND	a.ANO_BO  = b.ANO_BO   ");
				sql.append("	AND	a.NUM_BO  = b.NUM_BO   ");
				sql.append("	AND	a.CONT_PESSOA  = b.CONT_PESSOA  ");    
				sql.append("	LEFT OUTER JOIN db2aplicativos.TB_TIPO_CARGA c ON b.ID_TIPO_CARGA_ANTES = c.ID_TIPO_CARGA  ");
				sql.append("WHERE	 "); 	  
				sql.append("	a.ID_DELEGACIA  = ?  ");
				sql.append("AND	a.ANO_BO  = ?  ");
				sql.append("AND	a.NUM_BO  = ?  ");
				sql.append("AND	a.CONT_PESSOA  = ?  ");
				
				pstm = conn.prepareStatement(sql.toString());
				mapPrepStatement.put(prepare, pstm);
			}
			pstm.setInt(1, idDelegacia);
			pstm.setInt(2, ano);
			pstm.setInt(3, numero);
			pstm.setInt(4, contPessoa);

			rset = pstm.executeQuery();
			
			List<String> vitimas = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);
					
					if (!colunasDescartadas.contains(coluna))
					{
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
				}
				adicionarAtributoJson(atributos, consultarPessoasVitimasFerimentos(idDelegacia, ano, numero, contPessoa, conn));
				
				json = criarJson(atributos);
				vitimas.add(json);
			}
			if (!vitimas.isEmpty())
			{
				json = criarJsonArray("VITIMA", vitimas);
			}
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}

	/**
	 * bo.pessoa.autor
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 */
	private String consultarPessoasAutores(int idDelegacia, int ano, int numero, int contPessoa, Connection conn)
	{
		String json = "";
		
		ResultSet rset = null;

		try {
			final String prepare = "tb_pessoa_autor";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(prepare))
			{
				pstm = mapPrepStatement.get(prepare);
			}
			else
			{
				
				StringBuilder sql = new StringBuilder();
				sql.append("select  "); 
				sql.append("	b.ID_RELIGIAO,  ");
				sql.append("	c.DESCR_RELIGIAO,  ");
				sql.append("	b.FLAG_BEBIDA_ALCOOLICA_AUTOR,  ");
				sql.append("	b.FLAG_DIVIDA_AUTOR, "); 
				sql.append("	b.TIPO_SANGUINEO_AUTOR, "); 
				sql.append("	b.RELACIONAMENTO_FAMILIA_AUTOR,  ");
				sql.append("	b.MOTIVO_PRISAO_ANTERIOR_AUTOR, ");
				sql.append("	b.FLAG_ANTECEDENTE_CRIM_AUTOR,  ");
				sql.append("	b.PARENTESCO_VITIMA_AUTOR  ");
				sql.append("from  ");
				sql.append("	db2aplicativos.TB_PESSOA_AUTOR b  ");
				sql.append("	left outer join db2aplicativos.TB_RELIGIAO c on b.ID_RELIGIAO = c.ID_RELIGIAO  ");
				sql.append("where  ");
				sql.append("	b.ID_DELEGACIA = ?  ");
				sql.append("and	b.ANO_BO = ?  ");
				sql.append("and b.NUM_BO = ?  ");
				sql.append("and	b.CONT_PESSOA = ? ");
				
				pstm = conn.prepareStatement(sql.toString());
				mapPrepStatement.put(prepare, pstm);
			}
			pstm.setInt(1, idDelegacia);
			pstm.setInt(2, ano);
			pstm.setInt(3, numero);
			pstm.setInt(4, contPessoa);

			rset = pstm.executeQuery();
			
			List<String> vitimas = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);
					
					if (!colunasDescartadas.contains(coluna))
					{
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
				}
				adicionarAtributoJson(atributos, consultarPessoasModusOperandiAutores(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasAutorLinguagens(idDelegacia, ano, numero, contPessoa, conn));

				json = criarJson(atributos);
				vitimas.add(json);
			}
			if (!vitimas.isEmpty())
			{
				json = criarJsonArray("AUTOR", vitimas);
			}
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}
	
	/**
	 * bo.pessoa.autor.modusoperandi
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 */
	private String consultarPessoasModusOperandiAutores(int idDelegacia, int ano, int numero, int contPessoa, Connection conn)
	{
		String json = "";
		
		ResultSet rset = null;

		try {
			final String prepare = "tb_modus_operandi_autor";
			
			PreparedStatement pstm = null;
			if (mapPrepStatement.containsKey(prepare))
			{
				pstm = mapPrepStatement.get(prepare);
			}
			else
			{
				
				StringBuilder sql = new StringBuilder();
				sql.append("select "); 
				sql.append("	c.CARACTERISTICA_VEICULO_AUTOR,  "); 
				sql.append("	c.FLAG_ALTERACAO_VOZ_AUTOR,  "); 
				sql.append("	c.TIPO_PROPRIET_VEICULO_AUTOR,  ");
				sql.append("	c.FLAG_ALT_CARACT_PSICO_AUTOR,   ");
				sql.append("	c.FLAG_HABITO_VITIMA_AUTOR,  "); 
				sql.append("	c.FLAG_HABITO_FAMILIA_AUTOR,  ");
				sql.append("	c.FLAG_LAZER_TRABALHO_AUTOR,  "); 
				sql.append("	c.PLACA_VEICULO,  "); 
				sql.append("	c.DESCR_ITINERARIO_AUTOR,   ");
				sql.append("	c.AMEACA_PROFERIDA_AUTOR  "); 
				sql.append("from  ");
				sql.append("	db2aplicativos.TB_MODUS_OPERANDI_AUTOR c  ");
				sql.append("where  ");
				sql.append("	c.ID_DELEGACIA = ?   ");
				sql.append("and	c.ANO_BO = ?   ");
				sql.append("and	c.NUM_BO = ?   ");
				sql.append("and	c.CONT_PESSOA = ?   ");
				
				pstm = conn.prepareStatement(sql.toString());
				mapPrepStatement.put(prepare, pstm);
			}
			pstm.setInt(1, idDelegacia);
			pstm.setInt(2, ano);
			pstm.setInt(3, numero);
			pstm.setInt(4, contPessoa);

			
			rset = pstm.executeQuery();
			
			List<String> vitimas = new ArrayList<>();
			while (rset.next()) {

				List<String> atributos = new ArrayList<>();
				
				//-- recuperacao das colunas de cabecalho
				ResultSetMetaData rsmd = rset.getMetaData();
				int qtdColuna = rsmd.getColumnCount();
				for (int i = 1; i <= qtdColuna; i++) {
				
					String coluna = rsmd.getColumnName(i);
					
					if (!colunasDescartadas.contains(coluna))
					{
						//-- recuperacao do valor no resultSet
						String valor = converterValorParaString(rset, coluna);
						String atributo = criarAtributoJson(coluna, valor);
						adicionarAtributoJson(atributos, atributo);
					}
				}
				adicionarAtributoJson(atributos, consultarPessoasModusOperandiCaracPsicos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasModusOperandiAbordagens(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasModusOperandiCoacao(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasModusOperandiInstrumentos(idDelegacia, ano, numero, contPessoa, conn));
				adicionarAtributoJson(atributos, consultarPessoasModusOperandiTelefones(idDelegacia, ano, numero, contPessoa, conn));
				
				json = criarJson(atributos);
				vitimas.add(json);
			}
			if (!vitimas.isEmpty())
			{
				json = criarJsonArray("MODUS_OPERANDI", vitimas);
			}
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException(e);
		} finally {
			DaoUtil.fecharRecurso(rset);
		}
		return json;
	}


	/**
	 * bo.pessoa.autor.modusoperandi.caracteristicapsicologica
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasModusOperandiCaracPsicos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select "); 
		sql.append("	d.ID_CARACTERISTICA_PSICOLOGICA,  ");  
		sql.append("	e.DECR_CARAC_PSICO   "); 
		sql.append("from   "); 
		sql.append("	db2aplicativos.TB_MO_CARAC_PSICO d,  "); 
		sql.append("	db2aplicativos.TB_CARACTERISTICA_PSICOLOGICA e  "); 
		sql.append("where   "); 
		sql.append("	d.ID_CARACTERISTICA_PSICOLOGICA = e.ID_CARACTERISTICA_PSICOLOGICA   "); 
		sql.append("and d.ID_DELEGACIA = ?   "); 
		sql.append("and	d.ANO_BO = ?   "); 
		sql.append("and	d.NUM_BO = ?   "); 
		sql.append("and	d.CONT_PESSOA = ?   "); 
		sql.append("order by d.ID_CARACTERISTICA_PSICOLOGICA  "); 
		
		json = prepararConsultarIterar(conn, "CARACTERISTICA_PSICOLOGICA", "tb_mo_carac_psico",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.autor.modusoperandi.abordagem
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasModusOperandiAbordagens(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	d.ID_ABORDAGEM, ");  
		sql.append("	e.DESCR_ABORDAGEM   ");
		sql.append("from  "); 
		sql.append("	db2aplicativos.TB_MO_ABORDAGEM d,   ");
		sql.append("	db2aplicativos.TB_ABORDAGEM e  ");
		sql.append("where  ");
		sql.append("	d.ID_ABORDAGEM = e.ID_ABORDAGEM   ");
		sql.append("and d.ID_DELEGACIA = ?  ");
		sql.append("and	d.ANO_BO = ?   ");
		sql.append("and	d.NUM_BO = ?  "); 
		sql.append("and	d.CONT_PESSOA = ?  "); 
		sql.append("order by d.ID_ABORDAGEM  ");
		
		json = prepararConsultarIterar(conn, "ABORDAGEM", "tb_mo_abordagem",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.autor.modusoperandi.coacao
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasModusOperandiCoacao(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select "); 
		sql.append("	f.ID_TIPO_COACAO,  "); 
		sql.append("	g.DESCR_TIPO_COACAO   ");
		sql.append("from   ");
		sql.append("	db2aplicativos.TB_MO_COACAO f,  "); 
		sql.append("	db2aplicativos.TB_TIPO_COACAO g  ");
		sql.append("where  ");
		sql.append(" 	f.ID_TIPO_COACAO = g.ID_TIPO_COACAO  "); 
		sql.append("and f.ID_DELEGACIA = ?   ");
		sql.append("and	f.ANO_BO = ?   ");
		sql.append("and	f.NUM_BO = ?   ");
		sql.append("and	f.CONT_PESSOA = ?   ");
		sql.append("order by f.ID_TIPO_COACAO  ");
		
		json = prepararConsultarIterar(conn, "COACAO", "tb_mo_coacao",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.autor.modusoperandi.instrumento
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasModusOperandiInstrumentos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select "); 
		sql.append("h.ID_INSTRUMENTO,   ");
		sql.append("i.DESCR_INSTRUMENTO   ");
		sql.append("from   ");
		sql.append("db2aplicativos.TB_MO_INSTRUMENTOS h,   ");
		sql.append("db2aplicativos.TB_INSTRUMENTO i  ");
		sql.append("where  ");
		sql.append(" 	h.ID_INSTRUMENTO = i.ID_INSTRUMENTO  ");
		sql.append("and 	h.ID_DELEGACIA = ?   ");
		sql.append("and	h.ANO_BO = ?  "); 
		sql.append("and	h.NUM_BO = ?   ");
		sql.append("and	h.CONT_PESSOA = ?   ");
		sql.append("order by h.ID_INSTRUMENTO  ");
		
		json = prepararConsultarIterar(conn, "INSTRUMENTO", "tb_mo_instrumento",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.autor.modusoperandi.telefone
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasModusOperandiTelefones(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("	j.DDD_TELEFONE, ");
		sql.append("	j.NUMERO_TELEFONE, ");
		sql.append("	j.DATA_TELEFONE, ");
		sql.append("	j.HORA_TELEFONE, ");
		sql.append("	j.SEXO_LIGACAO_TELEFONE, ");
		sql.append("	j.FLAG_FALA_NO_BOCAL_TELEFONE, ");
		sql.append("	l.ID_RUIDO_TELEFONE, ");
		sql.append("	m.DESCR_RUIDO_TELEFONE ");
		sql.append("FROM   ");
		sql.append("	db2aplicativos.TB_MO_TELEFONE j   ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_MO_RUIDO_TELEFONE l ON  ");
		sql.append("		j.ID_DELEGACIA = l.ID_DELEGACIA  ");
	    sql.append("	AND	j.ANO_BO  = l.ANO_BO  ");
		sql.append("	AND	j.NUM_BO  = l.NUM_BO  ");
		sql.append("	AND	j.CONT_PESSOA  = l.CONT_PESSOA  ");   
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_RUIDO_TELEFONE m ON l.ID_RUIDO_TELEFONE = m.ID_RUIDO_TELEFONE ");
		sql.append("WHERE	  ");
		sql.append("	j.ID_DELEGACIA  = ? ");
		sql.append("AND	j.ANO_BO  = ? ");
		sql.append("AND	j.NUM_BO  = ? ");
		sql.append("AND	j.CONT_PESSOA  = ? ");
				
				
		json = prepararConsultarIterar(conn, "TELEFONE", "tb_mo_telefone",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.autor.linguagem
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasAutorLinguagens(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("	n.ID_ACENTO_LINGUAGEM,  ");
		sql.append("	o.DESCR_ACENTO_LINGUAGEM,  ");
		sql.append("	n.ID_DISTORCAO_LINGUAGEM,  ");
		sql.append("	s.DESCR_DISTORCAO_LINGUAGEM,  ");
		sql.append("	n.ID_FRASEOLOGIA_LINGUAGEM,  ");
		sql.append("	t.DESCR_FRASEOLOGIA_LINGUAGEM, "); 
		sql.append("	n.ID_RESSONANCIA_LINGUAGEM,  ");
		sql.append("	u.DESCR_RESSONACIA_LINGUAGEM, "); 
		sql.append("	n.ID_VELOCIDADE_LINGUAGEM,  ");
		sql.append("	p.DESCR_VELOCIDADE_LINGUAGEM,  ");
		sql.append("	n.ID_VOLUME_LINGUAGEM,  ");
		sql.append("	q.DESCR_VOLUME_LINGUAGEM,  ");
	    sql.append("	n.ID_VOZ_LINGUAGEM,  ");
		sql.append("	r.DESCR_VOZ_LINGUAGEM  ");
		sql.append("FROM  ");  
		sql.append("	db2aplicativos.TB_LINGUAGEM_AUTOR n  ");  
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_ACENTO_LINGUAGEM o ON n.ID_ACENTO_LINGUAGEM = o.ID_ACENTO_LINGUAGEM    ");  
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_VELOCIDADE_LINGUAGEM p ON n.ID_VELOCIDADE_LINGUAGEM = p.ID_VELOCIDADE_LINGUAGEM	LEFT OUTER JOIN db2aplicativos.TB_VOLUME_LINGUAGEM q ON n.ID_VOLUME_LINGUAGEM = q.ID_VOLUME_LINGUAGEM  ");    
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_VOZ_LIGUAGEM r ON n.ID_VOZ_LINGUAGEM = r.ID_VOZ_LINGUAGEM   ");   
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_DISTORCAO_LINGUAGEM s ON n.ID_DISTORCAO_LINGUAGEM = s.ID_DISTORCAO_LINGUAGEM    LEFT OUTER JOIN db2aplicativos.TB_FRASEOLOGIA_LINGUAGEM t ON n.ID_FRASEOLOGIA_LINGUAGEM  = t.ID_FRASEOLOGIA_LINGUAGEM  ");    
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_RESSONANCIA_LINGUAGEM u ON n.ID_RESSONANCIA_LINGUAGEM  = u.ID_RESSONANCIA_LINGUAGEM  ");
		sql.append("WHERE	 "); 
		sql.append("	n.ID_DELEGACIA  = ? ");
		sql.append("AND	n.ANO_BO  = ? ");
		sql.append("AND	n.NUM_BO  = ? ");
		sql.append("AND	n.CONT_PESSOA  = ? ");
				
		json = prepararConsultarIterar(conn, "LINGUAGEM", "tb_linguagem_autor",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	
	
	/**
	 * bo.pessoa.adorno
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasAdornos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("a.ID_TIPO_ADORNO, ");
		sql.append("b.DESCR_TIPO_ADORNO, ");
		sql.append("a.ID_LOCAL_CORPO, ");
		sql.append("c.DESCR_LOCAL_CORPO, ");
		sql.append("a.CARACTERISTICA_ADORNO ");
		sql.append("FROM   ");
		sql.append("db2aplicativos.TB_ADORNO a   ");
		sql.append("LEFT OUTER JOIN db2aplicativos.TB_TIPO_ADORNO b ON a.ID_TIPO_ADORNO = b.ID_TIPO_ADORNO ");
		sql.append("LEFT OUTER JOIN db2aplicativos.TB_LOCAL_CORPO c ON a.ID_LOCAL_CORPO = c.ID_LOCAL_CORPO ");
		sql.append("WHERE	  ");
		sql.append("	a.ID_DELEGACIA  = ? ");
		sql.append("AND	a.ANO_BO  = ? ");
		sql.append("AND	a.NUM_BO  = ? ");
		sql.append("AND	a.CONT_PESSOA  = ? ");
		sql.append("ORDER BY a.SEQUENCIA_ADORNO ");
		
		json = prepararConsultarIterar(conn, "ADORNO", "tb_adorno",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.deformidade
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasDeformidades(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select   ");
		sql.append("	a.ID_TIPO_DEFORMIDADE,   ");
		sql.append("	b.DESCR_TIPO_DEFORMIDADE,   ");
		sql.append("	a.ID_LOCAL_CORPO,   ");
		sql.append("	c.DESCR_LOCAL_CORPO   ");
		sql.append("from "); 
		sql.append("	db2aplicativos.TB_DEFORMIDADE a,   ");
		sql.append("	db2aplicativos.TB_TIPO_DEFORMIDADE b,   ");
		sql.append("	db2aplicativos.TB_LOCAL_CORPO c  ");
		sql.append("where   ");
		sql.append("	a.ID_TIPO_DEFORMIDADE = b.ID_TIPO_DEFORMIDADE   ");
		sql.append("and	a.ID_LOCAL_CORPO = c.ID_LOCAL_CORPO  ");
		sql.append("and a.ID_DELEGACIA = ?   ");
		sql.append("and	a.ANO_BO = ?   ");
		sql.append("and	a.NUM_BO = ?   ");
		sql.append("and	a.CONT_PESSOA = ?   ");
		sql.append("order by a.ID_TIPO_DEFORMIDADE, a.ID_LOCAL_CORPO ");
		
		json = prepararConsultarIterar(conn, "DEFORMIDADE", "tb_deformidade",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}

	/**
	 * bo.pessoa.patologia
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasPatologias(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select   ");
		sql.append("	a.ID_PATOLOGIA,  ");
		sql.append("	b.DESCR_PATOLOGIA  ");
		sql.append("from  ");
		sql.append("	db2aplicativos.TB_PESSOA_PATOLOGIA a,  ");
		sql.append("	db2aplicativos.TB_PATOLOGIA b  ");
		sql.append("where  ");
		sql.append("	a.ID_PATOLOGIA = b.ID_PATOLOGIA ");
		sql.append("and a.ID_DELEGACIA = ? ");
		sql.append("and	a.ANO_BO = ?   ");
		sql.append("and	a.NUM_BO = ?  ");
		sql.append("and	a.CONT_PESSOA = ?   ");
		sql.append("order by   ");
		sql.append("	a.ID_PATOLOGIA  ");

		
		json = prepararConsultarIterar(conn, "PATOLOGIA", "tb_pessoa_patologia",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}

	/**
	 * bo.pessoa.banco
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasBancos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select  "); 
		sql.append("	a.NOME_BANCO_PESSOA,  ");
		sql.append("	a.NUMERO_AGENCIA_BANCO,  ");
		sql.append("	a.NUMERO_CONTA   ");
		sql.append("from	 ");
		sql.append("	db2aplicativos.TB_BANCO_PESSOA a ");
		sql.append("where   ");
		sql.append("	a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ?   ");
		sql.append("and	a.NUM_BO = ?   ");
		sql.append("and	a.CONT_PESSOA = ?  ");
		sql.append("order by a.NOME_BANCO_PESSOA  ");
		
		json = prepararConsultarIterar(conn, "BANCO", "tb_banco_pessoa",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}

	/**
	 * bo.pessoa.cartao_credito
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasCartaoCreditos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select   ");
		sql.append("	a.NOME_CARTAO,  ");
		sql.append("	a.NUMERO_CARTAO,  ");
		sql.append("	a.VALIDADE_CARTAO  ");
		sql.append("from   ");
		sql.append("	db2aplicativos.TB_CARTAO_CREDITO a ");
		sql.append("where   ");
		sql.append("	a.ID_DELEGACIA = ? ");
		sql.append("and	a.ANO_BO = ? ");
		sql.append("and	a.NUM_BO = ? ");  
		sql.append("and	a.CONT_PESSOA = ?  "); 
		sql.append("order by ");  
		sql.append("	a.NOME_CARTAO,  ");
		sql.append("	a.NUMERO_CARTAO ");
				
		json = prepararConsultarIterar(conn, "CARTAO_CREDITO", "tb_cartao_credito",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.endereco
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasEnderecos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  ");
		sql.append("	a.TIPO_ENDERECO, ");
		sql.append("	a.LOGRADOURO_ENDERECO, ");
		sql.append("	a.NUMERO_LOGRADOURO_ENDERECO, ");
		sql.append("	a.COMPLEMENTO_ENDERECO,  ");
		sql.append("	a.BAIRRO_ENDERECO, ");
		sql.append("	a.CEP_ENDERECO,  ");
		sql.append("	a.CIDADE_ENDERECO, ");
		sql.append("	a.ID_UF, "); 
		sql.append("	b.NOME_UF,  ");
		sql.append("	a.PONTO_REFERENCIA_ENDERECO,  ");
		sql.append("	a.NOME_EMPRESA  ");
		sql.append("FROM   ");
		sql.append("	db2aplicativos.TB_PESSOA_ENDERECO a  ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_UF b ON a.ID_UF = b.ID_UF ");
		sql.append("WHERE	  ");
		sql.append("	a.ID_DELEGACIA  = ? ");
		sql.append("AND	a.ANO_BO  = ? ");
		sql.append("AND	a.NUM_BO  = ? ");
		sql.append("AND	a.CONT_PESSOA  = ? ");
		sql.append("ORDER BY  ");
		sql.append("	a.TIPO_ENDERECO ");
				
		json = prepararConsultarIterar(conn, "ENDERECO", "tb_pessoa_endereco",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}

	/**
	 * bo.pessoa.telefone
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasTelefones(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select  ");
		sql.append("	a.DDD_TELEFONE,  ");
		sql.append("	a.NUMERO_TELEFONE, "); 
		sql.append("	a.RAMAL_TELEFONE,  ");
		sql.append("	a.RECADO_TELEFONE,  ");
		sql.append("	a.FLAG_TIPO_TELEFONE, ");
		sql.append("	a.OBSERVACAO_TELEFONE  ");
		sql.append("from  ");
		sql.append("	db2aplicativos.TB_PESSOA_TELEFONE a ");
		sql.append("where 	 ");
		sql.append("	a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ?  ");
		sql.append("and	a.NUM_BO = ?  ");
		sql.append("and	a.CONT_PESSOA = ?  ");
		sql.append("order by ");
		sql.append("	a.DDD_TELEFONE,  ");
		sql.append("	a.NUMERO_TELEFONE ");
				
		json = prepararConsultarIterar(conn, "TELEFONE", "tb_pessoa_telefone",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.vestuario
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasVestuarios(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");   
		sql.append("	a.ID_TIPO_VESTUARIO, ");  
		sql.append("	b.DESCR_TIPO_VESTUARIO, ");  
		sql.append("	a.CARACTERISTICA_VESTUARIO ");  
		sql.append("from  "); 
		sql.append("	db2aplicativos.TB_VESTUARIO a,   ");
		sql.append("	db2aplicativos.TB_TIPO_VESTUARIO b "); 
		sql.append("where   "); 
		sql.append("	a.ID_TIPO_VESTUARIO = b.ID_TIPO_VESTUARIO   ");
		sql.append("and	a.ID_DELEGACIA = ?  ");  
		sql.append("and	a.ANO_BO = ?   ");
		sql.append("and	a.NUM_BO = ?  ");  
		sql.append("and	a.CONT_PESSOA = ?  ");  
		sql.append("order by a.ID_TIPO_VESTUARIO  "); 
				
		json = prepararConsultarIterar(conn, "VESTUARIO", "tb_vestuario",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.natureza
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasNaturezas(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("	e.ID_OCORRENCIA, ");
		sql.append("	a.DESCR_OCORRENCIA, ");
		sql.append("	e.ID_ESPECIE, ");
		sql.append("	b.DESCR_ESPECIE, ");
		sql.append("	e.ID_SUBESPECIE, ");
		sql.append("	c.DESCR_SUBESPECIE, ");
		sql.append("	e.ID_NATUREZA, ");
		sql.append("	d.RUBRICA, ");
		sql.append("	e.ID_DESDOBRAMENTO, ");
		sql.append("	g.DESCR_DESDOBRAMENTO, ");
		sql.append("	e.ID_MODALIDADE ");
		sql.append("FROM   ");
		sql.append("	db2aplicativos.TB_PESSOA_NATUREZA e  "); 
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_DESDOBRAMENTO g ON  ");
		sql.append("		e.ID_OCORRENCIA = g.ID_OCORRENCIA  ");
		sql.append("	AND	e.ID_ESPECIE  = g.ID_ESPECIE  ");
		sql.append("	AND	e.ID_SUBESPECIE  = g.ID_SUBESPECIE  ");
		sql.append("	AND	e.ID_NATUREZA  = g.ID_NATUREZA "); 
		sql.append("	AND	e.ID_DESDOBRAMENTO  = g.ID_DESDOBRAMENTO   ");  
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_MODALIDADE h ON  ");
		sql.append("		e.ID_OCORRENCIA = h.ID_OCORRENCIA  ");
		sql.append("	AND	e.ID_ESPECIE  = h.ID_ESPECIE  ");
		sql.append("	AND	e.ID_SUBESPECIE  = h.ID_SUBESPECIE  ");
		sql.append("	AND	e.ID_NATUREZA  = h.ID_NATUREZA  ");
		sql.append("	AND	e.ID_DESDOBRAMENTO  = h.ID_DESDOBRAMENTO  ");
		sql.append("	AND	e.ID_MODALIDADE  = h.ID_MODALIDADE, ");
		sql.append("	db2aplicativos.TB_NATUREZA d, ");
		sql.append("	db2aplicativos.TB_SUBESPECIE c, ");
		sql.append("	db2aplicativos.TB_ESPECIE b, ");
		sql.append("	db2aplicativos.TB_OCORRENCIA a ");
		sql.append("WHERE	  ");	
		sql.append("	e.ID_OCORRENCIA  = d.ID_OCORRENCIA ");
		sql.append("AND	e.ID_ESPECIE  = d.ID_ESPECIE ");
		sql.append("AND	e.ID_SUBESPECIE  = d.ID_SUBESPECIE ");
		sql.append("AND	e.ID_NATUREZA  = d.ID_NATUREZA ");
		sql.append("AND	d.ID_OCORRENCIA  = c.ID_OCORRENCIA ");
		sql.append("AND	d.ID_ESPECIE  = c.ID_ESPECIE ");
		sql.append("AND	d.ID_SUBESPECIE  = c.ID_SUBESPECIE ");
		sql.append("AND	c.ID_OCORRENCIA  = b.ID_OCORRENCIA ");
		sql.append("AND	c.ID_ESPECIE  = b.ID_ESPECIE ");
		sql.append("AND	b.ID_OCORRENCIA  = a.ID_OCORRENCIA ");
		sql.append("AND	e.ID_DELEGACIA  = ? ");
		sql.append("AND	e.ANO_BO  = ? ");
		sql.append("AND	e.NUM_BO  = ? ");
		sql.append("AND	e.CONT_PESSOA  = ? ");
		sql.append("ORDER BY e.SEQUENCIA ");

		json = prepararConsultarIterar(conn, "NATUREZA", "tb_pessoa_natureza",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.localfrequenta
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasLocalFrequentas(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  ");
		sql.append("	a.NOME_LOC_FREQ, ");
		sql.append("	b.DESCR_TIPO_LOCAL, ");
		sql.append("	a.PERIODO_LOC_FREQ, ");
		sql.append("	a.ID_TIPO_LOCAL,  ");
		sql.append("	a.LOGRADOURO_LOC_FREQ, ");
		sql.append("	a.NUMERO_LOGRADOURO_LOC_FREQ, ");
		sql.append("	a.COMPLEMENTO_LOC_FREQ,  ");
		sql.append("	a.CEP_LOC_FREQ, ");
		sql.append("	a.BAIRRO_LOC_FREQ,  ");
		sql.append("	a.CIDADE_LOC_FREQ, "); 
		sql.append("	a.ID_UF,  ");
		sql.append("	d.NOME_UF  ");
		sql.append("FROM  ");  
		sql.append("	db2aplicativos.TB_LOCAL_FREQUENTA a  ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_TIPO_LOCAL b ON a.ID_TIPO_LOCAL = b.ID_TIPO_LOCAL "); 
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_UF d ON a.ID_UF = d.ID_UF ");
		sql.append("WHERE ");
		sql.append("	a.ID_DELEGACIA  = ? ");
		sql.append("AND	a.ANO_BO  = ? ");
		sql.append("AND	a.NUM_BO  = ? ");
		sql.append("AND	a.CONT_PESSOA  = ? ");
		sql.append("ORDER BY a.NOME_LOC_FREQ ");
				
		json = prepararConsultarIterar(conn, "LOCAL_FREQUENTA", "tb_local_frequenta",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.toxico
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasToxicos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select "); 
		sql.append("	a.ID_TOXICO,  ");
		sql.append("	c.DESCR_TOXICO "); 
		sql.append("from  ");
		sql.append("	db2aplicativos.TB_PESSOA_TOXICO a,  ");
		sql.append("	db2aplicativos.TB_TOXICO c  ");
		sql.append("where ");
		sql.append("	a.ID_TOXICO = c.ID_TOXICO  ");
		sql.append("and	a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ?  ");
		sql.append("and	a.NUM_BO = ?  ");
		sql.append("and	a.CONT_PESSOA = ?  ");
		sql.append("order by  ");
		sql.append("	a.ID_TOXICO ");
				
		json = prepararConsultarIterar(conn, "TOXICO", "tb_pessoa_toxico",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.jogo
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasJogos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");   
		sql.append("	a.ID_JOGO, ");  
		sql.append("	c.DESCR_JOGO  "); 
		sql.append("from  ");  
		sql.append("	db2aplicativos.TB_PESSOA_JOGO a,  "); 
		sql.append("	db2aplicativos.TB_JOGO c  "); 
		sql.append("where  ");  
		sql.append("	a.ID_JOGO = c.ID_JOGO   ");
		sql.append("and a.ID_DELEGACIA = ?  ");  
		sql.append("and	a.ANO_BO = ?  ");  
		sql.append("and	a.NUM_BO = ? ");   
		sql.append("and	a.CONT_PESSOA = ?  "); 
		sql.append("order by a.ID_JOGO  "); 
	
				
		json = prepararConsultarIterar(conn, "JOGO", "tb_pessoa_jogo",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	/**
	 * bo.pessoa.condutor
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasCondutores(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.VIATURA "); 
		sql.append("from  ");
		sql.append("	db2aplicativos.TB_PESSOA_CONDUTOR a ");
		sql.append("where  ");
		sql.append("	a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ?  ");
		sql.append("and	a.NUM_BO = ?  ");
		sql.append("and	a.CONT_PESSOA = ? ");
				
		json = prepararConsultarIterar(conn, "CONDUTOR", "tb_pessoa_condutor",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	
	/**
	 * bo.pessoa.vitima.ferimento
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contPessoa
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarPessoasVitimasFerimentos(int idDelegacia, int ano,
			int numero, int contPessoa, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select  "); 
		sql.append("	a.ID_FERIMENTO,   ");
		sql.append("	b.DESCR_FERIMENTO,  "); 
		sql.append("	a.ID_INSTRUMENTO,  "); 
		sql.append("	c.DESCR_INSTRUMENTO   ");
		sql.append("from ");	 
		sql.append("	db2aplicativos.TB_BO_FERIMENTO a,   ");
		sql.append("	db2aplicativos.TB_FERIMENTO b,  "); 
		sql.append("	db2aplicativos.TB_INSTRUMENTO c  ");
		sql.append("where  ");
		sql.append("	a.ID_FERIMENTO = b.ID_FERIMENTO  ");
		sql.append("and	a.ID_INSTRUMENTO = c.ID_INSTRUMENTO  ");
		sql.append("and a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ? ");
		sql.append("and a.NUM_BO = ?  ");
		sql.append("and	a.CONT_PESSOA = ?  ");
		sql.append("order by a.ID_FERIMENTO, a.ID_INSTRUMENTO ");
				
		json = prepararConsultarIterar(conn, "FERIMENTO", "tb_bo_ferimento",
				sql.toString(), idDelegacia, ano, numero, contPessoa);

		return json;
	}
	
	
	/**
	 * bo.veiculo.seguranca
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contVeiculo
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarVeiculosSegurancas(int idDelegacia, int ano,
			int numero, int contVeiculo, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_VEICULO_SEGURANCA, ");
		sql.append("	b.DESCR_VEICULO_SEGURANCA ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_veiculo_seguranca a, ");
		sql.append("	db2aplicativos.tb_veiculo_seguranca b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_veiculo = ? ");
		sql.append("and	b.id_veiculo_seguranca = a.id_veiculo_seguranca ");
		sql.append("order by 1");
		
		json = prepararConsultarIterar(conn, "SEGURANCA", "tb_bo_veiculo_seguranca",
				sql.toString(), idDelegacia, ano, numero, contVeiculo);

		return json;
	}
	
	

	/**
	 * bo.veiculo.acesso
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contVeiculo
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarVeiculosAcessos(int idDelegacia, int ano,
			int numero, int contVeiculo, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_VEICULO_ACESSO, ");
		sql.append("	b.DESCR_VEICULO_ACESSO ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_veiculo_acesso a, ");
		sql.append("	db2aplicativos.tb_veiculo_acesso b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_veiculo = ? ");
		sql.append("and	b.id_veiculo_acesso = a.id_veiculo_acesso ");
		
		json = prepararConsultarIterar(conn, "ACESSO", "tb_bo_veiculo_acesso",
				sql.toString(), idDelegacia, ano, numero, contVeiculo);

		return json;
	}
	
	/**
	 * bo.veiculo.acionamento
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contVeiculo
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarVeiculosAcionamentos(int idDelegacia, int ano,
			int numero, int contVeiculo, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_VEICULO_ACIONAMENTO, ");
		sql.append("	b.DESCR_VEICULO_ACIONAMENTO ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_veiculo_acionamento a, ");
		sql.append("	db2aplicativos.tb_veiculo_acionamento b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_veiculo = ? ");
		sql.append("and	b.id_veiculo_acionamento = a.id_veiculo_acionamento ");
		
		json = prepararConsultarIterar(conn, "ACIONAMENTO", "tb_bo_veiculo_acionamento",
				sql.toString(), idDelegacia, ano, numero, contVeiculo);

		return json;
	}
	
	/**
	 * bo.veiculo.instrumento
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contVeiculo
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarVeiculosInstrumentos(int idDelegacia, int ano,
			int numero, int contVeiculo, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_INSTRUMENTO, ");
		sql.append("	b.DESCR_INSTRUMENTO, ");
		sql.append("	a.CARACTERISTICA_INSTRUMENTO ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_veiculo_instrumento a, ");
		sql.append("	db2aplicativos.tb_instrumento b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_veiculo = ? ");
		sql.append("and	b.id_instrumento = a.id_instrumento ");
		
		json = prepararConsultarIterar(conn, "INSTRUMENTO", "tb_bo_veiculo_instrumento",
				sql.toString(), idDelegacia, ano, numero, contVeiculo);

		return json;
	}
	
	/**
	 * bo.veiculo.carga
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param contVeiculo
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarVeiculosCargas(int idDelegacia, int ano,
			int numero, int contVeiculo, Connection conn)
			throws SQLException
	{
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("	a.ID_TIPO_VEICCARGA, ");
		sql.append("	b.DESCR_TIPO_VEICCARGA, ");
		sql.append("	a.CARACTERISTICAS, ");
		sql.append("	a.FLAG_ESCOLTA, ");
		sql.append("	a.FLAG_RECUPERACAO, ");
		sql.append("	a.DATA_RECUPERACAO, ");
		sql.append("	a.HORA_RECUPERACAO, ");
		sql.append("	a.LOCAL_RECUPERACAO ");
		sql.append("from ");
		sql.append("	db2aplicativos.tb_bo_veiculo_carga a, ");
		sql.append("	db2aplicativos.tb_tipo_veiccarga b ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and a.cont_veiculo = ? ");
		sql.append("and	b.id_tipo_veiccarga = a.id_tipo_veiccarga ");
		
		json = prepararConsultarIterar(conn, "CARGA", "tb_bo_veiculo_carga",
				sql.toString(), idDelegacia, ano, numero, contVeiculo);

		return json;
	}
	
	
	/**
	 * bo.objeto
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarObjetos(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("select ");
		sql.append("	a.*, ");
		sql.append("	b.DESCR_MODO_OBJETO, ");
		sql.append("	d.DESCR_TIPO_OBJETO, ");
		sql.append("	c.DESCR_SUBTIPO_OBJETO, ");
		sql.append("	e.DESCR_UNIDADE ");
		sql.append("from   ");
		sql.append("	db2aplicativos.TB_BO_OBJETO a LEFT OUTER JOIN db2aplicativos.TB_UNIDADE e ON a.ID_UNIDADE = e.ID_UNIDADE, ");
		sql.append("	db2aplicativos.TB_MODO_OBJETO b, ");
		sql.append("	db2aplicativos.TB_SUBTIPO_OBJETO c, ");
		sql.append("	db2aplicativos.TB_TIPO_OBJETO d ");
		sql.append("where ");
		sql.append("	a.id_delegacia = ? ");
		sql.append("and a.ano_bo = ? ");
		sql.append("and a.num_bo = ? ");
		sql.append("and	a.ID_MODO_OBJETO = b.ID_MODO_OBJETO ");
		sql.append("AND	a.ID_TIPO_OBJETO = c.ID_TIPO_OBJETO ");
		sql.append("AND	a.ID_SUBTIPO_OBJETO = c.ID_SUBTIPO_OBJETO ");
		sql.append("AND	c.ID_TIPO_OBJETO = d.ID_TIPO_OBJETO ");
		sql.append("order by a.CONT_OBJETO ");	
		
		json = prepararConsultarIterar(conn, "OBJETO", "tb_bo_objeto",
				sql.toString(), idDelegacia, ano, numero);

		return json;
	}	
	
	/**
	 * bo.arma
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarArmas(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";
		
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		sql.append("	a.*, ");
		sql.append("	e.NOME_PESSOA, ");
		sql.append("	b.DESCR_MODO_OBJETO, ");
		sql.append("	c.DESCR_ARMA_FOGO ");
		sql.append("FROM ");
		sql.append("	db2aplicativos.TB_BO_ARMAS a  ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_MODO_OBJETO b ON a.ID_MODO_OBJETO = b.ID_MODO_OBJETO ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_TIPO_ARMA_FOGO c ON a.ID_TIPO_ARMA_FOGO = c.ID_TIPO_ARMA_FOGO ");  
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_BO_PESSOA e ON  ");
		sql.append("		a.ID_DELEGACIA = e.ID_DELEGACIA  ");
		sql.append("	AND	a.ANO_BO  = e.ANO_BO  ");
		sql.append("	AND	a.NUM_BO  = e.NUM_BO  ");
		sql.append("	AND	a.CONT_PESSOA  = e.CONT_PESSOA ");
		sql.append("WHERE	 ");
		sql.append("	a.ID_DELEGACIA = ? ");
		sql.append("AND	a.ANO_BO = ? ");
		sql.append("AND	a.NUM_BO = ? ");
		sql.append("ORDER BY  ");
		sql.append("	a.CONT_ARMA ");
		
		
		json = prepararConsultarIterar(conn, "ARMA", "tb_bo_armas",
				sql.toString(), idDelegacia, ano, numero);

		return json;
	}	
	
	/**
	 * bo.empresa
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarEmpresas(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("	a.*, ");
		sql.append("	b.DESCR_TIPO_EMPRESA, ");
		sql.append("	e.NOME_PESSOA, ");
		sql.append("	c.NOME_UF ");
		sql.append("FROM   ");
		sql.append("	db2aplicativos.TB_BO_EMPRESA a  ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_TIPO_EMPRESA b ON a.ID_TIPO_EMPRESA = b.ID_TIPO_EMPRESA ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_UF c ON a.ID_UF = c.ID_UF  ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_BO_PESSOA e ON  ");
		sql.append("		a.ID_DELEGACIA = e.ID_DELEGACIA  ");
		sql.append("	AND	a.ANO_BO  = e.ANO_BO  ");
		sql.append("	AND	a.NUM_BO  = e.NUM_BO  ");
		sql.append("	AND	a.CONT_PESSOA  = e.CONT_PESSOA ");
		sql.append("WHERE	 ");
		sql.append("	a.ID_DELEGACIA  = ? ");
		sql.append("AND	a.ANO_BO  = ? ");
		sql.append("AND	a.NUM_BO  = ? ");
		sql.append("ORDER BY a.CONT_EMPRESA	 ");	
		
		json = prepararConsultarIterar(conn, "EMPRESA", "tb_bo_empresa",
				sql.toString(), idDelegacia, ano, numero);

		return json;
	}	
	
	/**
	 * bo.entorpecente
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarEntorpecentes(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";
		
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		sql.append("	a.*, ");
		sql.append("	b.DESCR_TOXICO, ");
		sql.append("	e.DESCR_UNIDADE, ");
		sql.append("	c.DESCR_ACONDICIONAMENTO, ");
		sql.append("	d.DESCR_INVOLUCRO ");
		sql.append("FROM   ");
		sql.append("	db2aplicativos.TB_BO_ENTORPECENTES a  ");
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_ACONDICIONAMENTO c ON a.ID_ACONDICIONAMENTO = c.ID_ACONDICIONAMENTO "); 
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_INVOLUCRO d ON a.ID_INVOLUCRO = d.ID_INVOLUCRO   ");  
		sql.append("	LEFT OUTER JOIN db2aplicativos.TB_UNIDADE e ON a.ID_UNIDADE = e.ID_UNIDADE, ");
		sql.append("	db2aplicativos.TB_TOXICO b ");
		sql.append("WHERE	 "); 
		sql.append("	a.ID_TOXICO  = b.ID_TOXICO ");
		sql.append("AND	a.ID_DELEGACIA  = ? ");
		sql.append("AND	a.ANO_BO = ? ");
		sql.append("AND	a.NUM_BO = ? ");
		sql.append("ORDER BY a.CONT_ENTORPECENTE ");
		
		json = prepararConsultarIterar(conn, "ENTORPECENTE", "tb_bo_entorpecentes",
				sql.toString(), idDelegacia, ano, numero);

		return json;
	}	
	
	/**
	 * bo.carga
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarCargas(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("	a.*, ");
		sql.append("	b.DESCR_TIPO_CARGA, ");
		sql.append("	c.DESCR_MODO_OBJETO, ");
		sql.append("	d.DESCR_UNIDADE ");
		sql.append("FROM  db2aplicativos.TB_BO_CARGA a   ");
		sql.append("LEFT OUTER JOIN  db2aplicativos.TB_MODO_OBJETO c  ON  a.ID_MODO_OBJETO  = c.ID_MODO_OBJETO ");
		sql.append("LEFT OUTER JOIN  db2aplicativos.TB_UNIDADE d  ON  a.ID_UNIDADE  = d.ID_UNIDADE, ");
		sql.append("db2aplicativos.TB_TIPO_CARGA b ");
		sql.append("WHERE ");
		sql.append("	a.ID_DELEGACIA = ? ");
		sql.append("and a.ANO_BO = ? ");
		sql.append("and a.NUM_BO = ? ");
		sql.append("and a.ID_TIPO_CARGA  = b.ID_TIPO_CARGA ");
		sql.append("ORDER BY a.ID_TIPO_CARGA ");		
		
		json = prepararConsultarIterar(conn, "CARGA", "tb_bo_carga",
				sql.toString(), idDelegacia, ano, numero);

		return json;
	}	
	
	/**
	 * bo.modusoperandi
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarModusOperandis(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";

		StringBuilder sql = new StringBuilder();
		sql.append("select  ");
		sql.append("	a.*, ");
		sql.append("	b.DESCR_LOCAL_ACESSO,  ");
		sql.append("	c.DESCR_QUALIFICACAO_ACESSO  ");
		sql.append("from ");
		sql.append("	db2aplicativos.TB_MODUS_OPERANDI a,  ");
		sql.append("	db2aplicativos.TB_LOCAL_ACESSO b,  ");
		sql.append("	db2aplicativos.TB_QUALIFICACAO_ACESSO c ");
		sql.append("where  ");
		sql.append("	a.ID_LOCAL_ACESSO = b.ID_LOCAL_ACESSO ");
		sql.append("and	a.ID_QUALIFICACAO_ACESSO = c.ID_QUALIFICACAO_ACESSO ");
		sql.append("and a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ?  ");
		sql.append("and	a.NUM_BO = ? ");
		sql.append("order by a.ID_LOCAL_ACESSO ");
		
		json = prepararConsultarIterar(conn, "MODUS_OPERANDI", "tb_modus_operandi",
				sql.toString(), idDelegacia, ano, numero);

		return json;
	}	
	
	/**
	 * bo.espolio
	 * @param idDelegacia
	 * @param ano
	 * @param numero
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private String consultarEspolios(int idDelegacia, int ano, int numero, Connection conn) throws SQLException
	{
		
		String json = "";

		StringBuilder sql = new StringBuilder();

		sql.append("select  ");
		sql.append("	a.ID_ESPOLIO,  ");
		sql.append("	b.DESCR_ESPOLIO  ");
		sql.append("from  ");
		sql.append("	db2aplicativos.TB_ESPOLIO_TB_BO a,  ");
		sql.append("	db2aplicativos.TB_ESPOLIO b  ");
		sql.append("where ");
		sql.append("	a.ID_ESPOLIO = b.ID_ESPOLIO ");
		sql.append("and a.ID_DELEGACIA = ?  ");
		sql.append("and	a.ANO_BO = ?  ");
		sql.append("and	a.NUM_BO = ? ");
		sql.append("order by a.ID_ESPOLIO ");
		
		json = prepararConsultarIterar(conn, "ESPOLIO", "tb_espolio_tb_bo",
				sql.toString(), idDelegacia, ano, numero);

		return json;
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
//			if (chave.equals("HISTORICO_BO"))
//			{
//				valor = valor.replaceAll("^\\p{Print}", " ");
//			}
//			valor = valor.replaceAll("\n", " "); //The newline (line feed) character u000A
//			valor = valor.replaceAll("\r", " "); //The carriage-return character u000D
//			valor = valor.replaceAll("\t", " "); //tab
//			valor = valor.replaceAll("\"", " ");
//			//valor = valor.replaceAll("\\", " "); //--problema de regex ?
//			while (valor.indexOf('\\') > 0)
//			{
//				valor = valor.replace('\\', ' ');		
//			}
//			
//			valor = valor.replaceAll("\u001a", "");
			valor = valor.replaceAll("[^\\u0020-\\u007e\\u00a0-\\u00ff]|\\u005c|\\u0022", " ");
			valor = valor.trim();
			atributo =  "\"" + chave + "\":\"" + valor + "\"";
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
}
