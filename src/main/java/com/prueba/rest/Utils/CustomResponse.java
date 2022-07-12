package com.prueba.rest.Utils;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.rest.Exception.CustomException;

public class CustomResponse {
	
	private int status;
	private String msg;
	private JsonNode dataResponse;
	private final ObjectMapper mapper = new ObjectMapper();
	
	public CustomResponse() {
		super();
		this.status = HttpStatus.OK.value();
		this.msg = "Solicitud procesada con éxito.";
		this.dataResponse = mapper.createObjectNode();
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

	public JsonNode getDataResponse() {
		return dataResponse;
	}

	public void setDataResponse(JsonNode dataResponse) {
		this.dataResponse = dataResponse;
	}
	
	public JsonNode toJson() {
		
		try {
			
			return mapper.convertValue( this, JsonNode.class );
			
		} catch (Exception e) {
			
			e.printStackTrace();
            throw new CustomException("No fue posible obtener la información en este momento.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
