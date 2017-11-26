package com.gzy.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class Access {

    private static final Logger LOG = LogManager.getLogger();

    @Value("${exam.websiteList}")
    private String websiteList;
    @Value("${exam.timeoutMilliSecond}")
    private int timeoutMilliSecond;
    @Value("${exam.accessCount}")
    private int accessCount;

    @RequestMapping(value = "/exam")
    public void access() {
        List<String> inputWebsite = Arrays.asList(websiteList.split(","));
        ExecutorService executorsService = Executors.newFixedThreadPool(inputWebsite.size());
        final CountDownLatch countDownLatch = new CountDownLatch(inputWebsite.size());
        for (final String website : inputWebsite) {
            executorsService.submit(() -> {
                try {
                    for (int count = 0; count < accessCount; count++) {
                        long startTime = System.currentTimeMillis();
                        int responseCode = doAccess(website);
                        long duration = System.currentTimeMillis() - startTime;
                        LOG.info("Access {} : ResponseCode is {}, ResponseTime is {} ms.",
                                website, responseCode, duration);
                    }
                } catch (Throwable e) {
                    LOG.catching(Level.ERROR, e);
                }
                countDownLatch.countDown();
            });
        }
        try {
            if (!countDownLatch.await(timeoutMilliSecond, TimeUnit.MILLISECONDS)) {
                LOG.warn("website accessing may not have finished, " +
                        "exceed the threshold {} ms.", timeoutMilliSecond);
            }
        } catch (InterruptedException e) {
            LOG.error("website accessing internal error.", e);
        }
        executorsService.shutdownNow();
    }

    /**
     * @param website
     * @return ResponseCode
     */
    public int doAccess(String website) {
        try {
            URL url = new URL(website);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(timeoutMilliSecond);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            return responseCode;
        } catch (MalformedURLException e) {
            LOG.catching(Level.ERROR, e);
            return -1;
        } catch (IOException e) {
            LOG.catching(Level.ERROR, e);
            return -1;
        }
    }
}
