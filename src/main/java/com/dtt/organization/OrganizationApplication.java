package com.dtt.organization;


import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import org.apache.hc.core5.ssl.TrustStrategy;
//import org.jasypt.encryption.StringEncryptor;
//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import ug.daes.DAESService;
import ug.daes.PKICoreServiceException;
import ug.daes.Result;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import org.apache.hc.client5.http.io.HttpClientConnectionManager;



@OpenAPIDefinition(info = @Info(title = "My API", version = "1.0", description = "API documentation"))
@SpringBootApplication
public class OrganizationApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrganizationApplication.class, args);

	}

	@Bean
	public RestTemplate restTemplate() throws Exception {

		TrustStrategy acceptingTrustStrategy =
				(X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = SSLContextBuilder.create()
				.loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

		CloseableHttpClient httpClient;
        httpClient = HttpClients.custom()
                .setUserAgent(String.valueOf(sslContext))
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);

		requestFactory.setConnectionRequestTimeout(300_000);
		//requestFactory.setConnectTimeout(300_000);
		requestFactory.setReadTimeout(300_000);

		return new RestTemplate(requestFactory);
	}



	/**
	 * Signatue service initilize.
	 *
	 * @throws InterruptedException
	 */
//	@Bean
//	public void signatueServiceInitilize() throws InterruptedException {
//		try {
//			Result result = DAESService.initPKINativeUtils();
//			if (result.getStatus() == 0)
//				System.out.println(new String(result.getStatusMessage()));
//			else {
//				System.out.println(new String(result.getResponse()));
//			}
//		} catch (PKICoreServiceException e) {
//			e.printStackTrace();
//		}
//	}
	@Bean
	public Result signatueServiceInitilize() throws InterruptedException {
		try {
			Result result = DAESService.initPKINativeUtils();
			if (result.getStatus() == 0)
				System.out.println(new String(result.getStatusMessage()));
			else
				System.out.println(new String(result.getResponse()));
			return result;
		} catch (PKICoreServiceException e) {
			e.printStackTrace();
			throw new RuntimeException("Initialization failed", e);
		}
	}


//	@Bean("jasyptStringEncryptor")
//	public StringEncryptor stringEncryptor() {
//		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//		config.setPassword("$DttKycImplEngin@@r");
//		config.setAlgorithm("PBEWithHMACSHA512AndAES_256");
//		config.setKeyObtentionIterations("1000");
//		config.setPoolSize("1");
//		config.setProviderName("SunJCE");
//		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
//		config.setStringOutputType("base64");
//		encryptor.setConfig(config);
//
//		return encryptor;
//	}
}
