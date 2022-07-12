package com.prueba.rest.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.prueba.rest.Exception.CustomException;

@Service
public class RsaDesencriptarService {

	public String desencriptar( JsonNode request ) {
		
		System.out.println("Convert RSA public key into a string an dvice versa");
        
		byte [] llavePublica = Base64.getDecoder().decode( request.get("keyPublic").asText() );
		
		byte [] llavePrivada = Base64.getDecoder().decode( request.get("keyPrivate").asText() );
		 
		KeyFactory keyFactory;
        
        PublicKey publicKeyServer;
        
        PrivateKey privateKey;
        
        RSAPublicKey rsa;
        
        RSAPrivateKey rsaPrivate;
        
		try {
			
			keyFactory = KeyFactory.getInstance("RSA");
	        
	        publicKeyServer = (PublicKey) keyFactory.generatePublic( new X509EncodedKeySpec( llavePublica ) );
	        
	        privateKey = keyFactory.generatePrivate( new PKCS8EncodedKeySpec( llavePrivada ) );
	        
	        rsaPrivate = (RSAPrivateKey) privateKey;
	        
	        rsa = (RSAPublicKey) publicKeyServer;
	        
	        Cipher rsaCipher = Cipher.getInstance("RSA");
			
			rsaCipher.init( Cipher.ENCRYPT_MODE, privateKey );
			
			byte[] mensajeCifrado = rsaCipher.doFinal( request.get("texto").asText().getBytes("UTF8") );
			
			System.out.println( mensajeCifrado );
			
			rsaCipher.init( Cipher.DECRYPT_MODE, publicKeyServer );
			
			byte[] mensajeDescifrado = rsaCipher.doFinal( mensajeCifrado );
			
			String mensajeDescifrado2 = new String( mensajeDescifrado, "UTF8" );
			
			System.out.println( mensajeDescifrado2 );
			
		} catch (Exception e) {
			
			if ( e instanceof NoSuchAlgorithmException) {
				
				throw new CustomException("Error al Obtener el algoritmo de la llave", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if ( e instanceof InvalidKeySpecException ) {
				
				throw new CustomException("Error en el formato de la llave", HttpStatus.BAD_REQUEST);
			}
			
			throw new CustomException("Error Interno Del Servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		System.out.println( rsaPrivate.getModulus( ) );
		
		System.out.println( rsaPrivate.getPrivateExponent( ) );
		
		System.out.println( rsa.getModulus( ) );
		
		System.out.println( rsa.getPublicExponent( ) );
        
        return Base64.getEncoder().encodeToString( rsa.getModulus( ).toByteArray( ) );
        
	}
	
	public String encriptar( String keyPublic, String valor ) {
		
		if ( keyPublic == null ) {
			
			keyPublic = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK2Jyrgn1dAG//z6tdVHWnRX1cedBoGOYW6tfNEkgKWQqGPYRV2Iq9//PVzOsUPuucAtTDh5Qr0NKTWrXdLPHAsCAwEAAQ==";
		}
		
		String response = "";
		
		byte [] llavePublica = Base64.getDecoder().decode( keyPublic );
		
		KeyFactory keyFactory;
        
        PublicKey publicKeyServer;
        
		try {
			
			keyFactory = KeyFactory.getInstance("RSA");
	        
			publicKeyServer = (PublicKey) keyFactory.generatePublic( new X509EncodedKeySpec( llavePublica ) );
	        
	        Cipher rsaCipher = Cipher.getInstance("RSA");
			
			rsaCipher.init( Cipher.ENCRYPT_MODE, publicKeyServer );
			
			byte[] mensajeCifrado = rsaCipher.doFinal( valor.getBytes( StandardCharsets.UTF_8 ) );
			
			response = Base64.getEncoder().encodeToString( mensajeCifrado );
			
		} catch (Exception e) {
			
			if ( e instanceof NoSuchAlgorithmException) {
			
				throw new CustomException("Error al Obtener el algoritmo de la llave", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if ( e instanceof InvalidKeySpecException ) {
				
				throw new CustomException("Error en el formato de la llave", HttpStatus.BAD_REQUEST);
			}
			
			throw new CustomException("Error Interno Del Servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;
		
	}
	
	public String desEncriptar( String privateKey, String valor ) {
		
		String response = "";
		
		byte [] llavePrivada = Base64.getDecoder().decode( privateKey );
		
		KeyFactory keyFactory;
        
        PrivateKey privateKeyFactory;
        
		try {
			
			keyFactory = KeyFactory.getInstance("RSA");
	        
	        privateKeyFactory = (PrivateKey) keyFactory.generatePrivate( new PKCS8EncodedKeySpec( llavePrivada ) );
	        
	        Cipher rsaCipher = Cipher.getInstance("RSA");
			
			rsaCipher.init( Cipher.DECRYPT_MODE, privateKeyFactory );
			
			byte[] mensajeDescifrado = rsaCipher.doFinal( Base64.getDecoder().decode( valor ) );
			
			response = new String( mensajeDescifrado, StandardCharsets.UTF_8 );
			
		} catch (Exception e) {
			
			if ( e instanceof NoSuchAlgorithmException) {
				
				throw new CustomException("Error al Obtener el algoritmo de la llave", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if ( e instanceof InvalidKeySpecException ) {
				
				throw new CustomException("Error en el formato de la llave", HttpStatus.BAD_REQUEST);
			}
			
			throw new CustomException("Error Interno Del Servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
        return response;
        
	}
	
}
