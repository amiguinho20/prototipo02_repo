package br.com.fences.prototipo02.exception;

public class GoogleLimiteAtingidoRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public GoogleLimiteAtingidoRuntimeException(){
		super();
	}
	
	public GoogleLimiteAtingidoRuntimeException(String string) {
		super(string);
	}
	
}
