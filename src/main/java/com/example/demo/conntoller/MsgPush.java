package com.example.demo.conntoller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.XmlFileClass.EFTMain;
import com.example.demo.XmlFileClass.EFTMainId;
import com.example.demo.XmlFileClass.MessageInfo;
import com.example.demo.XmlFileClass.Receiver;
import com.example.demo.XmlFileClass.Request;
import com.example.demo.XmlFileClass.Sender;
import com.example.demo.XmlFileClass.TransactionInfo;
import com.example.demo.XmlFileClass.TransactionReference;
import com.example.demo.service.MessageConsumer;
import com.example.demo.service.MessageValidator;


import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class MsgPush {
	//private static final Logger msgPushLogger = LoggerFactory.getLogger(MsgPush.class);

	@Autowired
	MessageValidator msgValidator;
	
	
	
	@PostMapping("/start")
	public ResponseEntity<?> msgPush(){
		//msgPushLogger.info("Step 1");
		log.info("test1");
		EFTMainId eftMainId=EFTMainId.builder().msgDate(LocalDate.of(2024, 3, 13)).daySl(1).build();
		EFTMain eftMain=EFTMain.builder().eftmainid(eftMainId).errRsn(null).errCode(null).referenceNum("sfffggdg").postDate(LocalDate.of(2024, 3, 13)).batchNum("1").in_Time(LocalDateTime.now()).build();
		MessageInfo msgInfo=MessageInfo.builder().MsgInfoId(1).messageVersion(2).build();
		Sender sender=Sender.builder().accountNumber("123123").name("customer").email("customer@BBS.com").mobileNumber("2482573064").bankCode("1").branchCode("01").customerId("CUSTOM_customerbbs").build();
		Receiver recv=Receiver.builder().accountNumber("112233").name("customer").bankCode("6").branchCode("01").build();
		TransactionReference tranRef=TransactionReference.builder().senderReference("ref1").systemReference("eaaebd00-adb4-4207-b5a0-c9d4130edea6").source("BU").build();
		TransactionInfo tranInf=TransactionInfo.builder().amount(2.0).currency("SCR").valueDate(null).initiatedBy("maker").initiatedOn("2012-09-13T08:43:32.593").checkedBy("maker").checkedOn("2012-09-13T08:43:49.716").build();
		Request req=Request.builder().ReqId(1).fileName("test.xml").in_time(LocalDateTime.now()).messageInfo(msgInfo).sender(sender).receiver(recv).transactionReference(tranRef).transactionInfo(tranInf).build();
		msgValidator.validateMessage(req);
		return new ResponseEntity<>("started",HttpStatus.ACCEPTED);
	}

}
