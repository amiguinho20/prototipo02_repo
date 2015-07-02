package br.com.fences.prototipo02.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Wrapper;

public class DaoUtil {

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
