package com.prueba.rest.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

@Service
public class JsonPathGenerator {

	private List<String> path = new ArrayList<>();
	
	private String aux = "";
	
	//private String arrayStringaux = "";
	
	public String jsonPathString( JsonNode request ) {
		
		path.clear();
		
		aux = "";
		
		if ( request.getNodeType() == JsonNodeType.ARRAY ) {
			
			System.out.println( "is Array ");
		}
		
		else {
			
			request.fieldNames().forEachRemaining( i -> {
				
				if ( request.get( i ).isObject() ) {
					
					aux = i;
					
					isJson( request.get( i ) );
					
				}
				
				else if ( request.get( i ).isArray() ) {
					
					aux = i;
					
					isArray( request.get( i ) );
					
					aux = "";
				}
				
				else {
					path.add( "\"$." + i + "\",");
				}
			});
			
		}

		path.stream().forEach(  p -> System.out.println( p ) ) ;
		
		aux = "";
		
		for ( int j = 0; j < path.size(); j++ ) {
			
			if ( j == ( path.size() -1 ) ) {
				
				aux += path.get( j ).substring( 0, ( path.get( j ).length() -1 ) ) + "\n";
			}
			
			else {
				
				aux += path.get( j ) + "\n";
			}
			
		}
		
		return aux;
	}
	
	private void isJson( JsonNode json ) {
		
		json.fieldNames().forEachRemaining( x -> {
			
			if ( json.get( x ).isObject() ) {
				
				aux += "." + x;
				
				isJson( json.get( x ) );
				
				aux = aux.replaceFirst( "." + x, "");
			}
			
			else if ( json.get( x ).isArray() ) {
				
				aux += "." + x;
				
				isArray( json.get( x ) );
				
				aux = aux.replace( "." + x , "");
				
			}
			
			else {
				
				path.add("\"$." + aux + "." + x + "\",");
			}
			
		});
		
	}
	
	private void isArray( JsonNode jsonNode ) {
		
		if ( jsonNode.get(0).getNodeType() == JsonNodeType.STRING ) {
			
			//path.add("\"$." + aux + "[*]\",");
			path.add("\"$." + aux + "[*]" + "\",");
		}
		
		else if ( jsonNode.get(0).getNodeType() == JsonNodeType.OBJECT ) {
			
			aux += "[*]";
			
			isJson( jsonNode.get(0) );
			
			int posicion = aux.lastIndexOf("[*]");
			
			aux = aux.substring(0, posicion );
			
		}
		
		else if ( jsonNode.get(0).getNodeType() == JsonNodeType.BOOLEAN ) {
			
			//path.add("\"$." + aux + "[*]\",");
			path.add("\"$." + aux + "\",");
		}
		
		else if ( jsonNode.get(0).getNodeType() == JsonNodeType.NUMBER ) {
			
			//path.add("\"$." + aux + "[*]\",");
			path.add("\"$." + aux + "\",");
		}
		
	}
	
}
