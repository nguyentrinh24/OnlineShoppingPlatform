package com.project.shopapp.Consumers;

import com.project.shopapp.Configurations.RabbitMQConfig;
import com.project.shopapp.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void processOrder(Order order) {
        try {
            logger.info("Processing order: {}", order.getId());
          
        } catch (Exception e) {
            logger.error("Error processing order: {}", order.getId(), e);
        }
    }
} 