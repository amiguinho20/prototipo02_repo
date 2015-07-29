package br.com.fences.prototipo02.batch;

import java.util.Arrays;
import java.util.List;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

@Named 
public class MeuProcessadorDeItem implements ItemProcessor { 
	
	List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
	
	@Override
	public MeuRegistroSaida processItem(Object t) { 
		
		MeuRegistroEntrada entrada = (MeuRegistroEntrada) t;
		MeuRegistroSaida saida = new MeuRegistroSaida(); 

		String letra = list.get(entrada.getNumero());
		saida.setLetra(letra);
		
		System.out.println("MeuProcessadorDeItem.processItem... de:" + entrada.getNumero() + " para " + saida.getLetra());

		return saida; 
	} 
}
