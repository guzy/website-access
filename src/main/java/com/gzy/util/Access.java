package com.gzy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

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

@Component
public class Access {

    private static final Logger LOG = LogManager.getLogger();

    public void access(List<String> inputWebsite) {
        ExecutorService executorsService = Executors.newFixedThreadPool(3);
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        for (final String website : inputWebsite) {
            executorsService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        doAccess(website);
                    } catch (Throwable e) {
//                        LOG.catching(Level.ERROR, e);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        try {
            if (!countDownLatch.await(2000, TimeUnit.MILLISECONDS)) {
                LOG.warn("website accessing may not have finished. {}", 2000);
            }
        } catch (InterruptedException e) {
            LOG.error("website accessing internal error.", e);
        }
        executorsService.shutdownNow();
    }

    public void doAccess(String website) {
        try {
            URL url = new URL(website);
            long startTime = System.currentTimeMillis();
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.connect();
            long duration = System.currentTimeMillis() - startTime;
            int responseCode = huc.getResponseCode();
            LOG.info("Access {} : ResponseCode is {}, ResponseTime is {}",
                    website, responseCode, duration);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
