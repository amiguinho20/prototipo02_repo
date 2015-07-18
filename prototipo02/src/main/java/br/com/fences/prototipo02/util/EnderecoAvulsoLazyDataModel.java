package br.com.fences.prototipo02.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.fences.prototipo02.dao.EnderecoAvulsoDAO;
import br.com.fences.prototipo02.entity.EnderecoAvulso;
import br.com.fences.prototipo02.entity.FiltroEnderecoAvulso;

public class EnderecoAvulsoLazyDataModel extends LazyDataModel<EnderecoAvulso> {

	private static final long serialVersionUID = 8313096364754460374L;
	
	private Logger logger =  Logger.getLogger(EnderecoAvulsoLazyDataModel.class);  


	private EnderecoAvulsoDAO enderecoAvulsoDAO;

	private List<EnderecoAvulso> enderecosAvulsos;
	private FiltroEnderecoAvulso filtro;

	public EnderecoAvulsoLazyDataModel(EnderecoAvulsoDAO enderecoAvulsoDAO, FiltroEnderecoAvulso filtro) {
		this.enderecosAvulsos = new ArrayList<>();
		this.enderecoAvulsoDAO = enderecoAvulsoDAO;
		this.filtro = filtro;
	}

	/**
	 * Metodo necessario para o "cache" dos registros selecionados via
	 * rowSelectMode = checkbox
	 */
	@Override
	public EnderecoAvulso getRowData(String rowKey) {
		EnderecoAvulso enderecoAvulso = enderecoAvulsoDAO.consultar(rowKey);
		return enderecoAvulso;
	}

	@Override
	public List<EnderecoAvulso> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
	
		filtro.setPrimeMapFiltro(filters);
		
		enderecosAvulsos = enderecoAvulsoDAO.pesquisarLazy(filtro, first, pageSize);

		int count = enderecoAvulsoDAO.contar(filtro);
		setRowCount(count);

		filtro.setPrimeMapFiltro(null);
		
		return enderecosAvulsos;
	}

}
