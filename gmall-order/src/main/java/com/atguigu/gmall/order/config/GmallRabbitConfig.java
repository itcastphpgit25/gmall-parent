package com.atguigu.gmall.order.config;

import org.codehaus.groovy.classgen.asm.BinaryBooleanExpressionHelper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GmallRabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     *
     * 广播交换机
     */
    @Bean
    public Exchange orderFanoutExchange(){
        return new FanoutExchange("orderFanoutExchange",true,false);
    }
    /**
     *
     * 死信交换机
     *
     */
    @Bean
    public Exchange orderDeadExchange(){
        return new DirectExchange("orderDeadExchange",true,false);
    }

    /**
     *
     * 库存系统接受订单消息的队列
     */
    @Bean
    public Queue stockOrderQueue(){
        return new Queue("stockOrderQueue",true,false,false);
    }
    /**
     *
     * 用户系统接收订单消息队列
     */
    @Bean
    public Queue userOrderQueue(){
        return new Queue("userOrderQueue",true,false,false);
    }
    /**
     *
     * 所有过期订单队列
     */
    @Bean
    public Queue deadOrderQueue(){
        return new Queue("deadOrderQueue",true,false,false);
    }
    /**
     *
     * 延迟队列
     */
    @Bean
    public Queue delayOrderQueue(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange","orderDeadExchange");
        map.put("x-dead-letter-routing-key","dead.order");
        map.put("x-message-ttl",1000*60*30);

        Queue queue = new Queue("delayOrderQueue",true,false,false);
        return queue;
    }
    /**
     * ============绑定关系==============
     *
     */
    @Bean
    public Binding stockOrderQueueBinding(){
        return new Binding("stockOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderFanoutExchange",
                "",null);
    }
    @Bean
    public Binding userOrderQueueBinding(){
        return new Binding("userOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderFanoutExchange","",null);
    }

    @Bean
    public Binding delayOrderQueueBinding(){
        return new Binding("delayOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderFanoutExchange",
                "", null);
    }

    /**
     *
     * 死性队列和死性交换机绑定
     * @return
     */
    @Bean
    public Binding deadOrderQueueBinding(){
        return new Binding("deadOrderQueue",
                Binding.DestinationType.QUEUE,
                "orderDeadExchange",
                "dead.order", null);
    }


}
