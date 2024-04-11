package com.example.demo.XmlFileClass;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receiver {
    
	
	private int RecId;
    private String accountNumber;    
    private String name;
    private String bankCode;
    private String branchCode;
	@Override
	public String toString() {
		return "Receiver [accountNumber=" + accountNumber + ", name=" + name + ", bankCode=" + bankCode
				+ ", branchCode=" + branchCode + "]";
	}
}