package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.helper.cidade.CidadeQueries;

public interface Cidades extends JpaRepository<Cidade, Long>, CidadeQueries{
	
	public List<Cidade> findByEstadoCodigo(Long codigoEstado);
	public Optional<Cidade> findByNomeAndEstado(String nomeCidade, Estado estado);
	//public Cidade findByCodigoFetchingEstado(Long codigo);
	@Query("SELECT c FROM Cliente cli  LEFT OUTER JOIN cli.endereco.cidade c  LEFT OUTER JOIN FETCH c.estado e  WHERE cli.codigo = :codigoCliente")
    public Cidade findCidadeComEstado(@Param("codigoCliente") Long codigo);
	
	
}
