package br.com.fences.prototipo02.entity;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Named;

import br.com.fences.prototipo02.util.AcentuacaoParaRegex;
import br.com.fences.prototipo02.util.Verificador;

import com.mongodb.BasicDBObject;
 
@Named
@javax.faces.view.ViewScoped
//@SessionScoped
public class FiltroEnderecoAvulso implements Serializable{ 
  
	private static final long serialVersionUID = -7701810214532312594L;
	
	private Map<String, Object> primeMapFiltro;
	
	public BasicDBObject montarPesquisa()
	{
		BasicDBObject pesquisa = new BasicDBObject();
		if (primeMapFiltro != null && !primeMapFiltro.isEmpty())
		{
			for (Map.Entry<String, Object> filtro : primeMapFiltro.entrySet())
			{
				if (filtro.getValue() != null)
				{
					String valor = filtro.getValue().toString().trim();
					if (Verificador.isValorado(valor))
					{
						String convertido = AcentuacaoParaRegex.converter(valor);
						pesquisa.append(filtro.getKey(), new BasicDBObject("$regex", convertido).append("$options", "i"));
					}
				}
			}
		}
		return pesquisa;
	}

	public Map<String, Object> getPrimeMapFiltro() {
		return primeMapFiltro;
	}

	public void setPrimeMapFiltro(Map<String, Object> primeMapFiltro) {
		this.primeMapFiltro = primeMapFiltro;
	}
	
	
	
}