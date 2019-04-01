package com.zhouxu.study.zookeeper.zkClient;

import org.I0Itec.zkclient.ZkClient;

public class SessionDemo {

    private static final String  CONNECTURLS = "192.168.0.102:2181,192.168.0.104:2181,192.168.0.105:2181";

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient(CONNECTURLS,50000);
        System.out.println(zkClient+"连接成功");
    }
}
