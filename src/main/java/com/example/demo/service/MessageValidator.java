package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.catalina.connector.Response;
import org.apache.coyote.BadRequestException;
//import org.apache.kafka.common.network.Receive;
//import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.Dao.EFTMainDao;
import com.example.demo.XmlFileClass.EFTMain;
import com.example.demo.XmlFileClass.EFTMainId;
import com.example.demo.XmlFileClass.Receiver;
import com.example.demo.XmlFileClass.Request;
import com.example.demo.XmlFileClass.Sender;
import com.example.demo.XmlFileClass.TransactionInfo;
import com.example.demo.conntoller.MsgPush;
import com.example.demo.dto.PostDto;
import com.example.demo.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@Component
public class MessageValidator {
	
	

	
	private String errMsg;
	private String errCode;
	private String debitAcc;
	private String credAcc;
	private EFTMainDao mainDao;
	//private WebClient webClient;
	WebClient.Builder webClientBuilder;

	private ObjectMapper mapper;
	private final ExecutorService executorService;
	
	private static final Logger msgValLogger = LoggerFactory.getLogger(MessageValidator.class);
	
	
	public MessageValidator(EFTMainDao mainDao,WebClient.Builder webClientBuilder,ObjectMapper mapper,ExecutorService executorService) {
		this.mainDao=mainDao;
		this.webClientBuilder=webClientBuilder;
		//this.webClient = webClientBuilder.baseUrl("http://QCBSACCOUNT").filter(errorHandler()).build();
		this.mapper=mapper;
		this.executorService=executorService;
	}
	
	public void validateMessage(Request msg) {
		try {
			msgValLogger.info("Step 2");
			Optional<Request> reqOpt=Optional.ofNullable(msg);
			EFTMain eftMain=null;
			LocalDate W_cbd;
			try {
				W_cbd=getCBD.get();
				msgValLogger.info("Step 3");
				if(reqOpt.isPresent()) {
					eftMain =this.save(msg,W_cbd);
					boolean result=false;
					result=dupCheck.test(msg.getTransactionReference().getSystemReference());
					if(result) {
						result=this.validateDebosit(msg.getSender(),msg.getTransactionInfo());
						if(result) {
							result=this.validateCredit(msg.getReceiver(),msg.getTransactionInfo());
						    if(result) {
						    	int batchNum=constructPostTran(msg.getSender(),msg.getReceiver(),msg.getTransactionInfo());
						    	if(eftMain !=null) {
								
									EFTMain eftmainnew=eftMain.builder().eftmainid(eftMain.getEftmainid()).referenceNum(eftMain.getReferenceNum()).in_Time(eftMain.getIn_Time()).batchNum(Integer.toString(batchNum)).postDate(W_cbd).build();
									this.mainDao.save(eftmainnew);
								} 
						    }
						}
					}
				}
			}
			catch(ValidationException ve) {
				if(eftMain !=null) {
					String errRsn=ve.getErrRsn();
					String errCode=ve.getErrCode();
					EFTMain eftmainnew=eftMain.builder().eftmainid(eftMain.getEftmainid()).errCode(errCode).errRsn(errRsn).referenceNum(eftMain.getReferenceNum()).in_Time(eftMain.getIn_Time()).build();
					this.mainDao.save(eftmainnew);
				}
			}
		}
		
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
public EFTMain save(Request msg,LocalDate date) throws Exception {
	Integer daySl=this.mainDao.maxDaySl(date)+1;
	EFTMainId eftMainId=EFTMainId.builder().msgDate(date).daySl(daySl).build();
	EFTMain eftMain=EFTMain.builder().eftmainid(eftMainId).in_Time(LocalDateTime.of(2024, 3, 8, 11, 20)).errCode(null).errRsn(null).referenceNum(msg.getTransactionReference().getSystemReference()).build();
	return this.mainDao.save(eftMain);
}

public Predicate<String> dupCheck=(String reference)->{
	int cnt =this.mainDao.cntRef(reference);
	/*if(cnt>2) {
		throw new ValidationException("Reference already processed","Duplicate","001");
	}*/
	return true;
};

public Supplier<LocalDate> getCBD=()->{
	LocalDate date=this.mainDao.getCbd();
	return date;
};


public Supplier<String> getInstallBank=()->{
	String ibank=this.mainDao.installBank();
	return ibank;
};





@CircuitBreaker(name = "product", fallbackMethod = "fallbackMethod")
public String getEft(String bankCode) {
	

   
	
	//webClient=WebClient.builder().build();
	//String mbk_core =webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getEftMapBank?eftCode="+bankCode).retrieve().bodyToMono(String.class).block();
	String mbk_core =webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getEftMapBank?eftCode="+bankCode).retrieve().bodyToMono(String.class).block();
	return mbk_core;
}

public String fallbackMethod(Exception t) {
    return null; // Provide your fallback response here
}

public boolean validateDebosit(Sender sender,TransactionInfo info) {
	//webClient=WebClient.builder().filter(errorHandler()).build();
	 // ObjectMapper mapper=new ObjectMapper();
	msgValLogger.info("Step 4");
	String bankCode=sender.getBankCode();
	String w_install_bank=getInstallBank.get();
	LocalDate acntClsDate=null;
	Boolean dbFreeze=false;
	String currCode=info.getCurrency();
	Boolean dormatAcnt=false;
	String mbk_core =this.getEft(bankCode);
	try {
		//String mbk_core=webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getEftMapBank?eftCode="+bankCode).retrieve().bodyToMono(String.class).block();
		//String mbk_core =webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getEftMapBank?eftCode="+bankCode).retrieve().bodyToMono(String.class).block();
		//String mbk_core =this.getEft(bankCode);
		
		if(mbk_core==null || mbk_core.equals(null)) {
			throw new ValidationException("Sender Bank Does Not Exist","Invalid Sender Bank Coe","002");
		}
		//if(webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getBankCode?bankCode="+mbk_core).retrieve().bodyToMono(Integer.class).block()==0) {
		if(webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getBankCode?bankCode="+mbk_core).retrieve().bodyToMono(Integer.class).block()==0) {	
		throw new ValidationException("Sender Bank Does Not Exist","Invalid Sender Bank Coe","003");
		}
		if(w_install_bank.equals(mbk_core)) {
			//webClientBuilder.build()
			//Object[][] objAry=webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getAcntDetails?acNum="+sender.getAccountNumber()).retrieve().bodyToMono(Object[][].class).block();
			Object[][] objAry=webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getAcntDetails?acNum="+sender.getAccountNumber()).retrieve().bodyToMono(Object[][].class).block();
			acntClsDate=(LocalDate) objAry[0][0];
			dbFreeze=(Boolean) objAry[0][1];
			currCode=(String) objAry[0][2];
			dormatAcnt=(Boolean) objAry[0][3];
		}
		else {
			acntClsDate=null;
			dbFreeze=false;
			currCode="SCR";
			dormatAcnt=false;
		}
		if(acntClsDate !=null) {
			throw new ValidationException("Account is closed",sender.getAccountNumber()+" account is closed","004");
		}
		if(dbFreeze) {
			throw new ValidationException("debit is freezed",sender.getAccountNumber()+" account debit is freezed","005");
		}
		if(dormatAcnt) {
			throw new ValidationException("dormant account",sender.getAccountNumber()+" account is dormant","006");
		}
		
		if(!w_install_bank.equals(mbk_core)) {
			//mbk_core="BOB";
			//webClientBuilder.build()
			//Object[][] acc=webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getExtAcnum?bankCode="+mbk_core+"&currCode="+currCode).retrieve().bodyToMono(Object[][].class).block();
			Object[][] acc=webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getExtAcnum?bankCode="+mbk_core+"&currCode="+currCode).retrieve().bodyToMono(Object[][].class).block();
			String tranAcnt=(String) acc[0][0];
			// [[2103,2103]]
			if(tranAcnt == null || tranAcnt=="0") {
				throw new ValidationException("Debit account not maped",sender.getAccountNumber()+" Debit account not maped","007");
			}
			debitAcc=tranAcnt;
		}
		else {
			debitAcc=sender.getAccountNumber();
		}
		//webClientBuilder.build()
		double balance=webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acntbal/chkbal?acnum="+debitAcc+"&curr="+currCode).retrieve().bodyToMono(Double.class).block();
		//double balance=webClient.get().uri("http://QCBSACCOUNT/api/v1/acntbal/chkbal?acnum="+debitAcc+"&curr="+currCode).retrieve().bodyToMono(Double.class).block();
		if(balance<info.getAmount()) {
			throw new ValidationException("insufficent fund",sender.getAccountNumber()+"insufficent fund","008");
		}
	}
	catch(ValidationException ex) {
		throw new ValidationException("Error in debosit",ex.getErrRsn(),ex.getErrCode());
	}
	catch(Exception ex) {
		throw new ValidationException("Error in debosit",ex.getMessage().substring(0,ex.getMessage().length()-1),"000");
	}
	
	
	return true;
}
	
public boolean validateCredit(Receiver recv,TransactionInfo info) {
	
		 // ObjectMapper mapper=new ObjectMapper();
		
		String bankCode=recv.getBankCode();
		//bankCode="11";
		String w_install_bank=getInstallBank.get();
		LocalDate acntClsDate=null;
		Boolean dbFreeze=false;
		String currCode=info.getCurrency();
		Boolean dormatAcnt=false;
		try {
			//String mbk_core=webClient.get().uri("http://QCBSACCOUNT/api/v1/acnt/getEftMapBank?eftCode="+bankCode).retrieve().bodyToMono(String.class).block();
			String mbk_core =webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getEftMapBank?eftCode="+bankCode).retrieve().bodyToMono(String.class).block();
			if(mbk_core==null || mbk_core.equals(null)) {
				throw new ValidationException("Sender Bank Does Not Exist","Invalid Sender Bank Coe","002");
			}
			if(webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getBankCode?bankCode="+mbk_core).retrieve().bodyToMono(Integer.class).block()==0) {
				throw new ValidationException("Sender Bank Does Not Exist","Invalid Sender Bank Coe","003");
			}
			if(w_install_bank==mbk_core) {
				Object[][] objAry=webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getAcntDetails?acNum="+recv.getAccountNumber()).retrieve().bodyToMono(Object[][].class).block();
				acntClsDate=(LocalDate) objAry[0][0];
				dbFreeze=(Boolean) objAry[0][1];
				currCode=(String) objAry[0][2];
				dormatAcnt=(Boolean) objAry[0][3];
			}
			else {
				acntClsDate=null;
				dbFreeze=false;
				currCode="SCR";
				dormatAcnt=false;
			}
			if(acntClsDate !=null) {
				throw new ValidationException("Account is closed",recv.getAccountNumber()+" account is closed","004");
			}
			if(dbFreeze) {
				throw new ValidationException("Credit is freezed",recv.getAccountNumber()+" account debit is freezed","005");
			}
			if(dormatAcnt) {
				throw new ValidationException("dormant account",recv.getAccountNumber()+" account is dormant","006");
			}
			
			if(w_install_bank !=mbk_core) {
				//mbk_core="BOB";
				Object[][] acc=webClientBuilder.build().get().uri("http://QCBSACCOUNT/api/v1/acnt/getExtAcnum?bankCode="+mbk_core+"&currCode="+currCode).retrieve().bodyToMono(Object[][].class).block();
				String tranAcnt=(String) acc[0][1];
				// [[2103,2103]]
				if(tranAcnt == null || tranAcnt=="0") {
					throw new ValidationException("Credit account not maped",recv.getAccountNumber()+" Debit account not maped","007");
				}
				credAcc=tranAcnt;
			}
			else {
				credAcc=recv.getAccountNumber();
			}
			//double balance=webClient.get().uri("http://QCBSACCOUNT/api/v1/acntbal/chkbal?acnum="+debitAcc+"&curr="+currCode).retrieve().bodyToMono(Double.class).block();
			
		}
		catch(ValidationException ex) {
			throw new ValidationException("Error in credit",ex.getErrRsn(),ex.getErrCode());
		}
		catch(Exception ex) {
			throw new ValidationException("Error in Credit",ex.getMessage().substring(0,ex.getMessage().length()-1),"000");
		}
		
		
		return true;
	}
  
   public int constructPostTran(Sender sender,Receiver recv,TransactionInfo info) {
	  //webClient=WebClient.builder().filter(errorHandler()).build();
	 // ObjectMapper mapper=new ObjectMapper();
	   Integer batchNum=0;
	   try {
		   List<PostDto> postDto=new ArrayList<PostDto>();
		 postDto.add(PostDto.builder().tran_brn_code(Integer.parseInt(sender.getBranchCode())).tran_date_of_tran(LocalDate.of(2024, 3, 8)).tranOption('E').tranCode("D").tranValueDate(info.getValueDate()).tranAcnum(debitAcc).tranDbCrFlg('D').tranCurrCode(info.getCurrency()).tranAmount(info.getAmount()).tranNarrDtl1("EFT payments").tranEntdBY("EFT").build());
		 postDto.add(PostDto.builder().tran_brn_code(Integer.parseInt(sender.getBranchCode())).tran_date_of_tran(LocalDate.of(2024, 3, 8)).tranOption('E').tranCode("C").tranValueDate(info.getValueDate()).tranAcnum(credAcc).tranDbCrFlg('C').tranCurrCode(info.getCurrency()).tranAmount(info.getAmount()).tranNarrDtl1("EFT payments").tranEntdBY("EFT").build());
		   try {
	        	//mapper.registerModule(new JavaTimeModule()).writeValueAsString(acnt);
	        mapper.registerModule(new JavaTimeModule()).writeValueAsString(postDto);
	        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
	        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(postDto);
	            System.out.println(jsonString);
	           batchNum=webClientBuilder.build().post().uri("http://QCBSACCOUNT/api/v1/tran/create").contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(jsonString)).retrieve().bodyToMono(Integer.class).block();
		   } catch (Exception e) {
	            e.printStackTrace();
	        }        
	   }
	   catch(Exception e) {
		   throw new ValidationException("Post error",e.getMessage(),"000");
	   }
	  return batchNum;
   }
   
   
   public static ExchangeFilterFunction errorHandler() {
	    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
	        if (clientResponse.statusCode().is5xxServerError()) {
	            return clientResponse.bodyToMono(String.class)
	                    .flatMap(errorBody -> Mono.error(new ValidationException("Post error1",errorBody,"000")));
	        } else if (clientResponse.statusCode().is4xxClientError()) {
	        	System.out.println("400");
	            return clientResponse.bodyToMono(String.class)
	                    .flatMap(errorBody -> Mono.error(new ValidationException("Post error2",errorBody,"000")));
	        } else {
	        	System.out.println("res"+clientResponse);
	            return Mono.just(clientResponse);
	        }
	    });
	}

}
