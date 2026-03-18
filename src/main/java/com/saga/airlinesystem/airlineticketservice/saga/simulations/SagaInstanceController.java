package com.saga.airlinesystem.airlineticketservice.saga.simulations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/saga-instance")
@RequiredArgsConstructor
public class SagaInstanceController {

    private final SagaInstanceService sagaInstanceService;

    @GetMapping("/{id}")
    public ResponseEntity<SagaInstanceDto> getByTicketOrderId(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(sagaInstanceService.getByTicketOrder(id));
    }
}
