package com.saga.airlinesystem.airlineticketservice.saga.simulations;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaInstance;

import com.saga.airlinesystem.airlineticketservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaInstanceService {

    private final SagaInstanceRepository sagaInstanceRepository;

    public SagaInstanceDto getByTicketOrder(UUID ticketOrderId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByAggregateId(ticketOrderId).orElseThrow(
                () -> new ResourceNotFoundException("SagaInstance not found")
        );
        SagaInstanceDto sagaInstanceDto = new SagaInstanceDto();
        sagaInstanceDto.setId(sagaInstance.getId());
        sagaInstanceDto.setState(sagaInstance.getState());
        sagaInstanceDto.setAggregateId(sagaInstance.getAggregateId());
        sagaInstanceDto.setUpdatedAt(sagaInstanceDto.getUpdatedAt());
        return sagaInstanceDto;
    }

}
