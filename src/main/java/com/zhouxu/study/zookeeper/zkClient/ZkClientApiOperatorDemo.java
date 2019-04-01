package com.zhouxu.study.zookeeper.zkClient;


import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 一些人封装的zkClient，简化我们对原生API的书写
 */
public class ZkClientApiOperatorDemo {
    private static final String  CONNECTURLS = "192.168.0.102:2181,192.168.0.104:2181,192.168.0.105:2181";

    private static ZkClient getInstanceOf(){
        return new ZkClient(CONNECTURLS,5000);
    }

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = getInstanceOf();
        //创建临时节点（不能同时创建父子节点）
        zkClient.createEphemeral("/zkClient",true);
        System.out.println("创建临时节点成功");

        //创建持久化节点（（可以直接创建父节点和子节点，封装了递归调用,但是要把data设置为true，否则就不能同时创建父子节点））
        zkClient.createPersistent("/zkClientP/zkClientP1",true);
        System.out.println("创建持久化节点成功");

        //删除节点
        zkClient.delete("/zkClientP/zkClientP1");
        System.out.println("持久化节点删除成功");

        //创建watcher（监听）
        zkClient.subscribeDataChanges("/zkClientP", new IZkDataListener() {
            //节点数据改变触发状态
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("节点数据更新");
            }
            //节点数据删除触发状态
            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("节点数据删除");

            }
        });
        zkClient.writeData("/zkClientP","zkClientP");
        //因为主线程是同步的，如果不等待可能看不到监听输出的结果
        TimeUnit.SECONDS.sleep(2);


        zkClient.subscribeChildChanges("/zkClientP", new IZkChildListener() {
            //节点数据改变触发状态
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                System.out.println("子节点数据改变");

            }
        })

    }
}
