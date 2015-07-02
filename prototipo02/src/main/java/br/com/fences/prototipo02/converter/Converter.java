package br.com.fences.prototipo02.converter;

import org.bson.Document;

/**
 * @param <T> Tipo de Objeto para conversao.
 */
public abstract class Converter<T> {

	public abstract Document paraDocumento(T obj);
	public abstract T paraObjeto(Document doc);
	
	/**
	 * Quando for recuperar o JSON do MongoDB para obj...
	 * De : "_id" : { "$oid" : "55805588bf836defd8ac32ec" }
	 * Para : "_id" : "55805588bf836defd8ac32ec"
	 */
	protected String transformarIdParaJsonObj(String json)
	{
		int indice = 0;
		while (json.indexOf("_id", indice) > 1)
		{
			indice = json.indexOf("_id", indice);
			
			int indiceAbreParenteses = json.indexOf("{", indice);
			int indiceFechaParenteses = json.indexOf("}", indice);
			indiceFechaParenteses++; //-- incluir o parenteses
			String conteudoOriginalId = json.substring(indiceAbreParenteses, indiceFechaParenteses);
			int indiceFechaAspasId = conteudoOriginalId.lastIndexOf("\"");
			int indiceAbreAspasId = conteudoOriginalId.lastIndexOf("\"", indiceFechaAspasId - 1);
			indiceFechaAspasId++; //-- incluir a aspas
			String conteudoNovoId = conteudoOriginalId.substring(indiceAbreAspasId, indiceFechaAspasId);

			json = json.replace(conteudoOriginalId, conteudoNovoId);
			
			indice = indiceFechaParenteses + 1;
		}
		
		//-- (associacao transiente) transformar id da ocorrencias COMPLEMENTARES
		while (json.contains("$oid"))
		{
			json = json.replace("$oid", "_id");
		}
		
		return json;
	}
	
	/**
	 * Quando for passar o JSON para o MongoDB...
	 * De : "_id" : "55805588bf836defd8ac32ec"
	 * Para : "_id" : { "$oid" : "55805588bf836defd8ac32ec" }
	 */
	protected String transformarIdParaJsonDb(String json)
	{
		int indice = 0;
		//-- transformar id da ocorrencia principal
		while (json.indexOf("_id", indice) > 1)
		{
			indice = json.indexOf("_id", indice);
			indice = json.indexOf(":", indice);
			
			int indiceAbreAspas = json.indexOf("\"", indice);
			int indiceFechaAspas = json.indexOf("\"", indiceAbreAspas + 1);
			indiceFechaAspas++; //-- incluir a aspas do fechamento
			
			String conteudoOriginalId = json.substring(indiceAbreAspas, indiceFechaAspas);
			String conteudoNovoId = "{ \"$oid\" :" + conteudoOriginalId + " }";

			json = json.replace(conteudoOriginalId, conteudoNovoId);
			
			indice = indiceFechaAspas + 1;
		}
		return json;
	}
	

}
