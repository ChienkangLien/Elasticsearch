package org.tutorial.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tutorial.pojo.PageResult;
import org.tutorial.pojo.RequestParams;
import org.tutorial.service.HotelService;

@RestController
@RequestMapping("/hotel")
public class HotelController {
	@Autowired
	HotelService service;

	@PostMapping("/list")
	public PageResult search(@RequestBody RequestParams params) {
		return service.search(params);
	}
}
