package org.tutorial.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@PostMapping("/filters")
	public Map<String, List<String>> getFilters(@RequestBody RequestParams params) {
		return service.filters(params);
	}
	
	@GetMapping("/suggestion")
	public List<String> getSuggestions(@RequestParam("key") String prefix) {
		return service.getSuggestions(prefix);
	}
}
