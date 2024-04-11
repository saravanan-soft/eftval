package com.example.demo.valiTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import com.example.demo.Dao.EFTMainDao;
import com.example.demo.XmlFileClass.EFTMain;
import com.example.demo.XmlFileClass.EFTMainId;
import com.example.demo.XmlFileClass.MessageInfo;
import com.example.demo.XmlFileClass.Receiver;
import com.example.demo.XmlFileClass.Request;
import com.example.demo.XmlFileClass.Sender;
import com.example.demo.XmlFileClass.TransactionInfo;
import com.example.demo.XmlFileClass.TransactionReference;
import com.example.demo.service.MessageValidator;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;






@ExtendWith(MockitoExtension.class)
public class MessageValidatorTest {
	
	@InjectMocks
	private MessageValidator msgVal;
	
	// @Mock
	  //  private CircuitBreaker circuitBreaker;

	  //  @Mock
	   // private CircuitBreakerRegistry circuitBreakerRegistry;

	
	@Mock
	private EFTMainDao eftMainDao;
	
	private EFTMain eftMain;
	
	private EFTMainId eftMainId;
	
	private Request req;
	private MessageInfo msgInfo;
	private Sender sender;
	private Receiver recv;
	private TransactionReference tranRef;
	private TransactionInfo tranInf;
	
	 @BeforeEach
	    public void setup(){
		 eftMainId=EFTMainId.builder().msgDate(LocalDate.of(2024, 3, 13)).daySl(1).build();
		 eftMain=EFTMain.builder().eftmainid(eftMainId).errRsn(null).errCode(null).referenceNum("sfffggdg").postDate(LocalDate.of(2024, 3, 13)).batchNum("1").in_Time(LocalDateTime.now()).build();
		 msgInfo=MessageInfo.builder().MsgInfoId(1).messageVersion(2).build();
		 sender=Sender.builder().accountNumber("123123").name("customer").email("customer@BBS.com").mobileNumber("2482573064").bankCode("1").branchCode("01").customerId("CUSTOM_customerbbs").build();
		 recv=Receiver.builder().accountNumber("112233").name("customer").bankCode("6").branchCode("01").build();
		 tranRef=TransactionReference.builder().senderReference("ref1").systemReference("eaaebd00-adb4-4207-b5a0-c9d4130edea6").source("BU").build();
		 //tranInf=TransactionInfo.builder().amount(2.0).currency("SCR").valueDate(LocalDateTime.of(2012,9,13,0,0)).initiatedBy("maker").initiatedOn("2012-09-13T08:43:32.593").checkedBy("maker").checkedOn("2012-09-13T08:43:49.716").build();
		 req=Request.builder().ReqId(1).fileName("test.xml").in_time(LocalDateTime.now()).messageInfo(msgInfo).sender(sender).receiver(recv).transactionReference(tranRef).transactionInfo(tranInf).build();
		// circuitBreakerRegistry
        // .circuitBreaker("transervice")
        /// .getEventPublisher()
        // .onError(throwable -> {
        //     System.out.println("Circuit breaker state transition: " + throwable);
        // });
	 
	 }
	 
	 @DisplayName("JUnit test for duplicate check") 
	 //@Test
		public void dupCheck() throws Exception
		{
		 String parRef="fdfjfklsjf";
		 when(eftMainDao.cntRef(parRef)).thenReturn(2);
			boolean res=msgVal.dupCheck.test(parRef);
			assertTrue(res);
		}
	 
	 @DisplayName("JUnit test for save check") 
	// @Test
		public void saveCheck() throws Exception
		{
		    Request msg=req;
		    LocalDate date=LocalDate.of(2024, 3, 8);
		    EFTMainId eftMainIdPar=EFTMainId.builder().msgDate(date).daySl(1).build();
			EFTMain eftMainPa=EFTMain.builder().eftmainid(eftMainIdPar).in_Time(LocalDateTime.of(2024, 3, 8, 11, 20)).errCode(null).errRsn(null).referenceNum("").referenceNum(msg.getTransactionReference().getSystemReference()).build();
			
		    when(eftMainDao.maxDaySl(date)).thenReturn(0);
		    when(eftMainDao.save(eftMainPa)).thenReturn(eftMain);
		    
		    EFTMain res=msgVal.save(msg, date);
		    System.out.println(res);
		    assertEquals(res.getBatchNum(),"1");
		}
	 
	 @DisplayName("JUnit test for getCBD check") 
		// @Test
			public void getCBD() throws Exception
			{
			    LocalDate date=LocalDate.of(2024, 3, 8);
			    when(eftMainDao.getCbd()).thenReturn(date);
			    LocalDate dateres= msgVal.getCBD.get();
			    
			    assertEquals(dateres,date);
			}
	 
	 @DisplayName("JUnit test for getCBD check") 
	// @Test
		public void getInstallBank() throws Exception
		{
		 String insBank="1";
			when(eftMainDao.installBank()).thenReturn(insBank);
			String resBank= msgVal.getInstallBank.get();
		    
		    assertEquals(resBank,insBank);
		}
	 
	 @DisplayName("JUnit test for constructPostTran check") 
	// @Test
			public void constructPostTran() throws Exception
			{
			    
			    int batch= msgVal.constructPostTran(sender, recv, tranInf);
			    
			    assertNotEquals(batch,0);
			}
	 
	 
	@DisplayName("JUnit test for deposit check") 
	//@Test
	public void deposit() throws Exception
	{
			     
		String insBank="1";
		when(eftMainDao.installBank()).thenReturn(insBank);
		boolean result= msgVal.validateDebosit(sender,tranInf);
	    assertTrue(result);
	}

	@DisplayName("JUnit test for credit check") 
//@Test
	public void credit() throws Exception
	{
			     
		String insBank="1";
		when(eftMainDao.installBank()).thenReturn(insBank);
		boolean result= msgVal.validateCredit(recv,tranInf);
	    assertTrue(result);
	}
	
	
	@DisplayName("JUnit test for eft api check positive") 
	@Test
		public void getEftCode() throws Exception
		{
		//MockitoJUnitRunner.class
		 // MockitoAnnotations.openMocks(circuitBreaker);
	     // when(circuitBreakerRegistry.circuitBreaker("myService")).thenReturn(circuitBreaker);
	     // when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.OPEN);   
			//String bankCode="1";
			//String result= msgVal.getEft(bankCode);
		    //assertEquals(result,"CBS");
		}
	
}
