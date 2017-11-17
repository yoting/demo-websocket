package com.gusi.demo.websocket.simple;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class DemoClientEndPoint {
    private static CountDownLatch latch;
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("websocket opening.");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("receive message：" + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.session = null;
        latch.countDown();
    }

    public static void main(String[] args) {
        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();
        try {
            Session session = client.connectToServer(DemoClientEndPoint.class,
                    new URI("ws://localhost:8080/demoWebsocket/demostomp"));

            for (int i = 0; i < 10; i++) {
                session.getBasicRemote().sendText("message " + String.valueOf(i));
            }
            latch.await();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

