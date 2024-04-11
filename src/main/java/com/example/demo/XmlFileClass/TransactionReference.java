package com.example.demo.XmlFileClass;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionReference {
	
	private int TranRefId;
    private String senderReference;
    private String systemReference;
    private String source;
    
	@Override
	public String toString() {
		return "TransactionReference [senderReference=" + senderReference + ", systemReference=" + systemReference
				+ ", source=" + source + "]";
	}
}