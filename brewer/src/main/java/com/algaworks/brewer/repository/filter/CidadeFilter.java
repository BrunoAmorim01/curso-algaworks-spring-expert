package com.algaworks.brewer.repository.filter;

import com.algaworks.brewer.model.Estado;

public class CidadeFilter {
	
	private Estado estado;
	private String Nome;
	
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public String getNome() {
		return Nome;
	}
	public void setNome(String nome) {
		Nome = nome;
	}
	
}
