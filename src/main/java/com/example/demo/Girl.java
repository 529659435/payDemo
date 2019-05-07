/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Girl
 * Author:   cj
 * Date:     2019/4/24 10:42
 * Description: 实例
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 〈一句话功能简述〉<br> 
 * 〈实例〉
 *
 * @author cj
 * @create 2019/4/24
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "girl")
public class Girl {
    private String name;
    private String cupSize;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCupSize() {
        return cupSize;
    }

    public void setCupSize(String cupSize) {
        this.cupSize = cupSize;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
