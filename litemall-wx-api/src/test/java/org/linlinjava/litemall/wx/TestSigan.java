package org.linlinjava.litemall.wx;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qiniu.util.Json;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import net.sf.json.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.linlinjava.litemall.wx.util.MD5Util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.*;
import java.util.*;

public class TestSigan {
    public static final String WECHAT_MACHID_KEY = "0gigl5rowc7ah2157j254pgdaebs55k5";

    //连接超时时间，默认10秒
    private static final int socketTimeout = 10000;

    //传输超时时间，默认30秒
    private static final int connectTimeout = 30000;

    /**
     * 实体类签名算法
     *
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Object o) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
                String name = f.getName();
                XStreamAlias anno = f.getAnnotation(XStreamAlias.class);
                if (anno != null)
                    name = anno.value();
                list.add(name + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + WECHAT_MACHID_KEY;
        System.out.println("签名数据：" + result);
        result = MD5Util.md5Encrypt32Upper(result);
        return result;
    }

    /**
     * json方式的post请求
     *
     * @param url
     * @param jsonObject
     * @return
     * @throws IOException
     */
    public static Object doPost(String url, String jsonObject) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        String response = null;

        try {
            StringEntity s = new StringEntity(jsonObject);
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                String result = EntityUtils.toString(entity);
                response = result;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }


    /**
     * xml参数的post请求
     *
     * @param url
     * @param xmlObj
     * @return
     * @throws IOException
     */
    public static String sendPost(String url, Object xmlObj) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        xStreamForRequestPostData.alias("xml", xmlObj.getClass());
        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);
        System.out.println("postDataXml====" + postDataXML);
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

    /**
     * map签名
     *
     * @param parameters
     * @return
     */
    public static String createSign(SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + WECHAT_MACHID_KEY);//这里是商户那里设置的key
        System.out.println("签名字符串:" + sb.toString());
        String sign = md5Password(sb.toString()).toUpperCase();
        return sign;
    }

    /**
     * md5加密
     *
     * @param key
     * @return
     */
    public static String md5Password(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 云代付
     *
     * @param
     */
    @Test
    public void yunDaiFu() {
        SortedMap<String, Object> map = new TreeMap<>();
        map.put("appid", "7d5e04aa-2488-4604-963e-d7ce20b36059"); //绑定appid
        map.put("channel", "wx");   //绑定付款类型
        map.put("order_no", "A509727867558287"); //绑定订单号
        map.put("amount", "0.01");  //绑定金额
        map.put("description", "123");  //绑定商品详情
        map.put("recipient_openid", "");    //绑定用户openid
        String sign = createSign(map);  //签名
        System.out.println(sign);
        map.put("sign", sign); //绑定签名
        JSONObject json = new JSONObject(map);  //把map 转成json
        System.out.println("json    =====" + json);
        Object s = doPost("http://api.yundaifu.com/api/api/withdraw", json.toString()); //发送请求
        System.out.println(s);
    }


    /**
     * 免签支付
     *
     * @param
     */
    @Test
    public void MianQian() {
        SortedMap<String, Object> map = new TreeMap<>();
        map.put("appid", "fe80984a-3f8b-4032-93bf-18ba9e3d05b9");  // 商户appid
        map.put("order_no", "A15165564684615"); //商户订单号
        map.put("url", "http://%77%77%77%2E%78%78%78%6B%65%6A%69%2E%63%6F%6D/"); //支付回调地址
        map.put("return_url", "http://%77%77%77%2E%78%78%78%6B%65%6A%69%2E%63%6F%6D/"); //支付成功跳转地址
        map.put("remark", "123"); //详情信息
        map.put("amount", "2"); //金额
        map.put("type", "2"); //支付类型： 2 微信商户
        map.put("code_type", "0"); //二维码类型 0.固定金额

        System.out.println();
        String sign = createSign(map);  //签名

        map.put("sign", sign);

        JSONObject json = new JSONObject(map);  //把map 转成json

        System.out.println("json=====" + json);
        Object s = doPost("http://hrbhgbj.cn?freepay&route=/api/api/pay", json.toString()); //发送请求

        System.out.println(s);
    }
}
