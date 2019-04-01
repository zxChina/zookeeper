package com.zhouxu.study.zookeeper.javaApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 节点控制
 */
public class AuthControlDemo implements Watcher{
    private static final String  CONNECTURLS = "192.168.0.102:2181,192.168.0.104:2181,192.168.0.105:2181";

    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
            if(Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }
        }
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zookeeper = new ZooKeeper(CONNECTURLS,5000,new AuthControlDemo());
        countDownLatch.await();

        ACL acl = new ACL(ZooDefs.Perms.CREATE,new Id("digest","root:root"));
        ACL acl2 = new ACL(ZooDefs.Perms.CREATE,new Id("ip","192.168.1.1"));

        List<ACL> acls=new ArrayList<>();
        acls.add(acl);
        acls.add(acl2);
        zookeeper.create("/auth1","123".getBytes(),acls, CreateMode.PERSISTENT);
        zookeeper.addAuthInfo("digest","root:root".getBytes());
        zookeeper.create("/auth1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zookeeper.create("/auth1/auth1-1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);


        ZooKeeper zooKeeper1=new ZooKeeper(CONNECTURLS, 5000, new AuthControlDemo());
        countDownLatch.await();
        zooKeeper1.delete("/auth1",-1);
    }
}