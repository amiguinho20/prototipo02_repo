package br.com.fences.prototipo02.util;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

import org.primefaces.model.map.LatLng;

import br.com.fences.prototipo02.entity.Ocorrencia;
import br.com.fences.prototipo02.exception.GoogleLimiteAtingidoRuntimeException;
import br.com.fences.prototipo02.exception.GoogleZeroResultsRuntimeException;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;

public class EnderecoGeocodeUtil {

	private static final String IDIOMA = "pt";
	private static final Geocoder geocoder = new Geocoder();
	
	public static LatLng converter(Ocorrencia ocorrencia) throws Exception
	{
		LatLng latLng = null;
		if (ocorrencia != null)
		{
			latLng = verificarExistenciaPreviaDeGeocode(ocorrencia);
			//if (latLng == null)
			//{
			//	//-- recuperar o geocode do google
			//	String endereco = formatarEndereco(ocorrencia);
			//	latLng = converterEnderecoEmLatLng(endereco);
			//}
		}
		return latLng;
	}
	
	
	public static LatLng verificarExistenciaPreviaDeGeocode(Ocorrencia ocorrencia)
	{
		LatLng latLng = null;
		if (Verificador.isValorado(ocorrencia.getLatitude()) && Verificador.isValorado(ocorrencia.getLongitude()))
		{	//-- utilizar o geocode original
			float latitude = Float.parseFloat(ocorrencia.getLatitude());
			float longitude = Float.parseFloat(ocorrencia.getLongitude());
			latLng = new LatLng(latitude, longitude);
		}
		else if (Verificador.isValorado(ocorrencia.getGoogleLatitude()) && Verificador.isValorado(ocorrencia.getGoogleLongitude()))
		{	//-- utilizar o geocode pre-processado do google
			float latitude = Float.parseFloat(ocorrencia.getGoogleLatitude());
			float longitude = Float.parseFloat(ocorrencia.getGoogleLongitude());
			latLng = new LatLng(latitude, longitude);
		}
		else
		{ 
			//-- nao tem geocode, retorna nulo
		}
		return latLng;	
	}
	
	public static LatLng gerarGeocode(Ocorrencia ocorrencia)
	{
		LatLng latLng = null;
		if (ocorrencia != null && verificarExistenciaPreviaDeGeocode(ocorrencia) == null)
		{
			String enderecoFormatado = formatarEndereco(ocorrencia);
			latLng = converterEnderecoEmLatLng(enderecoFormatado);
		}
		return latLng;
	}
	
	private static LatLng converterEnderecoEmLatLng(String endereco)
	{
		LatLng latLng = null;
		endereco = retirarAcentos(endereco);
		int qtdTentativa = 0;
		boolean sucesso = false;
		GeocoderStatus geocoderStatus = null;
		try
		{
			while (!sucesso && qtdTentativa < 3)
			{
				qtdTentativa++;

				GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(endereco).setLanguage(IDIOMA).getGeocoderRequest();
				GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

				geocoderStatus = geocoderResponse.getStatus();
				if (!geocoderStatus.equals(GeocoderStatus.OVER_QUERY_LIMIT))
				{
					sucesso = true;
					
					List<GeocoderResult> results = geocoderResponse.getResults(); 
					if (results != null && !results.isEmpty())
					{
						float latitude = results.get(0).getGeometry().getLocation().getLat().floatValue();
					    float longitude = results.get(0).getGeometry().getLocation().getLng().floatValue();
						latLng = new LatLng(latitude, longitude);
					}
					else
					{
						System.out.println("converterEnderecoEmLatLng: geocode erro[" + geocoderStatus + "] para o endereco [" + endereco + "].");
					}
				}
				else
				{
					Thread.sleep(3000); //-- aguardar 3 segundos 
				}
			}
			if (!geocoderStatus.equals(GeocoderStatus.OK))
			{
				if (geocoderStatus.equals(GeocoderStatus.OVER_QUERY_LIMIT))
				{
					throw new GoogleLimiteAtingidoRuntimeException("Limite de 2500 pesquisas diárias foi atingido.");
				}
				if (geocoderStatus.equals(GeocoderStatus.ZERO_RESULTS))
				{
					throw new GoogleZeroResultsRuntimeException("Endereço sem retorno do Google.");
				}
			}
		}
		catch (GoogleLimiteAtingidoRuntimeException e)
		{	
			throw e;
		}
		catch (GoogleZeroResultsRuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			System.err.println("Erro no processamento do Geocode : " + e.getMessage());
			e.printStackTrace();
		}
		return latLng;
	}

	
	public static String formatarEndereco(Ocorrencia ocorrencia)
	{
		String endereco = concatenarEndereco(ocorrencia.getLogradouro(),
				ocorrencia.getNumeroLogradouro(),
				ocorrencia.getBairro(), ocorrencia.getCidade(),
				ocorrencia.getIdUf());
		return endereco;
	}
	
	private static String concatenarEndereco(String... campos) 
	{
		String resultado = "";
		for (String campo : campos) 
		{
			if (campo != null && !campo.trim().isEmpty() && !campo.trim().equals("0"))
			{
				campo = campo.replaceAll(",", ""); //-- retirar virgulas adicionais
				if (!resultado.isEmpty())
				{
					resultado += ", ";
				} 
				resultado += campo.trim(); 					
			}
		}
		return resultado;
	}
	
	public static String retirarAcentos(String arg)
	{
	    String normalizador = Normalizer.normalize(arg, Normalizer.Form.NFD);
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    String retorno = pattern.matcher(normalizador).replaceAll("");
	    return retorno;
	}
	
	
}
