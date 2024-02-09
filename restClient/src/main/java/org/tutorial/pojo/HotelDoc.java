package org.tutorial.pojo;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HotelDoc {
	private Long id;
	private String name;
	private String address;
	private Integer price;
	private Integer score;
	private String brand;
	private String city;
	private String starName;
	private String business;
	private String location;
	private String pic;
	private Object distance;
	private Boolean isAD;
	private List<String> suggestion; // 品牌和商圈加入自動補全功能

	public HotelDoc(Hotel hotel) {
		super();
		this.id = hotel.getId();
		this.name = hotel.getName();
		this.address = hotel.getAddress();
		this.price = hotel.getPrice();
		this.score = hotel.getScore();
		this.brand = hotel.getBrand();
		this.city = hotel.getCity();
		this.starName = hotel.getStarName();
		this.business = hotel.getBusiness();
		this.location = hotel.getLongitude() + ", " + hotel.getLatitude();
		this.pic = hotel.getPic();
		this.isAD = hotel.getIsAD() == 1 ? true : false;
		this.suggestion = Arrays.asList(this.brand, this.business);
	}

}
