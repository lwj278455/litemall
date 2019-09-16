package org.linlinjava.litemall.wx;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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


    @Test
    public void getqrCode() throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxb225ef8edffc7fde&secret=03ba968fb5e9b97a9d1bf3523112d733";
        HttpGet httpGet = new HttpGet(url);
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response =  httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        System.out.println(responseEntity);
    }

}
