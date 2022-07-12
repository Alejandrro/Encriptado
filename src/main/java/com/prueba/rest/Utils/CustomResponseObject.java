package com.prueba.rest.Utils;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.rest.Exception.CustomException;

public class CustomResponseObject {
	
	private int status;
	
	private String msg;
	
	private JsonNode resultado;
	
	private ObjectMapper map = new ObjectMapper();

	public CustomResponseObject( ) {
		
		this.status = HttpStatus.OK.value();
		this.msg = "Operacion Exitosa";
		this.resultado = map.createObjectNode();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public JsonNode getResultado() {
		return resultado;
	}

	public void setResultado(JsonNode resultado) {
		this.resultado = resultado;
	}

	public JsonNode toJson() {
		
		try {
			
			return map.convertValue( this, JsonNode.class);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new CustomException("Error del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
