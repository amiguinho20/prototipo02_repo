package br.com.fences.prototipo02.test.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestarUnicode {

	@Before
	public void pre()
	{
	}
	
	@After
	public void pos()
	{
	}
	
	//@Test
	public void testarUnicode()
	{	
		try{
		String original = "Tomás Saúde § : § \\ \" \"  « < baú $ #5	";
		System.out.println("antes.: [" + original + "]");
		
		String alterado = original.replaceAll("[^\\u0020-\\u007e\\u00a0-\\u00ff]|\\u005c|\\u0022", " ");
		
		System.out.println("depois: [" + alterado + "]");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	//@Test
	public void normalizar()
	{	
		PrintWriter pw = null;
		BufferedReader br = null;
		try
		{
			String entrada = "/Users/Amiguinho/Development/espelho/20150601_old_bkp.json";
			String saida = "/Users/Amiguinho/Development/espelho/20150601_new.json";

	    	 pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saida), "UTF-8")));

			
			 br = new BufferedReader(new FileReader(entrada));
			String linha;
			int qtd = 0;
			while ((linha = br.readLine()) != null) {

				pw.println(linha.replaceAll("[^\\u0020-\\u007e\\u00a0-\\u00ff]|\\u005c", ""));
				qtd++;
				if ((qtd % 20) == 0)
				{
					System.out.println("linha... " + qtd);
				}
			}
			System.out.println("termino linha... " + qtd);
			br.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{

			pw.close();
		}
		
	}
	
}
