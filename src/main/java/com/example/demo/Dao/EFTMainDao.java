package com.example.demo.Dao;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.XmlFileClass.EFTMain;
import com.example.demo.XmlFileClass.EFTMainId;

public interface EFTMainDao extends JpaRepository<EFTMain,EFTMainId>{
	
	@Query(value="select COALESCE(max(day_sl), 0) from eftmain where msg_date=:date",nativeQuery=true)
	public Integer maxDaySl(@Param("date") LocalDate date);
	
	@Query(value="select ins_our_bank_code from install",nativeQuery=true)
	public String installBank();

	
	@Query(value="select COALESCE(count(day_sl), 0) from eftmain where reference_num=:ref and err_rsn is null",nativeQuery=true)
	public Integer cntRef(@Param("ref") String ref);
	
	@Query(value="select mn_curr_business_date from maincont",nativeQuery=true)
	public LocalDate getCbd();
	
}
