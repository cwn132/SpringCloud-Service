package com.springcloud.zookeeper.lock;


import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


@Slf4j
public class ZkLock implements Lock{

    private String lockPath = "/zk-payment-lock";
    private String currentPath;

//    @Autowired
    private ZkClient zkClient;


    public ZkLock() {
        super();
    }

    @Override
    public void lock() {
        while (!tryLock()) {
            log.info("锁定当前节点");
            waitForLock();
        }
    }

    private void waitForLock() {
        // 创建countdownLatch协同
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 注册watcher监听
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String path, Object o) throws Exception {
                //System.out.println("zookeeper data has change!!!");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                // System.out.println("zookeeper data has delete!!!");
                // 监听到锁释放了，释放线程
                countDownLatch.countDown();
            }
        };
        zkClient.subscribeDataChanges(lockPath , listener);

        // 线程等待
        if (zkClient.exists(lockPath)) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 取消注册
        zkClient.unsubscribeDataChanges(lockPath , listener);

    }


    @Override
    public void unlock() {
        if (zkClient != null) {
            log.info("释放当前节点");
            zkClient.delete(lockPath);
        }
    }

    @Override
    public boolean tryLock() {
        try {

           zkClient.createEphemeral(lockPath);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
