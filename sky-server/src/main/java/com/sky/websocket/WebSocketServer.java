package com.sky.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {
    //存放会话对象
    private static Map<String, Session> sessionMap = new HashMap<>();

    //建立连接成功调用插入方法
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("客户端"+sid+"建立连接");
        sessionMap.put(sid, session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        System.out.println("收到客户端:"+sid+"的信息"+message);
    }
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接断开"+sid);
        sessionMap.remove(sid);
    }

    //群发
    public void sengToAllClient(String message) throws IOException {
        Collection<Session> values = sessionMap.values();
        for (Session session:values) {
                session.getBasicRemote().sendText(message);
        }
    }
}
