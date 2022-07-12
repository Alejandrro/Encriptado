package com.prueba.rest.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class EncriptarJsonServiceImpl {

	@Autowired
	private RsaDesencriptarService encriptador;
	
	@Autowired
	private EncriptarAES Aes;
	
	@Autowired
	private ObjectMapper map;
	
	private String keyPublic = "";
	
	private String accesoSimetrico = "";
	
	public JsonNode jsonEncript ( JsonNode request, ObjectNode llave ) {
		
		LinkedHashMap<String, Object> mapJson = new LinkedHashMap<>( );
		
		keyPublic = llave.get("resultado").get("accesoPublico").asText();
		
		accesoSimetrico = llave.get("resultado").get("accesoSimetrico").asText();
			
		request.fieldNames().forEachRemaining( i -> {
			
			if ( request.get( i ).isObject() ) {
					
				mapJson.put( i, isJson( request.get( i ) ) );
			}
				
			else if ( request.get( i ).isArray() ) {
				
				if ( i.contains("#") || i.contains("$") ) {
					
					mapJson.put( i.substring( 0, ( i.length() - 1 ) ), arrayGenerico( request.get( i ), Boolean.FALSE ) );
				}
				
				else if ( request.get( i ).get(0).getNodeType() == JsonNodeType.STRING ) {
					
					mapJson.put( i, arrayGenerico( request.get( i ), Boolean.FALSE ) );
				}
				
				else {
					
					mapJson.put( i, isArray( request.get( i ) ) );
				}
					
			}
				
			else {
				
				if ( i.contains("#") || i.contains("$") ) {
					
					mapJson.put( i.substring( 0, ( i.length() - 1 ) ), i.contains("#") ? encriptador.encriptar( keyPublic, request.get( i ).asText() ) : Aes.encriptar( request.get( i ).asText() , accesoSimetrico ) );
				}
				
				else {
					
					mapJson.put( i, request.get( i ).asText() );
				}
			}
				
		});
		
		return map.convertValue( mapJson, JsonNode.class );
		
	}
	
	private LinkedHashMap<String, Object> isJson( JsonNode json ) {
		
		LinkedHashMap< String, Object > subNode = new LinkedHashMap<>( );
		
		json.fieldNames().forEachRemaining( x -> {
			
			if ( json.get( x ).isObject() ) {
				
				subNode.put( x, isJson( json.get( x ) ) );
			}
			
			else if ( json.get( x ).isArray() ) {
				
				if ( x.contains("#") ) {
					
					subNode.put( x.substring( 0, ( x.length() - 1 ) ), arrayGenerico( json.get( x ), Boolean.TRUE ) );
				}
				
				else if ( json.get( x ).get(0).getNodeType() == JsonNodeType.STRING ) {
			
					subNode.put( x, arrayGenerico( json.get( x ), Boolean.FALSE ) );
				}
				
				else {
					
					subNode.put( x, isArray( json.get( x ) ) );
				}
				
			}
			
			else {
				
				if ( x.contains("#") || x.contains("$") ) {
					
					subNode.put( x.substring( 0, ( x.length() -1 ) ), x.contains("#") ? encriptador.encriptar( keyPublic, json.get( x ).asText() ) : Aes.encriptar( json.get( x ).asText(), accesoSimetrico) );
				}
				
				else {
					
					subNode.put( x, json.get( x ).asText() );
				}
				
			}
				
		});
		
		return subNode;
		
	}
	
	private List<LinkedHashMap<String, Object>> isArray( JsonNode jsonNode ) {
		
		List<LinkedHashMap<String, Object>> arrayResponse = new ArrayList<>( );
		
		jsonNode.forEach( i -> {
			
			LinkedHashMap<String, Object> jsonObject = new LinkedHashMap<>( );
			
			i.fieldNames().forEachRemaining( y -> {
				
				if ( i.get( y ).isObject() ) {
					
					jsonObject.put( y, isJson( i.get( y ) ) );
				}
				
				else if (  i.get( y ).isArray() ) {
					
					if ( y.contains("#") ) {
						
						jsonObject.put( y.substring( 0, ( y.length() - 1 ) ), arrayGenerico( i.get( y ), Boolean.TRUE ) );
					}
					
					else if ( i.get( y ).get(0).getNodeType() == JsonNodeType.STRING ) {
						
						jsonObject.put( y, arrayGenerico( i.get( y ), Boolean.FALSE ) );
					}
					
					else {
						
						jsonObject.put( y, isArray( i.get( y ) ) );
					}
				}
				
				else {
					
					if ( y.contains("#") || y.contains("$")  ) {
						
						jsonObject.put( y.substring( 0, ( y.length() -1 ) ), y.contains("#") ? encriptador.encriptar( keyPublic, i.get( y ).asText() ) : Aes.encriptar( i.get( y ).asText(), accesoSimetrico ) );
					}
					
					else {
						
						jsonObject.put( y, i.get( y ).asText() );
					}
				}
				
			});
			
			arrayResponse.add( jsonObject );
			
		});
		
		return arrayResponse;
		
	}
	
	private List<Object> arrayGenerico ( JsonNode jsonNode, Boolean banderaArray ) {
		
		List<Object> arraysSimples = new ArrayList<>( );
		
		if ( banderaArray ) {
			
			jsonNode.forEach( i -> {
				
				LinkedHashMap<String, Object> jsonObject = new LinkedHashMap<>( );
				
				if ( i.getNodeType() == JsonNodeType.OBJECT ) {
					
					i.fieldNames().forEachRemaining( y -> {
						
						if ( i.get( y ).isObject() ) {
							
							jsonObject.put( y, i.get( y ) );
						}
						
						else if (  i.get( y ).isArray() ) {
							
							if ( y.contains("#") ) {
								
								jsonObject.put( y.substring( 0, ( y.length() - 1 ) ), arrayGenerico( i.get( y ), Boolean.TRUE ) );
							}
							
							else if ( i.get( y ).get(0).getNodeType() == JsonNodeType.STRING ) {
								
								jsonObject.put( y, arrayGenerico( i.get( y ), Boolean.FALSE ) );
							}
							
							else {
								
								jsonObject.put( y, isArray( i.get( y ) ) );
							}
						}
						
						else {
							
							if ( y.contains("#") ) {
								
								jsonObject.put( y.substring( 0, ( y.length() -1 ) ), encriptador.encriptar( keyPublic, i.get( y ).asText() ) );
							}
							
							else {
								
								jsonObject.put( y, i.get( y ).asText() );
							}
						}
						
					});
					
					arraysSimples.add( jsonObject );
				}
				
				else if ( i.getNodeType() == JsonNodeType.ARRAY ) {
					
					arraysSimples.add( arrayGenerico( i, Boolean.FALSE ) );
					
				}
				
				else {
					
					arraysSimples.add( encriptador.encriptar( keyPublic, i.asText() ) );
				}
				
			});

		}
		
		else {
			
			jsonNode.forEach( i -> {
				
				LinkedHashMap<String, Object> jsonObject = new LinkedHashMap<>( );
				
				if ( i.getNodeType() == JsonNodeType.STRING ) {
					
					if ( i.asText().contains("#") ) {
						
						arraysSimples.add( encriptador.encriptar( keyPublic, i.asText().substring( 0, ( i.asText().length() - 1  ) ) ) );
					}
					
					else {
						
						arraysSimples.add( i.asText() );
					}
					
					//jsonObject.put( y.substring( 0, ( y.length() -1 ) ), encriptador.encriptar( keyPublic, i.get( y ).asText() ) );
				}
				
				else if ( i.getNodeType() == JsonNodeType.NUMBER ) {
					
					arraysSimples.add( encriptador.encriptar( keyPublic, i.asText() ) );//jsonObject.put( y.substring( 0, ( y.length() -1 ) ), encriptador.encriptar( keyPublic, i.get( y ).asText() ) );

				}
				
				else if ( i.getNodeType() == JsonNodeType.BOOLEAN ) {
					
					arraysSimples.add( encriptador.encriptar( keyPublic, i.asText() ) );//jsonObject.put( y.substring( 0, ( y.length() -1 ) ), encriptador.encriptar( keyPublic, i.get( y ).asText() ) );

				}
				
				else if ( i.getNodeType() == JsonNodeType.OBJECT ) {
					
					i.fieldNames().forEachRemaining( y -> {
						
						if ( i.get( y ).isObject() ) {
							
							jsonObject.put( y, i.get( y ) );
						}
						
						else if (  i.get( y ).isArray() ) {
							
							if ( y.contains("#") ) {
								
								jsonObject.put( y.substring( 0, ( y.length() - 1 ) ), arrayGenerico( i.get( y ), Boolean.TRUE ) );
							}
							
							else if ( i.get( y ).get(0).getNodeType() == JsonNodeType.STRING ) {
								
								jsonObject.put( y, arrayGenerico( i.get( y ), Boolean.FALSE ) );
							}
							
							else {
								
								jsonObject.put( y, isArray( i.get( y ) ) );
							}
						}
						
						else {
							
							if ( y.contains("#") ) {
								
								jsonObject.put( y.substring( 0, ( y.length() -1 ) ), encriptador.encriptar( keyPublic, i.get( y ).asText() ) );
							}
							
							else {
								
								jsonObject.put( y, i.get( y ).asText() );
							}
						}
						
					});
					
					arraysSimples.add( jsonObject );
				}
				
				else {
					
					
					arraysSimples.add( arrayGenerico( i, Boolean.FALSE ) );
					
				}
				
			});
			
		}
			
		return arraysSimples;
		
	}
	
}
