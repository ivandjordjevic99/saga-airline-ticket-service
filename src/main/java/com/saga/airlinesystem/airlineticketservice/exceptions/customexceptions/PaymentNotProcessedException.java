package com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions;

public class PaymentNotProcessedException extends AbstractException {

    public PaymentNotProcessedException(String message) {
        super(400, message);
    }

    public PaymentNotProcessedException() {
        super(400, "Payment not processed");
    }
}
