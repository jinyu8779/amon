package websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jqyu on 2017/9/8.
 */
public class MyWebSocketUtils {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<PushServerEndpoint> pushSet = new CopyOnWriteArraySet<PushServerEndpoint>();

    //
    private static ConcurrentHashMap<String, PushServerEndpoint> pushMap = new ConcurrentHashMap<String, PushServerEndpoint>();

    
    public static AtomicInteger getOnlineCount() {
        return onlineCount;
    }

    public static CopyOnWriteArraySet<PushServerEndpoint> getPushSet() {
        return pushSet;
    }

    public static ConcurrentHashMap<String, PushServerEndpoint> getPushMap() {
        return pushMap;
    }


    /***
     * 维护关系（增加）
     * @param pushServerEndpoint
     */
    public static void addRelation(PushServerEndpoint pushServerEndpoint) {

        MyWebSocketUtils.getPushSet().add(pushServerEndpoint);     //加入set中

        MyWebSocketUtils.getPushMap().put(pushServerEndpoint.getUserId(), pushServerEndpoint);//加入map中

    }


    /***
     * 维护关系（删除）
     * @param pushServerEndpoint
     */
    public static void removeRelation(PushServerEndpoint pushServerEndpoint) {

        MyWebSocketUtils.getPushSet().remove(pushServerEndpoint);     //从set中移除

        MyWebSocketUtils.getPushMap().remove(pushServerEndpoint.getUserId());//从map中移除

    }


    /***
     * 同步方式向某一税务机关下的所有人发送消息（含税务机关的下级机关、下下级机关...）
     * @param swjgdm
     * @param message
     */
    public static void basicSendMsgBySwjgdm(String swjgdm, String message) {
        //获取到待处理列表
        CopyOnWriteArraySet<PushServerEndpoint> set = getPushSet();

        for (PushServerEndpoint cur : set) {

            //判断每个对象中的swjgdm是否包含参数中的内容（即是否以入参开头）
            if (cur.getSwjgdm().startsWith(swjgdm)) {

                try {
                    cur.getSession().getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


    }

    /***
     * 异步方式向某一税务机关下的所有人发送消息（含税务机关的下级机关、下下级机关...）
     * @param swjgdm
     * @param message
     */
    public static void asyncSendMsgBySwjgdm(String swjgdm, String message) {
        //获取到待处理列表
        CopyOnWriteArraySet<PushServerEndpoint> set = getPushSet();

        for (PushServerEndpoint cur : set) {

            //判断每个对象中的swjgdm是否包含参数中的内容（即是否以入参开头）
            if (cur.getSwjgdm().startsWith(swjgdm)) {

                cur.getSession().getAsyncRemote().sendText(message);
            }

        }


    }

    /**
     * 同步方式批量发送消息
     *
     * @param message
     */
    public static void basicSendMsgToAll(String message) {
        //获取到待处理列表
        CopyOnWriteArraySet<PushServerEndpoint> set = MyWebSocketUtils.getPushSet();

        for (PushServerEndpoint cur : set) {

            try {
                cur.getSession().getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /***
     * 异步方式批量发送消息
     * @param message
     */
    public static void asyncSendMsgToAll(String message) {
        //获取到待处理列表
        CopyOnWriteArraySet<PushServerEndpoint> set = MyWebSocketUtils.getPushSet();

        for (PushServerEndpoint cur : set) {

            cur.getSession().getAsyncRemote().sendText(message);
        }

    }


    /***
     * 同步向个人发送消息
     * @param userId
     * @param message
     * @return 发送成功则返回true, 否则返回false
     */
    public static boolean sendMessage(String userId, String message) {


        try {

            ConcurrentHashMap<String, PushServerEndpoint> map = getPushMap();

            PushServerEndpoint curPushServerEndpoint = map.get(userId);
            if (curPushServerEndpoint == null)
                return false;


            curPushServerEndpoint.getSession().getBasicRemote().sendText(message);
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
