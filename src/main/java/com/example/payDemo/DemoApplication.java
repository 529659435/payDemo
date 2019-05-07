package com.example.payDemo;

import com.example.bean.ConfigFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableSwagger2
@ComponentScan(basePackages = { "com" })
public class DemoApplication {


    public static void main(String[] args) {
        System.out.println("main();");
        SpringApplication.run(DemoApplication.class, args);
    }



}

