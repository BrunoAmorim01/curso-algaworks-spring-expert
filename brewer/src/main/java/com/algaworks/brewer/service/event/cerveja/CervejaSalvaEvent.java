package com.algaworks.brewer.service.event.cerveja;

import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
//nao e mais usado
@Deprecated
public class CervejaSalvaEvent {
	
	private Cerveja cerveja;

	public CervejaSalvaEvent(Cerveja cerveja) {
		this.cerveja = cerveja;
	}

	public Cerveja getCerveja() {
		return cerveja;
	}
	
	public boolean temFoto() {
		return !StringUtils.isEmpty(cerveja.getFoto());
	}
	
	public boolean isNovaFoto() {
		return cerveja.isNovaFoto();
	}
	
	
}
