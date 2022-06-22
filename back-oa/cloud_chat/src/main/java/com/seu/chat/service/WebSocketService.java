package com.seu.chat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seu.util.entity.ChatMsg;
import com.seu.util.entity.CompanyNotice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 @Date: 2020/10/24 15:00
 @Description: websocket 服务类
 @author Ajie
 */

/**
 * @ServerEndpoint
 *这个注解用于标识作用在类上，它的主要功能是把当前类标识成一个WebSocket的服务端
 *注解的值用户客户端连接访问的URL地址
 */

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{uuid}")
public class WebSocketService {

   private static ChatMsgService chatMsgService;
   private static CompanyNoticeService companyNoticeService;

   @Autowired
   private void setChatMsgService(ChatMsgService chatMsgService){
       WebSocketService.chatMsgService = chatMsgService;  // 解决Component下无法Autowired
   }
    @Autowired
    private void setCompanyNoticeService(CompanyNoticeService companyNoticeService){
        WebSocketService.companyNoticeService = companyNoticeService;  // 解决Component下无法Autowired
    }
    /**
     *  与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;

    /**
     * 标识当前连接客户端的用户名
     */
    private String uuid;

    /**
     *  用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String, WebSocketService> webSocketSet = new ConcurrentHashMap<String, WebSocketService>();

    /**
     * @param session
     * @param uuid
     * OnOpen：创建连接
     */
    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "uuid") String uuid) throws IOException {
        this.session = session;
        this.uuid = uuid;
        // uuid是用来表示唯一客户端，如果需要指定发送，需要指定发送通过name来区分
        webSocketSet.put(uuid,this);
        log.info("[WebSocket] 连接成功，当前连接人数为：={}",webSocketSet.size());
        // 查询离线消息
        QueryWrapper<ChatMsg> chatMsgQueryWrapper = new QueryWrapper<>();
        chatMsgQueryWrapper.eq("user",uuid);
        List<ChatMsg> list = chatMsgService.list(chatMsgQueryWrapper);
        Collections.reverse(list);// 倒序
        if (list.size()>0)
            for (ChatMsg msg : list){
                AppointSending(msg.getUser(),msg.getMsg());
                chatMsgService.removeById(msg.getId());
            }
    }

    /**
     * OnClose：关闭连接的操作
     */
    @OnClose
    public void OnClose() {
        webSocketSet.remove(this.uuid);
        System.out.println("Connection OnClose");
        log.info("[WebSocket] 退出成功，当前连接人数为：={}", webSocketSet.size());
    }

    /**
     * 接收消息，进行处理
     * @param message
     */
    @OnMessage
    public void OnMessage(String message) throws IOException {
        log.info("[WebSocket] 收到消息：{}",message);
        //do something....
        JSONObject msg = JSON.parseObject(message);
        if (msg.getString("type").equals("msg")) {
            WebSocketService to = webSocketSet.get(msg.getString("to"));
            if (to != null){
                AppointSending(msg.getString("to"),message);
            }else { // 不在线的时候，保存离线消息
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.setUser(msg.getString("to"));
                chatMsg.setMsg(message);
                chatMsgService.save(chatMsg);
            }
            log.info("[WebSocket] 指定发送");
        }else {
            CompanyNotice notice = new CompanyNotice();
            notice.setTime(new Date());
            notice.setContext(msg.getString("context"));
            notice.setTitle(msg.getString("title"));
            companyNoticeService.save(notice);
            GroupSending(message); // 群发公告
        }
    }


    //传送错误时调用
    @OnError
    public void OnError(Throwable error) {
        System.out.println(error.getMessage());
        error.printStackTrace();
    }

    /**
     * 主动推送,谁发的就退给谁
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发
     * @param message
     */
    public void GroupSending(String message) {
        for (String name : webSocketSet.keySet()) {
            try {
                webSocketSet.get(name).session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定发送
     * @param to
     * @param message
     */
    public void AppointSending(String to, String message) {
        if (webSocketSet.get(to).session.isOpen()) {
            try {
                webSocketSet.get(to).session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            log.info("[WebSocket] 用户不在线");
        }
    }
    }
