package com.bwc.common.rest;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.HttpsSupport;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : RestTemplate.java
 * @Description : 클래스 설명을 기술합니다.
 * @author BWC072
 * @since 2018. 8. 8.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2018. 8. 8.     BWC101         최초 생성
 * </pre>
 */

@Slf4j
@Component
public class RestTemplateHttp {

	private static final int DEFAULT_TIME = 30; // 30초
	private static final int DEFAULT_TOTAL = 100;
	private static final int DEFAULT_PERROUTE = 10;
	private String add_Time = "";

	@Bean
	public RestTemplate restTemplate() throws Exception {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

		factory.setConnectTimeout(DEFAULT_TIME * 1000);    // 연결시간 초과

		RequestConfig requestConfig = RequestConfig.custom()
			.setResponseTimeout(DEFAULT_TIME * 1000, TimeUnit.MILLISECONDS)    // 응답 시간 초과
			.build();

		HttpClient httpClient = HttpClientBuilder.create()
			.setDefaultRequestConfig(requestConfig)
			.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
				.setMaxConnTotal(DEFAULT_TOTAL)
				.setMaxConnPerRoute(DEFAULT_PERROUTE)
				.build())
			.build();
		factory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(factory);

		return restTemplate;
	}

	@Bean
	public RestTemplate getHttpsTemplate() {
		HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();

		httpClientFactory.setConnectTimeout(DEFAULT_TIME * 1000);
		RestTemplate restTemplate = null;
		try {

			TrustStrategy acceptingTrustStategy = (new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			});

			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStategy).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				new String[] {"TLSv1.2"}, null, HttpsSupport.getDefaultHostnameVerifier());

			RequestConfig requestConfig = RequestConfig.custom()
				.setResponseTimeout(DEFAULT_TIME * 1000, TimeUnit.MILLISECONDS)    // 응답 시간 초과
				.build();

			httpClientFactory.setHttpClient(HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
					.setSSLSocketFactory(sslsf)
					.setMaxConnTotal(DEFAULT_TOTAL)
					.setMaxConnPerRoute(DEFAULT_PERROUTE)
					.build())
				.build());

		} catch (Exception e) {
			log.error("getHttpsTemplate() - Exception : {}", e.getMessage());
		}

		restTemplate = new RestTemplate(httpClientFactory);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

		return restTemplate;
	}

}


