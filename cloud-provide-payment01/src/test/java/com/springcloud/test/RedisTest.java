package com.springcloud.test;

import com.ctc.wstx.util.StringUtil;
import com.springcloud.pojo.CommonResult;
import com.springcloud.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testString(){
        String result = "";

//        result = redisUtil.get("2");
//        System.out.println(result);

        Long id = 2l;
        System.out.println(id.toString());
        String order = redisUtil.get(id.toString());
        System.out.println(order);
        if(order != null && !"null".equals(order)){

            System.out.println("111");
        }


    }

}
