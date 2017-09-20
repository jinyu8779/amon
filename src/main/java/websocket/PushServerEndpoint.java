/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package websocket;

import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/push/{swjgdm}/{userid}")
public class PushServerEndpoint {


    //临时分配的用户名
    private final String nickname;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //税务机关代码
    private String swjgdm;

    //用户userId
    private String userId;

    public PushServerEndpoint() {
        //在线数加1
        this.nickname = "游客ID：" + MyWebSocketUtils.getOnlineCount().getAndIncrement();
    }

    @OnOpen
    public void onOpen(@PathParam("swjgdm") String swjgdm, @PathParam("userid") String userId, Session session) {

        this.session = session;
        this.swjgdm = swjgdm;
        this.userId = userId;
        MyWebSocketUtils.addRelation(this);//维护记录websocket连接与用户数据的关系
        System.out.println("有新连接加入！当前在线人数为" + MyWebSocketUtils.getOnlineCount());

    }

    @OnClose
    public void onClose() {
        MyWebSocketUtils.removeRelation(this);
        System.out.println("有人退出连接！剩余在线人数为" + MyWebSocketUtils.getOnlineCount().decrementAndGet());
    }


    @OnMessage
    public void onMsg(String message) {

        System.out.println("服务端收到来自WEB端的消息。内容为：" + message);

    }


    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public String getNickname() {
        return nickname;
    }

    public Session getSession() {
        return session;
    }

    public String getSwjgdm() {
        return swjgdm;
    }

    public String getUserId() {
        return userId;
    }
}
