package com.zookeeper.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class CuratorTest {

    @Autowired
    CuratorFramework curatorFramework;

    @Test
    public void createNode() throws Exception {
        //添加持久节点
        String path = curatorFramework.create().forPath("/curator-node");
//        //添加临时序号节点
//        String path = curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator-node","some-data".getBytes());
        log.info(String.format("curator create node :%s successfully.",path));
        //阻塞线程等待读取zookeeper方法 保持连接
//        System.in.read();
    }

    @Test
    public void testGetData() throws Exception {
        //查询节点数据
        byte[] bytes = curatorFramework.getData().forPath("/zk-payment");
        log.info(new String(bytes));
        System.out.println("Zookeeper节点:"+new String(bytes));
    }

    @Test
    public void testSetData() throws Exception {
        //设置zNode节点的数据
        curatorFramework.setData().forPath("/curator-node","changed".getBytes());
        byte[] bytes = curatorFramework.getData().forPath("/curator-node");
        log.info(new String(bytes));
    }

    @Test
    public void testCreateWithParent() throws Exception {
        //建立节点 直到指定路径节点全部建完
        String pathWithParent= "/node-parent/sub-node-1";
        String path = curatorFramework.create().creatingParentsIfNeeded().forPath(pathWithParent);
        log.info(String.format("curator create node :%s successfullyy",path));
    }

    @Test
    public void testDelete() throws Exception {
        //进行删除节点(包括子节点)
        String pathWithParent = "/node-parent";
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(pathWithParent);
    }

   //获取读锁
    @Test
    public void testGetReadLock() throws Exception {
        //读写锁   初始化参数 连接对象，需要加锁的路径
        InterProcessReadWriteLock readWriteLock =
                new InterProcessReadWriteLock(curatorFramework,"/curator-node");
        //获取读锁对象
        InterProcessLock interProcessLock = readWriteLock.readLock();
        log.info("等待获取读锁对象！");
        //获取锁   //没获取到锁之前会进入阻塞
        interProcessLock.acquire();
        //已经获取到读锁了，这下面制造堵塞（不释放锁 测试用）
        for (int i = 0; i < 100; i++) {
            Thread.sleep(3000);
            log.info("{}",i);
        }
        //释放锁
        interProcessLock.release();
        log.info("============锁已经释放了");
    }


    //获取写锁
    @Test
    public void testGetWriteLock() throws Exception {
        //读写锁
        InterProcessReadWriteLock readWriteLock =
                new InterProcessReadWriteLock(curatorFramework,"/curator-node");
        //获取写锁对象
        InterProcessLock interProcessLock = readWriteLock.writeLock();
        log.info("等待获取写锁中");
        //获取锁
        interProcessLock.acquire();
        for (int i = 0; i < 100; i++) {
            log.info("{}",i);
            Thread.sleep(3000);
        }
        //释放锁
        interProcessLock.release();
        log.info("锁已经释放成功");
    }






}
