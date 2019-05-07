package com.example.demo;

import com.example.payDemo.DemoApplication;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
        DemoApplication application = new DemoApplication();
        System.out.println("Hello World");
    }

    @Test
    public void co() {
        Map map = new HashMap();
        map.put("name", "json");
        map.put("bool", Boolean.TRUE);
        map.put("int", new Integer(1));
        map.put("arr", new String[] { "a", "b" });
        JSONObject json = JSONObject.fromObject(map);
        System.out.println(json.toString());
    }
}
