package br.com.fences.prototipo02.batch;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Named;

@Named
public class MeuLeitorDeItem extends AbstractItemReader {
	List<Integer> list;
	int indice = 0;

	@Override 
	public void open(Serializable c) throws Exception {
		//-- aqui faz leitura de extracao
		list = Arrays.asList(1,2,3,4,5,6,7,8,9,10); 
		System.out.println("MeuLeitorDeItem.open...");
	}	
	
	@Override
	public MeuRegistroEntrada readItem() {
		
		MeuRegistroEntrada meuRegistroEntrada = null;
		if (indice < list.size())
		{
			meuRegistroEntrada = new MeuRegistroEntrada(list.get(indice));
			

			
			indice++;
		}
		return meuRegistroEntrada;
	}
}
