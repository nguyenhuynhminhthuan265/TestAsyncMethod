package com.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class AppRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    GitHubLookupService gitHubLookupService;


    @PostConstruct
    public void run() {
        long start = System.currentTimeMillis();


        try {
            // Kick of multiple, asynchronous lookups
            CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
            CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
            CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");
            CompletableFuture<String> image = gitHubLookupService.loadImage();

//        Wait until they are done
//            CompletableFuture.allOf(page1, page2, page3).join();
            logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
            logger.info("--> " + page1.get());
            logger.info("--> " + page2.get());
            logger.info("--> " + page3.get());
            logger.info("--> " + image.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }
}
