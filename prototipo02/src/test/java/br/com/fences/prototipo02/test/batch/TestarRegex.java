package br.com.fences.prototipo02.test.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestarRegex {

	@Before
	public void pre()
	{
	}
	
	@After
	public void pos()
	{
	}
	
	@Test
	public void testarRegex()
	{	
		
		final String regexA = "[AaÃãÁáÀà]";
		final String regexE = "[EeÉéÈè]";
		final String regexI = "[IiÍíÌì]";
		final String regexO = "[OoÕõÓóÒò]";
		final String regexU = "[UuÚúÙù]";
		final String regexC = "[CcÇç]";
		final String regexN = "[NnÑñ]";
		
		List<String> regexs = Arrays.asList(regexA, regexE, regexI, regexO, regexU, regexC, regexN);
		
		try{
			String original = "João";
			StringBuilder convertida = new StringBuilder();
			for (char letra : original.toCharArray())
			{
				String regexEncontrada = "";
				for (String regex : regexs)
				{
					if (regex.contains(Character.toString(letra)))
					{
						regexEncontrada = regex;
						break;
					}
				}
				if (regexEncontrada.isEmpty())
				{
					convertida.append(letra);
				}
				else
				{
					convertida.append(regexEncontrada);
				}
			}
			
			System.out.println("original: " + original);
			System.out.println("convertida: " + convertida.toString());
			
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
