package br.com.fences.prototipo02.converter;

import javax.enterprise.context.ApplicationScoped;

import org.bson.Document;

import br.com.fences.prototipo02.entity.EnderecoAvulso;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ApplicationScoped
public class EnderecoAvulsoConverter extends Converter<EnderecoAvulso>{

	private Gson gson = new GsonBuilder().create();
	
	@Override
	public Document paraDocumento(EnderecoAvulso ocorrencia) 
	{
    	String json = gson.toJson(ocorrencia);
    	String jsonMongoDB = transformarIdParaJsonDb(json);
    	Document documento = Document.parse(jsonMongoDB);
		return documento;
	}

	@Override
	public EnderecoAvulso paraObjeto(Document doc) 
	{
		String jsonMongoDB = doc.toJson();
    	String json = transformarIdParaJsonObj(jsonMongoDB);
    	EnderecoAvulso enderecoAvulso = gson.fromJson(json, EnderecoAvulso.class);
    	return enderecoAvulso;
	}	
}
