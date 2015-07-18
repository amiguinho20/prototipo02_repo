package br.com.fences.prototipo02.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.fences.prototipo02.dao.RdoRouboCargaReceptacaoDAO;
import br.com.fences.prototipo02.entity.FiltroRouboCargaReceptacao;
import br.com.fences.prototipo02.entity.Ocorrencia;

public class RdoRouboCargaReceptacaoLazyDataModel extends LazyDataModel<Ocorrencia> {

	private static final long serialVersionUID = 8313096364754460374L;

	private RdoRouboCargaReceptacaoDAO RdoRouboCargaReceptacaoDAO;

	private List<Ocorrencia> ocorrencias;
	private FiltroRouboCargaReceptacao filtro;

	public RdoRouboCargaReceptacaoLazyDataModel(RdoRouboCargaReceptacaoDAO RdoRouboCargaReceptacaoDAO, FiltroRouboCargaReceptacao filtro) {
		this.ocorrencias = new ArrayList<>();
		this.RdoRouboCargaReceptacaoDAO = RdoRouboCargaReceptacaoDAO;
		this.filtro = filtro;
	}

	/**
	 * Metodo necessario para o "cache" dos registros selecionados via
	 * rowSelectMode = checkbox
	 */
	@Override
	public Ocorrencia getRowData(String rowKey) {
		Ocorrencia ocorrencia = RdoRouboCargaReceptacaoDAO.consultar(rowKey);
		return ocorrencia;
	}

	@Override
	public List<Ocorrencia> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		
		ocorrencias = RdoRouboCargaReceptacaoDAO.pesquisarLazy(filtro, first, pageSize);

		int count = RdoRouboCargaReceptacaoDAO.contar(filtro);
		setRowCount(count);

		return ocorrencias;
	}

}
