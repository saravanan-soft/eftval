package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.demo.XmlFileClass.Request;

@Service
public class MessageConsumer {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(MessageConsumer.class);
	
	@Autowired
	MessageValidator msgValidator;
	
	@KafkaListener(topics = "test", groupId = "myGroup")
    public void listen(Request message) {
		LOGGER.info("Received message: " + message);
		msgValidator.validateMessage(message);
    }
}
