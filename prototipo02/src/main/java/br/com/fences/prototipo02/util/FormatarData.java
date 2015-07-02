package br.com.fences.prototipo02.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FormatarData {
	
	private static DateFormat tipo01 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static DateFormat tipo02 = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat tipo03 = new SimpleDateFormat("dd/MM/yyyy");
	private static DateFormat tipo04 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static DateFormat tipo05 = new SimpleDateFormat("HHmm");
	
	/**
	 * yyyyMMddHHmmss
	 */
	public static DateFormat getAnoMesDiaHoraMinutoSegundoConcatenados()
	{
		return tipo01;
	}
	
	/**
	 * yyyyMMdd
	 */
	public static DateFormat getAnoMesDiaContatenado()
	{
		return tipo02;
	}
	
	/**
	 * dd/MM/yyyy
	 */
	public static DateFormat getDiaMesAnoComBarras()
	{
		return tipo03;
	}
	
	/**
	 * dd/MM/yyyy HH:mm:ss
	 */
	public static DateFormat getDiaMesAnoComBarrasEHoraMinutoSegundoComDoisPontos()
	{
		return tipo04;
	}
	
	/**
	 * HHmm
	 */
	public static DateFormat getHoraMinutoContatenado()
	{
		return tipo05;
	}
	
}
