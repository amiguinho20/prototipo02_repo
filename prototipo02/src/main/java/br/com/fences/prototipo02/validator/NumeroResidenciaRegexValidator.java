package br.com.fences.prototipo02.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.omnifaces.util.Messages;

import br.com.fences.prototipo02.util.Verificador;


/**
 * Permite apenas de 1 a 5 digitos, e o numero deve estar entre 1 e 15000 <br/>
 */
@FacesValidator("numeroResidencialRegexValidator")
public class NumeroResidenciaRegexValidator implements Validator {
     
	final static String REGEX = "\\d{1,5}";
			
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException 
    {
    	String valor = value.toString();
        if (Verificador.isValorado(valor)) 
        {
        	if (valor.matches(REGEX))
        	{
        		int numero = Integer.parseInt(valor);
        		if (numero < 1 || numero > 15000)
        		{
        			String msg = "Apenas números entre 1 e 15000 são permitidos.";
            		throw new ValidatorException(Messages.createError(msg));
        		}
        	}
        	else
        	{
        		String msg = "Apenas de 1 a 5 dígitos permitidos.";
        		throw new ValidatorException(Messages.createError(msg));
        	}
        }
    }
}