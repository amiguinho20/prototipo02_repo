package br.com.fences.prototipo02.util;

public class Verificador {
	
	public static boolean isValorado(final String arg)
	{
		boolean ret = true;
		if (arg == null || arg.trim().isEmpty())
		{
			ret = false;
		}
		return ret;
	}
	
	public static boolean isInteiro(String str) {
        try 
        {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

}
