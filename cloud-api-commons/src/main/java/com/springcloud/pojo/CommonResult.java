package com.springcloud.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data  //注在类上，提供类的get、set、equals、hashCode、toString等方法
@NoArgsConstructor //注在类上，提供类的无参构造
@AllArgsConstructor //注在类上，提供类的全参构造
@Accessors(chain = true)  //用于配置getter和setter方法的生成结果, chain的中文含义是链式的，设置为true，则setter方法返回当前对象
public class CommonResult<T> {

    private Integer code; //返回状态码
    private String message; //返回是否调用成功
    private T data; //返回的数据

    public CommonResult(Integer code, String message) {
        this(code,message,null);
    }
}

