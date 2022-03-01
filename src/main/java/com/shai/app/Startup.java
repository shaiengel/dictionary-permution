package com.shai.app;

import com.shai.caching.CacheItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages={"com.shai"})
public class Startup {

    @Autowired
    CacheItems cacheItems;

    public static void main(String[] args) {
        SpringApplication.run(Startup.class, args);
    }

    @PostConstruct
    public void start() throws InterruptedException {

        cacheItems.readFile();
    }
}

