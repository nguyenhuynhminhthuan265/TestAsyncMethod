package com.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@SpringBootApplication(scanBasePackages = "com")
@EnableAsync
@RestController
public class AsyncMethodApplication {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    GitHubLookupService gitHubLookupService;

    public static void main(String[] args) {
        SpringApplication.run(AsyncMethodApplication.class, args);
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        long start = System.currentTimeMillis();
        String res = "";

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

            res += "--> " + page1.get() + "\n--> " + page2.get() + "\n--> " + page3.get() + "\n--> " + image.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return res;

    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }


}
