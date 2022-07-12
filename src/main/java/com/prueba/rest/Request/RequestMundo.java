package com.prueba.rest.Request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.prueba.rest.Utils.CustomDeserialize;

@JsonDeserialize( using = CustomDeserialize.class )
public class RequestMundo {

	private Long id;
	
	private String lenguaje;
	
	private String sistema;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLenguaje() {
		return lenguaje;
	}

	public void setLenguaje(String lenguaje) {
		this.lenguaje = lenguaje;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	@Override
	public String toString() {
		return "RequestMundo [id=" + id + ", lenguaje=" + lenguaje + ", sistema=" + sistema + "]";
	}

	public RequestMundo( Long id, String lenguaje, String sistema ) {
		super();
		this.id = id;
		this.lenguaje = lenguaje;
		this.sistema = sistema;
	}
	
	public RequestMundo() {}
	
}
