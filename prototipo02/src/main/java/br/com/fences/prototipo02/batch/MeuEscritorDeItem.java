package br.com.fences.prototipo02.batch;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

@Named
public class MeuEscritorDeItem extends AbstractItemWriter{

	@Override
	public void writeItems(List<Object> items) throws Exception {
		
		System.out.println("MeuEscritorDeItem.writeItems... size: " + items.size());
		for (Object obj : items)
		{
			MeuRegistroSaida meuRegistroSaida = (MeuRegistroSaida) obj;
			System.out.println("item: " + meuRegistroSaida.getLetra());
		}
		System.out.println("MeuEscritorDeItem.writeItems... finalizado");
	}

}
