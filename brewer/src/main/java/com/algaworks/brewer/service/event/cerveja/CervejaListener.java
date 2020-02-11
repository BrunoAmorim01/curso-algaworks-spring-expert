package com.algaworks.brewer.service.event.cerveja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.storage.FotoStorage;

@Component
public class CervejaListener {

	@Autowired
	private FotoStorage fotoStorage;
	
	@EventListener(condition = "#event.temFoto() and #evento.novaFoto")
	public void cervejaSalva(CervejaSalvaEvent event) {
		System.out.println("Nova Cerveja salva - " + event.getCerveja().getNome());
		System.out.println("Nova Cerveja foto - " + event.getCerveja().getFoto());
		fotoStorage.salvar(event.getCerveja().getFoto());
	}
}
