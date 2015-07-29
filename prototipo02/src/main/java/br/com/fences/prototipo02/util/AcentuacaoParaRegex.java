package br.com.fences.prototipo02.util;

import java.util.Arrays;
import java.util.List;

public class AcentuacaoParaRegex {

	public static String converter(String original)
	{
		final String regexA = "[AaÃãÁáÀà]";
		final String regexE = "[EeÉéÈè]";
		final String regexI = "[IiÍíÌì]";
		final String regexO = "[OoÕõÓóÒò]";
		final String regexU = "[UuÚúÙù]";
		final String regexC = "[CcÇç]";
		final String regexN = "[NnÑñ]";
		final String regexWildcardAsterisco = ".*"; //qualquer coisa zero ou mais vezes
		final String regexWildcardInterrogacao = ".?"; //qualquer coisa zero ou uma vez
		
		List<String> regexs = Arrays.asList(regexA, regexE, regexI, regexO,
				regexU, regexC, regexN, regexWildcardAsterisco,
				regexWildcardInterrogacao);
		
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
		return convertida.toString();
	}
	
	
}
