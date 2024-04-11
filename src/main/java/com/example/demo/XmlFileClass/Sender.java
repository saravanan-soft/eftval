package com.example.demo.XmlFileClass;


import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sender {
    
	private int SendId;
    private String accountNumber;
    private String name;
    private String email;
    private String mobileNumber;
    private String bankCode;
    private String branchCode;
    private String customerId;
    
	@Override
	public String toString() {
		return "Sender [accountNumber=" + accountNumber + ", name=" + name + ", email=" + email + ", mobileNumber="
				+ mobileNumber + ", bankCode=" + bankCode + ", branchCode=" + branchCode + ", customerId=" + customerId
				+ "]";
	}
}