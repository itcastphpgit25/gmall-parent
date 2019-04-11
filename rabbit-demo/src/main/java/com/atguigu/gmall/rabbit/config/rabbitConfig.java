package com.atguigu.gmall.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.table.TableRowSorter;
import java.util.HashMap;

@Configuration
public class rabbitConfig {

    /**
     *
     * 去系统的消息转换都使用json进行转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){

      return new Jackson2JsonMessageConverter();
  }

    /**
     *
     * 全系统使用json进行转换
     */
    @Bean
    public Queue queue(){

        return new Queue("ninini", true, false,false);
    }

    @Bean
    public Exchange exchange(){

        return new DirectExchange("Java-777",true,false);
    }

    @Bean
    public Binding binding(){
        return new Binding("ninini",Binding.DestinationType.QUEUE,
                "Java-777",
                "ninini",
                null);
    }

    /**
     *=================绑定普通交换机和队列=================
     * 1.创建一个普通的交换机
     * 2.创建一个普通队列
     */
    @Bean
    public Exchange delayExchange(){

        return new DirectExchange("delayExchange",true,false);
    }
    @Bean
    public Queue delayQueue(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange","deadExchange");
        map.put("x-dead-letter-routing-key","dead");
        map.put("x-message-ttl",1000*60);
        return new Queue("delayQueue",true,false,false,map);
    }
    @Bean
    public Binding delayBinding(){
        Binding binding = new Binding("delayQueue",
                Binding.DestinationType.QUEUE,
                "delayExchange",
                "order",
                null);
        return binding;
    }
    //================绑定死性交换机和队列================
    //普通队列中信死之后
    @Bean
    public Exchange deadExchange(){

        return new DirectExchange(
                "deadExchange",true,false);
    }
    @Bean
    public Queue deadQueue(){

        return new Queue(
                "deadQueue",true,false,false);
    }
    @Bean
    public Binding deadBinding(){
        Binding binding = new Binding("deadQueue",
                Binding.DestinationType.QUEUE,
                "deadExchange",
                "dead",
                null);
        return binding;
    }
}
