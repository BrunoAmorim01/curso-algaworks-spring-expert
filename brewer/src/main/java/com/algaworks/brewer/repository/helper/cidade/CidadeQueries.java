package com.algaworks.brewer.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;

public interface CidadeQueries {
	public Page<Cidade> filtrar(CidadeFilter filter, Pageable pageable);
}
