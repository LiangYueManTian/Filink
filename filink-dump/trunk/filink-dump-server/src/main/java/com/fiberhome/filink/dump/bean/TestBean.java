package com.fiberhome.filink.dump.bean;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yuanyao@wistronits.com
 * create on 2019-05-29 17:54
 */
@Document
public class TestBean {


    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
