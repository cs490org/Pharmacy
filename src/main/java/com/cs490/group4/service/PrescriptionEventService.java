package com.cs490.group4.service;

import com.cs490.group4.config.RabbitMQConfig;
import com.cs490.group4.dao.Prescription;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionEventService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendPrescriptionFulfilledEvent(Prescription prescription) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            prescription
        );
    }
} 