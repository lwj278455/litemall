package org.linlinjava.litemall.wx;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.wx.util.WxAuthorization;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class T {
    public static String accToken = "21_0CCWI9oDu3LignaE96B1d6R0gdSLaSLGpnl03a-JpaQoqudNWkZUiDavbKN8_j9CWWNJkl-24o3249nZdw4WtpmjrIl7ZV1s1QMtB1SNFAr3ypjN-R0kY5QWM_QpG0P5gE3JisYaam91ZIKGJSXcAIAENS";
    @Autowired
    private WxAuthorization wxAuthorization;
    private static CloseableHttpClient CLIENT;
    static {
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setMaxConnTotal(1000);
            httpClientBuilder.setMaxConnPerRoute(100);
            httpClientBuilder.evictIdleConnections(15, TimeUnit.SECONDS);
            SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
            socketConfigBuilder.setTcpNoDelay(true);
            httpClientBuilder.setDefaultSocketConfig(socketConfigBuilder.build());
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder.setConnectTimeout(3000);
            requestConfigBuilder.setSocketTimeout(3000);
            httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new EasyX509TrustManager();
            ctx.init(null, new TrustManager[]{tm}, null);
            httpClientBuilder.setSSLContext(ctx);
            CLIENT = httpClientBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getqrCode() throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+accToken;
        String jsonParam = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": "+1+"}}}";
        String s = doPostWithJson(url, jsonParam);
        System.out.println("s======:"+s);
    }
    public static String doPostWithJson(String url, String context) throws Exception {

        HttpPost post = new HttpPost(url);
        HttpEntity requestEntity = new StringEntity(context, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        return execute(post);
    }
    private static String execute(HttpUriRequest request) throws Exception {
        CloseableHttpResponse response = CLIENT.execute(request);
        if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
            throw new Exception("Invalid http status code:" + response.getStatusLine().getStatusCode());
        }
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }
}
