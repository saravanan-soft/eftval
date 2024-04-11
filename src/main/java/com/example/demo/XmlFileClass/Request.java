package com.example.demo.XmlFileClass;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
	private int ReqId;
	private String fileName;
	private LocalDateTime in_time;
	private MessageInfo messageInfo;
    private Sender sender;
    private Receiver receiver;
    private TransactionReference transactionReference;
    private TransactionInfo transactionInfo;
}