package com.prueba.rest.Service;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.prueba.rest.Exception.CustomException;

@Service
public class EncriptarAES {
 
    public String encriptar( String datos, String claveSecreta ) /*throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException*/ {
        
    	String encriptado = "";
    	
    	try {
    		
    		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            SecretKeySpec secretKeySpec = new SecretKeySpec( Base64.getDecoder().decode( claveSecreta.getBytes() ), "AES");
            
            cipher.init( Cipher.ENCRYPT_MODE, secretKeySpec );
            
            byte[] bytesEncriptados = cipher.doFinal( datos.getBytes() );
            
            encriptado = Base64.getEncoder().encodeToString( bytesEncriptados );
			
		} catch (Exception e) {

			throw new CustomException("Error Interno Del Servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
        return encriptado;
        
    }
 
    public String desencriptar( String datosEncriptados, String claveSecreta ) /*throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException*/ {
 
    	String datos = "";
    	
    	try {
    		
    		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            SecretKeySpec secretKeySpec = new SecretKeySpec( Base64.getDecoder().decode( claveSecreta.getBytes() ), "AES");
            
            cipher.init( Cipher.DECRYPT_MODE, secretKeySpec );
            
            byte[] datosDesencriptados = cipher.doFinal( datosEncriptados.getBytes() );
            
            datos = new String( datosDesencriptados );
			
		} catch (Exception e) {
			
			throw new CustomException("Error Interno Del Servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
         
        return datos;
        
    }
    
}
