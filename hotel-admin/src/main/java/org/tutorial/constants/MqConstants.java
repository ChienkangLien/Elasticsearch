package org.tutorial.constants;

public class MqConstants {
	/**
	 * 交換機
	 */
	public static final String HOTEL_EXCHANGE = "hotel.topic";

	/**
	 * 監聽增加和修改的隊列
	 */
	public static final String HOTEL_INSERT_QUEUE = "hotel.insert.queue";

	/**
	 * 監聽刪除的隊列
	 */
	public static final String HOTEL_DELETE_QUEUE = "hotel.delete.queue";

	/**
	 * 新增或修改的RoutingKey
	 */
	public static final String HOTEL_INSERT_KEY = "hotel.insert";

	/**
	 * 刪除的RoutingKey
	 */
	public static final String HOTEL_DELETE_KEY = "hotel.delete";
}
