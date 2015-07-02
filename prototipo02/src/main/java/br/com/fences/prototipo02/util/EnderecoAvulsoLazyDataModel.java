package br.com.fences.prototipo02.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.fences.prototipo02.dao.EnderecoAvulsoDAO;
import br.com.fences.prototipo02.dao.RdoRouboCargaReceptacaoDAO;
import br.com.fences.prototipo02.entity.EnderecoAvulso;
import br.com.fences.prototipo02.entity.Filtro;
import br.com.fences.prototipo02.entity.Ocorrencia;

public class EnderecoAvulsoLazyDataModel extends LazyDataModel<EnderecoAvulso> {

	private static final long serialVersionUID = 8313096364754460374L;

	private EnderecoAvulsoDAO enderecoAvulsoDAO;

	private List<EnderecoAvulso> enderecosAvulsos;
	private Filtro filtro;

	public EnderecoAvulsoLazyDataModel(EnderecoAvulsoDAO enderecoAvulsoDAO, Filtro filtro) {
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
		enderecosAvulsos = enderecoAvulsoDAO.pesquisarLazy(filtro, first, pageSize);

		int count = enderecoAvulsoDAO.contar(filtro);
		setRowCount(count);

		return enderecosAvulsos;
	}

}
