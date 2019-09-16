package org.linlinjava.litemall.wx.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.wx.dto.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 *
 * 获取微信的登录的信息
 *
 */
@Component
public class WxAuthorization {

    //连接超时时间，默认10秒
    private static final int socketTimeout = 10000;
    //传输超时时间，默认30秒
    private static final int connectTimeout = 30000;
    //获取access_token的url
    private static final String BASE_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    //固定值
    private static final String GRANT_TYPE = "authorization_code";
    //自己的AppID
    public static String APP_ID;
    //自己的AppSecret
    public static String SECRET ;
    //商户号
    public static String MCH_ID;
    //商户秘钥
    public static String MCH_KEY;
    //回调地址
    public static String  NOTIFY_URL;
    @Value("${litemall.wx.notify-url}")
    public  void setNotifyUrl(String notifyUrl) {
         this.NOTIFY_URL = notifyUrl;
    }
    @Value("${litemall.wx.mch-key}")
    public  void setKEY(String KEY) {
        this.MCH_KEY = KEY;
    }

    @Value("${litemall.wx.mch-id}")
    public  void setMchId(String mchId) {
        MCH_ID = mchId;
    }

    @Value("${litemall.wx.app-id}")
    public void setAPP_ID(String APP_ID) {
        this.APP_ID = APP_ID;
    }
    @Value("${litemall.wx.app-secret}")
    public void setSECRET(String SECRET) {
        this.SECRET = SECRET;
    }

    /**
     * 登录请求
     * @param code
     * @return
     */
    public String getAccess_token(String code) {
        String url = BASE_TOKEN_URL+"appid="+APP_ID+"&secret="+SECRET+"&code="+code+"&grant_type="+GRANT_TYPE;//
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        String responseStr=null;
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                responseStr= EntityUtils.toString(responseEntity);
                System.out.println("get请求返回的response值：" + responseStr);
                return responseStr;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


//    private EmojiConverter emojiConverter = EmojiConverter.getInstance();

    /**
     * 获取用户信息
     * @param access_token
     * @param appid
     * @return
     */
    public UserInfo getUserInfo(String access_token,String appid) {
        String url ="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+appid+"&lang=zh_CN";
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        String responseStr=null;
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                responseStr= EntityUtils.toString(responseEntity);
                System.out.println("get请求返回的response值：" + responseStr);
                String openid = JacksonUtil.parseString(responseStr,"openid");
                String nickname = JacksonUtil.parseString(responseStr,"nickname");
                String headimgurl = JacksonUtil.parseString(responseStr,"headimgurl");
                String country = JacksonUtil.parseString(responseStr,"country");
                String province = JacksonUtil.parseString(responseStr,"province");
                String city = JacksonUtil.parseString(responseStr,"city");
                String unionid = JacksonUtil.parseString(responseStr,"unionid");
                Byte sex = JacksonUtil.parseByte(responseStr,"sex");
                UserInfo userInfo = new UserInfo(openid,headimgurl,country,province,city,unionid,sex);
                String name = new String(nickname.getBytes("ISO-8859-1"),"utf-8");
                try {
                    userInfo.setNickName(Base64.encodeBase64String(name.getBytes("utf-8")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return userInfo;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static HttpResponse  doPostWithJson(String url) throws Exception {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpPost httpPost = new HttpPost(url);
        // 响应模型
        CloseableHttpResponse response = null;
        // 由客户端执行(发送)Get请求
        response = httpClient.execute(httpPost);
        return  response;
    }



    public static String sendPost(String url, Object xmlObj) throws ClientProtocolException, IOException, UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        HttpPost httpPost = new HttpPost(url);
        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        xStreamForRequestPostData.alias("xml", xmlObj.getClass());
        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);

        //设置请求器的配置
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        return result;
    }
}
