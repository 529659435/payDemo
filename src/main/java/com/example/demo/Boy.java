/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Boy
 * Author:   cj
 * Date:     2019/4/24 11:20
 * Description: 测试数据库建表
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 * 〈一句话功能简述〉<br> 
 * 〈测试数据库建表〉
 *
 * @author cj
 * @create 2019/4/24
 * @since 1.0.0
 */

@Entity
public class Boy {

    @Id
    @GeneratedValue
    private  Integer id;
    private  String name;
    private  String sex;


    public Boy() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
