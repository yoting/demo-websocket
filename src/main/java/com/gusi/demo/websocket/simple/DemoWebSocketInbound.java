package com.gusi.demo.websocket.simple;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class DemoWebSocketInbound extends MessageInbound {
    private static List<DemoWebSocketInbound> mmiList = new ArrayList<DemoWebSocketInbound>();
    private WsOutbound myoutbound;

    @Override
    public void onOpen(WsOutbound outbound) {
        try {
            System.out.println("Open Client.");
            this.myoutbound = outbound;
            mmiList.add(this);
            outbound.writeTextMessage(CharBuffer.wrap("Welcome!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int status) {
        System.out.println("Close Client.");
        mmiList.remove(this);
    }

    @Override
    public void onTextMessage(CharBuffer cb) throws IOException {
        System.out.println("Accept Message : " + cb);
        for (DemoWebSocketInbound mmib : mmiList) {
            CharBuffer buffer = CharBuffer.wrap(cb);
            mmib.myoutbound.writeTextMessage(buffer);
            mmib.myoutbound.flush();
        }
    }

    @Override
    public void onBinaryMessage(ByteBuffer bb) throws IOException {
    }

    @Override
    public int getReadTimeout() {
        return 0;
    }
}
