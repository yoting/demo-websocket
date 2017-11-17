package com.gusi.demo.websocket.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * websocket使用stomp接收发送消息
 */
@Controller
public class DemoWebsocketStompController {

    /**
     * 服务端主动给客户端发送消息的操作对象，再定义websocket的时候，内部已经向spring容器注册该bean
     */
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    private String simpSessionId = null;//临时存放客户端id，用于之后发送一对一消息。正常使用应该是放在map中

    /**
     * 表示服务端可以接收客户端通过主题"/app/hello"发送的消息，其中/app是配置的固定前缀<br>
     * 客户端需要在主题"/topic/hello"上监听并接收服务端发回的消息，或者在@SendTo指定地址接收消息
     *
     * @param topic
     * @param headers
     */
    @MessageMapping("/hello")//默认消息代理目的地为"/topic/hello"
    public ResultDto handleMessage(@Header("atytopic") String topic, @Headers Map<String, Object> headers, ClientVo vo) {
        System.out.println("message:" + vo);
        System.out.println(topic);
        System.out.println(headers);
        return new ResultDto("I know your name is:" + vo.getName() + ",and your age is:" + vo.getAge());
    }

    /**
     * 表示服务端可以监听客户端通过主题"/app/hello1"上订阅的消息，其中/app是配置的固定前缀<br>
     * 客户端要在主题"/topic/hello1"上监听并接收服端返回的消息，或者在@SendTo指定地址上接收消息
     *
     * @return
     */
    @SubscribeMapping("/hello1") //默认消息代理目的地为"/topic/hello1"
    @SendTo("/topic/world1") //设置消息代理目的地为"/topic/world3"
    public ResultDto handleSubscribe(@Headers Map<String, Object> headers) {
        System.out.println("subscribe");
        System.out.println(headers);
        this.simpSessionId = headers.get("simpSessionId").toString();//再客户端订阅的时候，将客户端的id存下来
        return new ResultDto("this is return subscribe");
    }

    /**
     * 该方法类似于hello接口方法
     *
     * @param headers
     * @param vo
     * @return
     */
    @MessageMapping("/hello2")//默认消息代理目的地为"/topic/hello2"
    @SendTo("/queue/world2") //设置消息代理目的地为"/queue/world2"
    public ResultDto handleMessage2(@Headers Map<String, Object> headers, ClientVo vo) {
        System.out.println("message:" + vo);
        System.out.println(headers);
        return new ResultDto("I know your name is:" + vo.getName() + ",and your age is:" + vo.getAge());
    }

    /**
     * 表示服务端可以监听客户端通过主题"/app/hello3"上发送的消息<br>
     * 服务端返回的消息是发送给特定用户的，不是所有监听了改地址的客户端都能收到，主要是使用了@SendToUser<br>
     * 客户端接收一对一消息的主题应该是“/user/” + 用户Id + “/topic/message”,但是客户端订阅的时候只需要订阅"/user/topic/message"
     *
     * @return
     */
    @MessageMapping("/hello3")
    @SendToUser("/topic/message")
    public ResultDto handleMessageToUser(@Headers Map<String, Object> headers) {
        System.out.println("this is the user message");
        System.out.println(headers);
        return new ResultDto("this is return message to user!");
    }

    /**
     * 测试服务端主动发送消息给客户端
     *
     * @param type 发送消息类型
     * @return
     */
    @RequestMapping(path = "/send/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDto send(@PathVariable("type") Integer type) {
        ResultDto resultDto = null;
        if (type == 1) {//发送给特定用户
            simpMessageSendingOperations.convertAndSendToUser(simpSessionId, "/topic/message", new ResultDto("send to user message!")/*, createHeaders(simpSessionId)*/);
            return new ResultDto("success to user!");
        } else {//发送广播
            simpMessageSendingOperations.convertAndSend("/topic/world4", new ResultDto("send boardcase message!"));
            return new ResultDto("success boardcast!");
        }
    }

    /**
     * 发送一对一消息时候构建消息头
     *
     * @param sessionId
     * @return
     */
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}