package org.tutorial.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tutorial.constants.MqConstants;
import org.tutorial.service.HotelService;

@Component
public class HotelListener {
	@Autowired
	private HotelService hotelService;

	@RabbitListener(queues = MqConstants.HOTEL_INSERT_QUEUE)
    public void listenerHotelInsertOrUpdate(Long id) {
        hotelService.insertById(id);
    }

    @RabbitListener(queues = MqConstants.HOTEL_DELETE_QUEUE)
    public void listenerHotelDelete(Long id) {
        hotelService.deleteById(id);
    }
}
