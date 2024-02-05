package org.tutorial;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tutorial.service.HotelService;

@SpringBootTest
class RestClientApplicationTests {
	
	@Autowired
	private HotelService service;

	@Test
	void contextLoads() {
		System.out.println(service.filters());
	}

}
