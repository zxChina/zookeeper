package com.zhouxu.study.zookeeper.javaApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * javaApi对zookeeperd的支持(zookeeper原生的对java支持的APi)
 * 创建zookeeper会话
 */
public class CreateSessionDemo {
    private static final String  CONNECTURLS = "192.168.0.102:2181,192.168.0.104:2181,192.168.0.105:2181";

    private static CountDownLatch latch = new CountDownLatch(1);
    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTURLS, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //如果当前状态是已经连接状态，那么用计数器去控制（not_connected,connecting,connected,closed）
                if (Event.KeeperState.SyncConnected == watchedEvent.getState()){
                    latch.countDown();
                    System.out.println(watchedEvent.getState());
                }

            }
        });
        latch.await();
        System.out.println(zooKeeper.getState());
    }
}
