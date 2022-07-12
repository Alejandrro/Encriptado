package com.prueba.rest.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prueba.rest.Exception.CustomException;
import com.prueba.rest.Request.RequestMundo;

public class CustomDeserialize extends JsonDeserializer<RequestMundo> {
	
	RequestMundo res = null;
	
	@Override
	public RequestMundo deserialize( JsonParser jsonParser, DeserializationContext ctxt ) {
		
		RequestMundo request = null;
		
		try {
			
			ObjectNode node = jsonParser.readValueAsTree();

			request = generateObject( node );
			
		} catch (IOException  e ) {
			
			throw new CustomException("Json Mal Formado", HttpStatus.BAD_REQUEST);
		}
		
		
		return request;
	}

	private RequestMundo generateObject( ObjectNode node ) {
		
		res = new RequestMundo();
		
		Field [] fields = res.getClass().getDeclaredFields();
		
		node.fieldNames().forEachRemaining( i -> {
			
			Field campo = Arrays.stream( fields ).filter( f -> f.getName().equals( i ) ).findFirst().orElse(null);
			
			if ( campo != null ) {
				
				try {
					
					campo.setAccessible( true );
					
					if ( campo.getType() == Integer.class ) {
						
						campo.set( res, node.get( i ).asInt() );
						
					} else if ( campo.getType() == Long.class ) {
						
						campo.set( res, node.get( i ).asLong() );
						
					} else if ( campo.getType() == Boolean.class ) {
						
						campo.set( res, node.get( i ).asBoolean() );
						
					} else if ( campo.getType() == String.class ) {
						
						campo.set( res, node.get( i ).asText() );
						
					} else if ( campo.getType() == Character.class ) {
						
						campo.set( res, node.get( i ).asText().charAt(0) );
						
					} else if ( campo.getType() == RequestMundo.class ) {
						
					}
					
				} catch (Exception e) {
					
					throw new CustomException("Error en el request no coinciden los tipos de datos", HttpStatus.BAD_REQUEST);
				}
				
			} else {
				
				throw new CustomException("Error Propiedades Desconocidas En El Request", HttpStatus.BAD_REQUEST);
			}
			
		});
		
		return res;
		
	}
	
}
