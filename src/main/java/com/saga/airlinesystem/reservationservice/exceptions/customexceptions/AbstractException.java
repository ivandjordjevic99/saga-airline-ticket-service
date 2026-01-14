package com.saga.airlinesystem.reservationservice.exceptions.customexceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AbstractException extends RuntimeException {

    protected int status;
    protected String message;

}
