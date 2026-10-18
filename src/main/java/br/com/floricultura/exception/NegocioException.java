package br.com.floricultura.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NegocioException extends Exception {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}