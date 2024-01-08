package com.elliott.tworoomsandaboom.controller;

import com.elliott.tworoomsandaboom.error.DatabaseException;
import com.elliott.tworoomsandaboom.error.GameRuleException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class TwoRoomsAndABoomControllerExceptionHandler
{
    @ExceptionHandler({
            DatabaseException.class,
            GameRuleException.class
    })
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> onDatabaseException(final RuntimeException ex)
    {
        log.info(ex.getMessage());
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
