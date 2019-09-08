package com.horizon.common.exception;


import com.horizon.common.enums.ExceptionEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QxException extends RuntimeException {

    private ExceptionEnums exceptionEnums;


}
