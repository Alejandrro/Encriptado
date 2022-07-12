package com.prueba.rest.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrdenarJson {

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper map = new ObjectMapper();
		
		String json ="[{\"name\":\"Jhon\",\"age\":33},{\"name\":\"Sam\",\"age\":24},{\"name\":\"Mike\",\"age\":65},{\"name\":\"Jenny\",\"age\":33}]";
		
		List<JsonNode> list = map.convertValue( map.readTree( json ), new TypeReference<List<JsonNode>>(){} );
		
//		List<JsonNode> listSort = list.stream().sorted( 
//				Comparator.comparing( sort -> sort.get("age").asLong() ) )
//				.collect( Collectors.toList() );
		
		Collections.sort( list, new Comparator<JsonNode>() {

			@Override
			public int compare(JsonNode o1, JsonNode o2) {
				// TODO Auto-generated method stub
				return o2.get("age").asInt() - o1.get("age").asInt();
			}
		});
		
		System.out.println( list );
		

	}

}
