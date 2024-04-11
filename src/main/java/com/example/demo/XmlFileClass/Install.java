package com.example.demo.XmlFileClass;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Install {
	 
	  @Id
	  @GeneratedValue(strategy=GenerationType.IDENTITY)
	  private Integer id;
	  private String insNameOfBank; 
	  private String  insNameOfOffice;
	  private String insAddress1;
	  private LocalDate insInstallDate;
	  private LocalDate insLastUpgradeDate;
	  private String insSwMajorVersion;
	  private String insSwMinorVersion;
	  private String insOurBankCode;
	  private Integer insOurBrnCode;
	  private String insBaseCurrCode;
	  private String insOurCntryCode;
	  private String insDefLangCode;
	  private Integer insFinYearStartMonth;
	  private String insBosSystemCode;
	  private Integer insStartingDayOfWeek;
	  private Character insSystemStatus;
	  private String insEntdBy;
	  private LocalDate insEntdOn;
	  private String insLastModBy;
	  private LocalDate insLastModOn;
	  private String insReportFilePath;
	  private Character insTypeOfBank;
	  private String insScbBankCode;
	  
}
