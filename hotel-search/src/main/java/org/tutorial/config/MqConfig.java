package org.tutorial.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.tutorial.constants.MqConstants;

public class MqConfig {
	/**
	 * 定義交換機
	 */
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(MqConstants.HOTEL_EXCHANGE, true, false);
	}

	/**
	 * 定義新增或修改隊列
	 */
	@Bean
	public Queue insertQueue() {
		return new Queue(MqConstants.HOTEL_INSERT_QUEUE, true);
	}

	/**
	 * 定義刪除隊列
	 */
	@Bean
	public Queue deleteQueue() {
		return new Queue(MqConstants.HOTEL_DELETE_QUEUE, true);
	}

	/**
	 * 定義新增或修改的綁定關係
	 */
	@Bean
	public Binding insertQueueBinding() {
		return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(MqConstants.HOTEL_INSERT_KEY);
	}

	/**
	 * 定義刪除的綁定關係
	 */
	@Bean
	public Binding deleteQueueBinding() {
		return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(MqConstants.HOTEL_DELETE_KEY);
	}
}
