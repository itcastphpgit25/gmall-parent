package com.atguigu.gmall.rabbit.service;

import com.atguigu.gmall.rabbit.bean.Order;
import com.atguigu.gmall.rabbit.bean.User;
import com.rabbitmq.client.Channel;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class rabbitService {

    /**
     *
     * 购物车事例
     */
    //监听死性队列
    @RabbitListener(queues = "deadQueue")
    public void orderCancel(Order order,Channel channel,Message message) throws IOException {

        System.out.println("收到了超时订单......"+order);
        if(order.getStatus().equals("PAYED")){
            System.out.println("此订单已经支付...");
        }else {
            System.out.println("此订单将关闭....");
        }
        //活干完了，手动操作
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }



    /**
     *方法上可以有三种参数
     *   -Message收到的消息封装到message
     *   -直接写将消息封装成某种类型
     *   -可以写Channel自动控制发送的消息
     *   -ack确认收到  nack:不给回复确认收到 ==》reject
     *   -消息丢失怎么办：手动回复：ack
     *   -basicAck：手动回复,告诉RabbitMQ我收到消息了  basicNack：自动回复
     *   -业务中出现异常，一定拒绝入队：nck--》basicNack
     *   -basicNack和 basicReject区别：basicNack：消息拿来
     */
//    @RabbitListener(queues = "atguigu")
//    public void consumerConfig(Message message){
//        System.out.println("收到消息："+message.getBody());
//    }


//    @RabbitListener(queues = "atguigu")
//    public void consumerConfig(Map<String,Object> map){
//        System.out.println("收到消息："+map);
//    }
    //接收消息
    //@RabbitListener(queues = "atguigu")
    public void consumerConfig(User user,Message message,Channel channel) throws IOException {


        //拒绝消息重新放入队列
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        try {
            if(user.getAge()%2==0){
                System.out.println("【1号Server】收到消息："+user);
            }
        }catch (Exception e){
            System.out.println("【1号Server】决绝消息：");
            //拒绝了当前消息，并重新入队                                               是否拒绝多个      重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);

        }
    }
    //@RabbitListener(queues = "atguigu")
    public void twoConsumerConfig(User user,Message message,Channel channel) throws IOException {
        //消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //System.out.println("【2号Server】收到消息："+user);
        try {
            if(user.getAge()%2!=0){
                System.out.println("【2号Server】收到消息："+user);
            }
        }catch (Exception e){
            System.out.println("【2号Server】决绝消息：");
            //拒绝了当前消息，并重新入队                                               是否拒绝多个      重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    @RabbitListener(queues = "atguigu.emps")
    public void mc(User user,Message message,Channel channel) throws IOException {
        //消息属性
        long tag = message.getMessageProperties().getDeliveryTag();
        //只要某个消息没回复，这个下西市unacked状态，而且不会再次发过来
        System.out.println("收到消息。。。"+tag);
        if(tag%2==0){
            //只回复我当前的，手动回复
            channel.basicAck(tag,false);
        }
    }

}
