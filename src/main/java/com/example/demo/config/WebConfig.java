package com.example.demo.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.XmlFileClass.EFTMain;
import com.example.demo.XmlFileClass.EFTMainId;
import com.example.demo.XmlFileClass.MessageInfo;
import com.example.demo.XmlFileClass.Receiver;
import com.example.demo.XmlFileClass.Request;
import com.example.demo.XmlFileClass.Sender;
import com.example.demo.XmlFileClass.TransactionInfo;
import com.example.demo.XmlFileClass.TransactionReference;
import com.example.demo.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebConfig {
	@Autowired
	private ReactorLoadBalancerExchangeFilterFunction lbFunction;
	 
	
	
	@Bean
	public ExecutorService getExecute() {
		return Executors.newFixedThreadPool(10);
	}

	/*@Bean
	@LoadBalanced
    public WebClient webClient(){
		return WebClient.builder().baseUrl("http://QCBSACCOUNT").filter(errorHandler()).build();
    }*/
	@Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().filter(lbFunction).filter(errorHandler());
		//return WebClient.builder().filter(errorHandler());
    }
	
	/*@Bean
	@LoadBalanced
	public WebClient.Builder lbWebClient() {
	    return WebClient.builder();
	}

	@Bean
	@Primary
	public WebClient.Builder webClient() {
	    return WebClient.builder();
	}*/

	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
    }
	
	public static ExchangeFilterFunction errorHandler() {
	    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
	        if (clientResponse.statusCode().is5xxServerError()) {
	            return clientResponse.bodyToMono(String.class)
	                    .flatMap(errorBody -> Mono.error(new ValidationException("Post Error5",errorBody,"000")));
	        } else if (clientResponse.statusCode().is4xxClientError()) {
	            return clientResponse.bodyToMono(String.class)
	                    .flatMap(errorBody -> Mono.error(new ValidationException("Post Error4",errorBody,"000")));
	        } else {
	            return Mono.just(clientResponse);
	        }
	    });
	}
	
	
	
}
