package br.com.fences.prototipo02.batch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.runtime.BatchRuntime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TesteJob
 */
@WebServlet("/TesteJob")
public class TesteJob extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Exemplo de Invocação de Job - Servlet</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Invocando Jobs no Servlet:</h1>");
			JobOperator jo = BatchRuntime.getJobOperator();
			long jid = jo.start("meuJob", new Properties());
			out.println("Job submetido: " + jid + "<br>");
			out.println("</body>");
			out.println("</html>");
		} catch (JobStartException | JobSecurityException ex) {
			Logger.getLogger(TesteJob.class.getName()).log(Level.SEVERE,
					null, ex);
		}

	}
}
