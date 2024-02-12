package org.tutorial;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HotelSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelSearchApplication.class, args);
	}

	@Bean
	public RestHighLevelClient client() {
		return new RestHighLevelClient(RestClient.builder(
				new HttpHost("192.168.191.133", 9200))
		);
	}
}
