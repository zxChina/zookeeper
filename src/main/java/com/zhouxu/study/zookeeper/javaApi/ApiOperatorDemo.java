package com.zhouxu.study.zookeeper.javaApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper的基本APi增删改查操作
 */
public class ApiOperatorDemo implements Watcher{
    private static final String  CONNECTURLS = "192.168.0.102:2181,192.168.0.104:2181,192.168.0.105:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zookeeper;
    private static Stat stat=new Stat();
    @Override
    public void process(WatchedEvent watchedEvent) {
        //如果当前状态是已经连接状态，那么用计数器去控制（not_connected,connecting,connected,closed)
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()){
            countDownLatch.countDown();
            System.out.println(watchedEvent.getState());
            //监听的类型判断
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()){
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }else if(watchedEvent.getType()== Event.EventType.NodeDataChanged){
                //第二个参数设置为true会循环监听，设置为false就是一次性的，watcher的通知是一次性，一旦触发一次通知后，该watcher就失效
                try {
                    System.out.println("数据变更触发路径："+watchedEvent.getPath()+"->改变后的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged){//子节点的数据变化会触发
                try {
                    System.out.println("子节点数据变更路径："+watchedEvent.getPath()+"->节点的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(watchedEvent.getType()== Event.EventType.NodeCreated){//创建子节点的时候会触发
                try {
                    System.out.println("节点创建路径："+watchedEvent.getPath()+"->节点的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(watchedEvent.getType()== Event.EventType.NodeDeleted){//子节点删除会触发
                System.out.println("节点删除路径："+watchedEvent.getPath());
            }
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zookeeper=new ZooKeeper(CONNECTURLS, 5000, new ApiOperatorDemo());
        countDownLatch.await();


        //创建节点
        String result=zookeeper.create("/node","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //需要阻塞一段时间去注册这个节点的监听，因为是异步执行可能主会话已经结束了所以需要阻塞住用来监听下面数据改变和删除的事件，或者是下面那种手动增加一个监听watch
        // zookeeper.getData("/node",new ApiOperatorDemo(),stat);//增加一个watch( 监听)
        TimeUnit.SECONDS.sleep(1);
        System.out.println("创建成功："+result);

        //修改数据
        zookeeper.setData("/node","mic123".getBytes(),-1);
        Thread.sleep(2000);
        //修改数据
        zookeeper.setData("/node","mic234".getBytes(),-1);
        Thread.sleep(2000);

        //删除节点
        zookeeper.delete("/node",-1);
        Thread.sleep(2000);


        //创建节点和子节点
        String path="/nodeTest";

        zookeeper.create(path,"123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        TimeUnit.SECONDS.sleep(1);

        Stat stat=zookeeper.exists(path+"/node1",true);
        if(stat==null){//表示节点不存在
            zookeeper.create(path+"/node1","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            TimeUnit.SECONDS.sleep(1);
        }

        //修改子路径
        zookeeper.setData(path+"/node1","mic123".getBytes(),-1);


        //获取指定节点下的子节点
        List<String> childrens=zookeeper.getChildren("/node",true);
        System.out.println(childrens);

    }
}
