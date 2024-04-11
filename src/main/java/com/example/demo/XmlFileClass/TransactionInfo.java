package com.example.demo.XmlFileClass;

import java.time.LocalDate;
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
public class TransactionInfo  {
    
	private int TranInfoId;
    private double amount;
    private String currency;
    private LocalDate valueDate;
    private String initiatedBy;
    private String initiatedOn;
    private String checkedBy;
    private String checkedOn;
  
	public String toString() {
		return "TransactionInfo [amount=" + amount + ", currency=" + currency + ", valueDate=" + valueDate
				+ ", initiatedBy=" + initiatedBy + ", initiatedOn=" + initiatedOn + ", checkedBy=" + checkedBy
				+ ", checkedOn=" + checkedOn + "]";
	}
    
	
    
}