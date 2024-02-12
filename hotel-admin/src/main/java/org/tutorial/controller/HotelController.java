package org.tutorial.controller;

import java.security.InvalidParameterException;

import javax.persistence.EntityNotFoundException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tutorial.constants.MqConstants;
import org.tutorial.pojo.Hotel;
import org.tutorial.pojo.PageResult;
import org.tutorial.repository.HotelRepository;

@RestController
@RequestMapping("/hotel")
public class HotelController {
	@Autowired
	HotelRepository repository;
	
    @Autowired
    private RabbitTemplate rabbitTemplate;

	@GetMapping("/{id}")
	public Hotel queryById(@PathVariable("id") Long id) {
		return repository.findById(id).get();
	}

	@GetMapping("/list")
	public PageResult hotelList(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "1") Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Hotel> result = repository.findAll(pageable);

		return new PageResult(result.getTotalElements(), result.getContent());
	}

	@PostMapping
	public void saveHotel(@RequestBody Hotel hotel) {
		repository.save(hotel);

		// 發送消息，提醒消費者更新ES中的資料
		rabbitTemplate.convertAndSend(MqConstants.HOTEL_EXCHANGE, MqConstants.HOTEL_INSERT_KEY, hotel.getId());
	}

	@PutMapping()
	public void updateById(@RequestBody Hotel hotel) {
		if (hotel.getId() == null) {
			throw new InvalidParameterException("id不能為空");
		}
		if (!repository.findById(hotel.getId()).isPresent()) {
			throw new EntityNotFoundException("無此實體");
		}
		repository.save(hotel);

		// 發送消息，提醒消費者更新ES中的資料
		rabbitTemplate.convertAndSend(MqConstants.HOTEL_EXCHANGE, MqConstants.HOTEL_INSERT_KEY, hotel.getId());
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable("id") Long id) {
		if (!repository.findById(id).isPresent()) {
			throw new EntityNotFoundException("無此實體");
		}
		repository.deleteById(id);

		// 發送消息，提醒消費者刪除ES中的資料
		rabbitTemplate.convertAndSend(MqConstants.HOTEL_EXCHANGE, MqConstants.HOTEL_DELETE_KEY, id);
	}
}
