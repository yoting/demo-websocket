package com.gusi.demo.websocket.simple;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import javax.servlet.http.HttpServletRequest;

public class DemoWebSocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,HttpServletRequest request) {
        DemoWebSocketInbound newClientConn = new DemoWebSocketInbound();
        return newClientConn;
    }
}