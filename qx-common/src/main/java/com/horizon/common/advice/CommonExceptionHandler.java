package com.horizon.common.advice;

import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(QxException.class)
    public ResponseEntity<ExceptionResult> handleException(QxException e){
        ExceptionEnums exceptionEnums = e.getExceptionEnums();
        return ResponseEntity.status(exceptionEnums.getCode()).body(new ExceptionResult(e.getExceptionEnums()));
    }

}
