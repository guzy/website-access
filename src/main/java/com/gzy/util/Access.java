package com.gzy.util;

import org.apache.logging.log4j.Level;
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
    private static final int TIMEOUT_MS = 2000;

    public static void main(String[] args) {
        List<String> inputWebsite = Arrays.asList(new String[]{"http://www.gzy.com", "http://www.taobao.com", "http://www.qq.com"});
        new Access().access(inputWebsite);
    }

    public void access(List<String> inputWebsite) {
        ExecutorService executorsService = Executors.newFixedThreadPool(inputWebsite.size());
        final CountDownLatch countDownLatch = new CountDownLatch(inputWebsite.size());
        for (final String website : inputWebsite) {
            executorsService.submit(() -> {
                try {
                    doAccess(website);
                } catch (Throwable e) {
                    LOG.catching(Level.ERROR, e);
                }
                countDownLatch.countDown();
            });
        }
        try {
            if (!countDownLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                LOG.warn("website accessing may not have finished, " +
                        "exceed the threshold {} ms.", TIMEOUT_MS);
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
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MS);
            urlConnection.connect();
            long duration = System.currentTimeMillis() - startTime;
            int responseCode = urlConnection.getResponseCode();
            LOG.info("Access {} : ResponseCode is {}, ResponseTime is {} ms.",
                    website, responseCode, duration);
        } catch (MalformedURLException e) {
            LOG.catching(Level.ERROR, e);
        } catch (IOException e) {
            LOG.catching(Level.ERROR, e);
        }
    }
}
