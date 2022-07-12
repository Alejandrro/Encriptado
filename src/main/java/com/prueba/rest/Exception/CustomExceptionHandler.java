package com.prueba.rest.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.prueba.rest.Utils.CustomResponse;

@RestControllerAdvice
public class CustomExceptionHandler {

	private CustomResponse customResponse = new CustomResponse( );

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse> handleCustomException( CustomException ex ) {

        customResponse.setStatus( ex.getHttpStatus().value( ) );
        customResponse.setMsg( ex.getMessage( ) );

        return new ResponseEntity<CustomResponse>( customResponse, HttpStatus.valueOf( customResponse.getStatus( ) ) );
    }
}
