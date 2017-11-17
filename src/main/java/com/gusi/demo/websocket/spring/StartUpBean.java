package com.gusi.demo.websocket.spring;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created by yydeng on 2017/8/31.
 */
public class StartUpBean implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("spring start up ...");
    }
}
