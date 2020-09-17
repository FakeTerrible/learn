package test;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import okhttp3.*;
import util.SSLSocketClientUtil;
import javax.net.ssl.X509TrustManager;

public class Test {
    private static final OkHttpClient okHttpClient;

    static {
        // 支持https请求，绕过验证
        X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
        okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)// 忽略校验
                .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())//忽略校验
                .build();
    }

    public String postContent(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                .addHeader("Host","douban.fm")
                .build();

        // 返回结果字符串
        String result = null;
        try {
            result = okHttpClient.newCall(request).execute().body().string();

        } catch (IOException e) {
            System.out.println("request " + url + " error . ");
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        // 登录页面 url
        String url = "https://douban.fm/j/v2/rec_channels?specific=all";


        Test asker = new Test();
        String content = asker.postContent(url);

        Map one = JSON.parseObject(content,Map.class);
        Map Data = (Map)one.get("data");
        Map Channels = (Map)Data.get("channels");
        List Artist = (List)Channels.get("artist");
        for(int i = 0; i < Artist.size(); i++){
            Map source = (Map)Artist.get(i);
            System.out.println(source.get("id"));
            System.out.println(source.get("name"));
            List rd = (List)source.get("related_artists");
            for(int j = 0; j < rd.size(); j++){
                Map rda = (Map)rd.get(j);
                System.out.println(rda.get("id"));
                System.out.println(rda.get("name"));
            }
            System.out.println();
        }
    }
}