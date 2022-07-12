package com.prueba.rest.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.prueba.rest.Exception.CustomException;

/**
 * Componente con JSON SCHEMA para validar los request que entren a la aplicacion
 */
@Component
public class Validador {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	public boolean validador(JsonNode request,String nombrearchivo) {
	 	try {
			// Se lee el squema
			Resource resource = new ClassPathResource("static/validator/schemes/" + nombrearchivo + ".json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			JSONObject jsonSchema = new JSONObject(new JSONTokener(reader));

			// Se carga el squema
			Schema schema = SchemaLoader.load(jsonSchema);

			// Se valida el JSON
			schema.validate(new JSONObject(request.toString()));

			return true;

		} catch (Exception e) {
			
			LOG.error("Error validando el request :: Validador.squema()");
			LOG.error(e.getMessage());
		}

		return false;
	}

	public void test(JsonNode request, String fileName) {
		if (!validador(request, fileName)) {
			throw new CustomException("Solicitud sin procesar, formato incorrecto de request.", HttpStatus.BAD_REQUEST);
		}
	}

}
