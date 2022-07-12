package com.prueba.rest.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prueba.rest.Exception.CustomException;
import com.prueba.rest.Request.RequestMundo;
import com.prueba.rest.Service.EncriptarAES;
import com.prueba.rest.Service.EncriptarJsonServiceImpl;
import com.prueba.rest.Service.JsonPathGenerator;
import com.prueba.rest.Service.RsaDesencriptarService;
import com.prueba.rest.Utils.CustomResponse;
import com.prueba.rest.Utils.Validador;

@RestController
@RequestMapping(value = "test/")
public class HolaMundoController {

	//private final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	
	@Autowired
	private ObjectMapper map;
	
	@Autowired
	private RsaDesencriptarService service;
	
	@Autowired
	private JsonPathGenerator jsonService;
	
	@Autowired
	private EncriptarAES aes;
	
	@Autowired
	private Validador schema;
	
	@Autowired
	private EncriptarJsonServiceImpl jsonEncript;
	
	//@Autowired
	//private RestTemplate restTemplate;
	
	@PostMapping
	public JsonNode hola( @RequestBody JsonNode request ) {
		
		CustomResponse rs = new CustomResponse();
		
		rs.setDataResponse( map.convertValue( service.desencriptar( request ), JsonNode.class ) );
		
		return rs.toJson();
		
	}
	
	@PostMapping("path")
	public String getJsonPath( @RequestBody JsonNode request ) {
		
		return jsonService.jsonPathString( request );
	
	}
	
	@GetMapping( value = "descargar/archivo", produces = {MediaType.ALL_VALUE} )
	public ResponseEntity<InputStreamSource> downloadFile( ) {
		
		File leerArchivo = new File("e:\\Users\\jlopezv\\Downloads\\file-sample_100kB.doc");
		
		if ( ! leerArchivo.exists() ) {
			
			throw new CustomException("El archivo que se trata de leer no existe", HttpStatus.NOT_FOUND);
		}
		
		InputStream archivoLeido = null;
		
		try {
			
			archivoLeido = new FileInputStream( leerArchivo );
			 
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new CustomException("Error del servidor", HttpStatus.INTERNAL_SERVER_ERROR );
		}
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentDispositionFormData("attachment", leerArchivo.getName() );
		
		return ResponseEntity
				.ok()
				.headers( headers )
				.contentLength( leerArchivo.length() )
				.contentType( MediaType.APPLICATION_OCTET_STREAM )
				.body( new InputStreamResource( archivoLeido ) );
		
	}
	
	@GetMapping( value = "mostrar/imagen", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
	public ResponseEntity<byte[]> showPDFFile( ) {
		
		File leerArchivo = new File("e:\\Users\\jlopezv\\Downloads\\file-sample_100kB.doc");
		
		if ( ! leerArchivo.exists() ) {
			
			throw new CustomException("El archivo que se trata de leer no existe", HttpStatus.NOT_FOUND);
		}
		
		InputStream archivoLeido = null;
		
		byte[] response = null;
		
		try {
			
			archivoLeido = new FileInputStream( leerArchivo );

			response = IOUtils.toByteArray(archivoLeido);

			archivoLeido.close();
			 
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new CustomException("Error del servidor", HttpStatus.INTERNAL_SERVER_ERROR );
		}
		
		return ResponseEntity.ok().body( response );
		
	}
	
	@PostMapping("headers")
	public JsonNode headers( @RequestHeader Map<String, String> headers ) {
		
		CustomResponse rs = new CustomResponse();
		
		List<String> myList =
			    Arrays.asList("a1", "a2", "b1", "c2", "c1");

			myList
			    .stream()
			    .filter( s -> s.startsWith("c") )
			    .map( String :: toUpperCase )
			    .sorted()
			    .forEach( System.out :: println );
		
		rs.setDataResponse( map.convertValue( headers, ObjectNode.class ) );
		
		return rs.toJson();
		
	}
	
	@PostMapping("encriptar")
	public JsonNode aesEncriptador( @RequestBody JsonNode request ) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		CustomResponse rs = new CustomResponse();
		
		ObjectNode node = map.createObjectNode();
		
		//node.put("claveEncriptacion", "secreto!");
		node.put("datosOriginales", request.get("datosOriginales").asText() );
		
		String encriptado = aes.encriptar( request.get("datosOriginales").asText(), request.get("claveEncriptacion").asText() );
		
		node.put("Encriptado", encriptado );
		node.put("Desencriptado", aes.desencriptar( encriptado, request.get("claveEncriptacion").asText() ) );
		
		rs.setDataResponse( node );
		
		return rs.toJson();
		
	}
	
	@PostMapping("deserialize")
	public JsonNode deserialize( @RequestBody RequestMundo request ) {
		
		CustomResponse rs = new CustomResponse();
		
		if ( schema.validador( map.convertValue( request, JsonNode.class ), "firstRequest" ) ) {
			
			rs.setDataResponse( map.convertValue( request , JsonNode.class ) );
			
			return rs.toJson();
		}
		
		throw new CustomException("Error en el request", HttpStatus.BAD_REQUEST );
		
	}
	
	@GetMapping("json")
	public JsonNode getJson( ) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		
		JsonNode json = map.readValue( new URL("https://jsonplaceholder.typicode.com/posts/"), ArrayNode.class );
		
		return json;
		
	}
	
	@PostMapping(value = "encript" , produces = {MediaType.ALL_VALUE})
	public ResponseEntity<byte[]> getJsonEncript( @RequestHeader Map<String, Object> head, @RequestBody JsonNode request, @RequestParam( name = "url" ) Optional<String> url ) throws JsonProcessingException {
		
		/*if ( ! url.isPresent() ) {
			
			//url = Optional.of("https://dev-api.bancoazteca.com.mx:8080/elektra/seguridad/v1/aplicaciones/llaves");
			
			throw new CustomException("Url del api de seguridad no encontrada", HttpStatus.BAD_REQUEST);
		}*/
		
		/*ObjectNode dataResponse = null;
		
		try {
			
			System.out.println( head );
			
			HttpHeaders headers = new HttpHeaders( );
			
			headers.add("Authorization", (String) head.get("authorization") );
			
			HttpEntity<String> datos = new HttpEntity<>( headers );
			
			ResponseEntity<ObjectNode> response = restTemplate.exchange( url.get( ), HttpMethod.GET, datos, ObjectNode.class );
			
			dataResponse = response.getBody();
			
			if ( dataResponse.has("resultado") ) {
				
				if ( ! dataResponse.get("resultado").has("accesoPublico") ) {
					
					throw new CustomException("no se encuentra la llave de accesoPublico", HttpStatus.BAD_REQUEST);
					
				}
				
//				if ( ! dataResponse.get("resultado").has("codigoAutentificacionHash") ) {
//					
//					throw new CustomException("no se encuentra la llave de codigoAutentificacionHash", HttpStatus.BAD_REQUEST);
//				}
			}
			
			else {
				
				throw new CustomException("el api de seguridad no tiene los datos necesarios para cifrar", HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			
			LOG.info("No se puede obtener las llaves privadas", e );
			throw new CustomException("Error del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}*/
		
		JsonNode llaves = map.readTree("{\r\n"
				+ "    \"codigo\":\"200.Elektra-Comercio-Seguridad.100200\",\r\n"
				+ "    \"mensaje\":\"Operación Exitosa\",\r\n"
				+ "    \"folio\":\"100-202207051555303700\",\r\n"
				+ "    \"resultado\":{\r\n"
				+ "        \"idAcceso\":\"E31616E1A97B050EE0539E4C360A9421\",\r\n"
				+ "        \"accesoPublico\":\"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK7Bh1Q4IXdgwZE+/u/c2ZOIPeBI8wXRE4KKB3GTkVwRz9hrhIVp62e6djVqUilsV7oFZBSrod30ZzlNMTOkmfECAwEAAQ==\",\r\n"
				+ "        \"accesoPrivado\":\"MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAr1Ua+Xw1hN3m6vdg8tsCusdW4DOTaEMAG8fuLu74ZQidBJZW7NFNTd2hOYmrtrl69ij8fypXLs8KWxA4SsBs+wIDAQABAkAPW5CHzuHvN/KsnSuQSi/EhYZuEAZ26Pt+83XBBaybSM7vUel16hQz+og93QpEbT5yH7y0PKfoIgtjDYgg4B2lAiEAujGUW3HSeoMHhXf50cw/CFHM38B8rlA/UaUsSQ6tXQcCIQDxERdl3t119uLaVtrVJUyWTU0ofzP5gd87+QhLQu5nbQIgMNLJZUUufQOY2E3Ci1n0eVkB0PZne0n9oIogV7Pqj8cCIGB15lbtBtDdKYkUmBwSAMNjyywZOkLspNbqn1b+QUdBAiEAj/AjQ7GwEXsPReaXy1vavUzsv+39lrvQrDF/kD4moNQ=\",\r\n"
				+ "        \"accesoSimetrico\":\"gioVViza40Ic5HMt+Mv0nc00IwFmejgCxE8fTJsii8A=\",\r\n"
				+ "        \"codigoAutentificacionHash\":\"RydDK0d8uXtlGMsyc8+nQFkzq0QU56YIIAG2Vi7+spM=\"\r\n"
				+ "    }\r\n"
				+ "}");
		
		ObjectWriter write = map.writer( new DefaultPrettyPrinter() );
		
		byte [] jsonFile = write.writeValueAsBytes( jsonEncript.jsonEncript( request, map.convertValue( llaves, ObjectNode.class ) ) );
		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=customers.json")
				.contentType(MediaType.APPLICATION_JSON)
				.contentLength(jsonFile.length)
				.body(jsonFile);
		
	}
	
	@GetMapping
	public JsonNode pruebaNet() {
		
		ObjectNode node = map.createObjectNode();
		
		node.put("msg", "hello world");
		
		return node;
	}
	
}
