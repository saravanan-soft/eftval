package com.example.demo.XmlFileClass;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EFTMain {
	
	@EmbeddedId
	private EFTMainId eftmainid;
	private String errRsn;
	private String errCode;
	private String referenceNum;
	private LocalDate postDate;
	private String batchNum;
	private LocalDateTime in_Time;

}
