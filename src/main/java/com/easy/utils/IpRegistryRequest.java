package com.easy.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IpRegistryRequest {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String URL = "https://api.ipregistry.co/?hostname=true&key=ira_YgGPCzMLCfgWGZyEas0oqjUC6N6jWs329qSu";

    // 定义要发送的请求头
    private static final String[] HEADERS = {
            "accept:*/*",
            "accept-language:zh-CN,zh;q=0.9",
            "origin:https://ipregistry.co",
            "priority:u=1, i",
            "referer:https://ipregistry.co/",
            "sec-ch-ua:\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
            "sec-ch-ua-mobile:?0",
            "sec-ch-ua-platform:\"Windows\"",
            "sec-fetch-dest:empty",
            "sec-fetch-mode:cors",
            "sec-fetch-site:same-site",
            "user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/131.0.0.0 Safari/537.36"
    };

    public static void main(String[] args) {
        // 定义线程池的大小，根据需要调整
        int numberOfThreads = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 提交多个任务到线程池
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    test();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        // 关闭线程池
        executorService.shutdown();
        try {
            // 等待所有任务完成，最多等待60秒
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // 定义 test 方法，执行 HTTP 请求
    public static void test() throws IOException {
        // 构建请求
        Request.Builder requestBuilder = new Request.Builder()
                .url(URL)
                .get();

        // 添加所有请求头
        for (String header : HEADERS) {
            String[] parts = header.split(":", 2);
            if (parts.length == 2) {
                requestBuilder.addHeader(parts[0], parts[1]);
            }
        }

        Request request = requestBuilder.build();

        // 执行请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("请求失败: " + response);
                return;
            }

            // 获取响应体
            String responseBody = response.body().string();

            // 使用 fastjson2 解析 JSON 响应
            JSONObject json = JSON.parseObject(responseBody);
            System.out.println("线程 " + Thread.currentThread().getName() + " 响应内容:" + json.toJSONString());
        } catch (IOException e) {
            System.err.println("请求异常: " + e.getMessage());
            throw e;
        }
    }
}
