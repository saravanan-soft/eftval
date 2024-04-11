package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.Dao.InstallDao;
import com.example.demo.Dao.MaincontDao;
import com.example.demo.XmlFileClass.EFTMain;
import com.example.demo.XmlFileClass.EFTMainId;
import com.example.demo.XmlFileClass.Install;
import com.example.demo.XmlFileClass.Maincont;
import com.example.demo.XmlFileClass.MessageInfo;
import com.example.demo.XmlFileClass.Receiver;
import com.example.demo.XmlFileClass.Request;
import com.example.demo.XmlFileClass.Sender;
import com.example.demo.XmlFileClass.TransactionInfo;
import com.example.demo.XmlFileClass.TransactionReference;
import com.example.demo.service.MessageConsumer;

//@EnableEurekaClient
@SpringBootApplication
//@EnableCircuitBreaker
//@EnableHystrixDashboard
public class QcbsTransactionApplication  implements CommandLineRunner  {

	
	
	public static void main(String[] args) {
		SpringApplication.run(QcbsTransactionApplication.class, args);
	}
	


	@Override
	public void run(String... args) throws Exception {
		
		//c.listen(req);
		
	}

}
