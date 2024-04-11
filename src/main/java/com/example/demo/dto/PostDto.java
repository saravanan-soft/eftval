package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PostDto {
	
	private Integer tran_brn_code;
	private LocalDate tran_date_of_tran;
	private Character tranOption;
	private String tranCode;
	private LocalDate tranValueDate;
	private String tranAcnum;
	private Character tranDbCrFlg;
	private String tranCurrCode;
	private Double tranAmount;
	private String tranNarrDtl1;
	private String tranNarrDtl2;
	private String tranNarrDtl3;
	private String tranEntdBY;

}
