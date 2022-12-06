package com.springcloud.test;

import com.springcloud.generator.OrderCodeGenerator;
import com.springcloud.zookeeper.lock.ZkLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class zookeeperTest extends Thread{

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private OrderCodeGenerator orderCodeGenerator;

    public void startCodeGenerator(){
        try {
            zkLock.lock();
            //新建订单编号
            String orderSerial =orderCodeGenerator.generatorOrderCode();
            System.out.println("orderSerial:"+orderSerial);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            zkLock.unlock();
        }
    }

    @Test
    public void TestMain(){

        Runnable runnable = () -> {
            try {
                zkLock.lock();
                //新建订单编号
                String orderSerial =orderCodeGenerator.generatorOrderCode();
                System.out.println("orderSerial:"+orderSerial);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }finally {
                zkLock.unlock();
            }
        };

        for (int i = 0; i < 20; i++) {
            Thread t1 = new Thread(runnable, "t1");
            Thread t2 = new Thread(runnable, "t2");

            t1.run();
            System.out.println("t1 线程:"+t1.getState());
            t2.run();
            System.out.println("t2 线程:"+t2.getState());
        }




    }


    //模拟并发请求
    @Test
    public void TestComplicate() throws InterruptedException {
        Runnable taskTemp = new Runnable() {

            private int iCounter;
            @Override
            public void run() {
                for(int i = 0; i < 10; i++) {

                    //请求
                    startCodeGenerator();

                    iCounter++;
                    System.out.println(System.nanoTime() + " [" + Thread.currentThread().getName() + "] iCounter = " + iCounter);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        startTaskAllInOnce(10, taskTemp);
    }

    public long startTaskAllInOnce(int threadNums, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threadNums);
        for(int i = 0; i < threadNums; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        // 使线程在此等待，当开始门打开时，一起涌入门中
                        startGate.await();
                        try {
                            task.run();
                        } finally {
                            // 将结束门减1，减到0时，就可以开启结束门了
                            endGate.countDown();
                        }
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            };
            t.start();
        }
        long startTime = System.nanoTime();
        System.out.println(startTime + " [" + Thread.currentThread() + "] All thread is ready, concurrent going...");
        // 因开启门只需一个开关，所以立马就开启开始门
        startGate.countDown();
        // 等等结束门开启
        endGate.await();
        long endTime = System.nanoTime();
        System.out.println(endTime + " [" + Thread.currentThread() + "] All thread is completed.");
        return endTime - startTime;
    }

}
