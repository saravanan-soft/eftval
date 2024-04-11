package com.example.demo.XmlFileClass;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Maincont {
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 private Integer id;
	 private LocalDate mnCurrBusinessDate;
	 private LocalDate mnPrevBusinessDate;
	 private String mnSodDoneBy;
	 private LocalDate mnSodDateTime;
	 private Integer mnSodInProgress;
	 private Integer mnSodOverFlag;
	 private String mnEodBy;
	 private LocalDate mnEodDateTime;
	 private Integer mnEodOverFlag;
	 private Integer mnEodInProgress;
	 private Integer mnMeSodInProgress;
	 private Integer mnMeEodInProgress;
	 private Integer mnMeEodOverFlag;
	 private String mnMeSodDoneBy;
	 private Integer mnMeSodOverFlag;
	 private Integer mnBefEodBackupOver;
	 private Integer mnAfterEodBackupOver;
	 private Integer mnPreEodInProgress;
	 private Integer mnPreEodOverFlag;
	 private String mnDailyMessage;
	 

}
