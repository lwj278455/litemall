package org.linlinjava.litemall.wx.util;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.codec.digest.DigestUtils;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PayUtil {
    @Autowired
    private LitemallUserService userService;

    private static final String TOKEN = "8576D2594AC607F4BF7AA89421046F36";
    /**
     * 获取随机字符串
     * @author Xuehao
     * @return
     * 2016年8月4日 上午9:26:07
     */
    public static String create_nonce_str() {
        String s = UUID.randomUUID().toString();
        // 去掉“-”符号
        return s.replaceAll( "\\-","").toUpperCase();
    }
    /**
     * 获取时间戳
     * @author Xuehao
     * @return
     * 2016年8月4日 上午9:26:20
     */
    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }


    /**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String generateSignature(Object o) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
                String name = f.getName();
                XStreamAlias anno = f.getAnnotation(XStreamAlias.class);
                if(anno != null)
                    name = anno.value();
                list.add(name + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + WxAuthorization.MCH_KEY;
        System.out.println("签名数据："+result);
        result = MD5Util.md5Encrypt32Upper(result);
        System.out.println("进入签名"+result);
        return result;
    }


    /**
     * 统一下单接口
     * @param price
     * @param userId
     * @param request
     * @return
     */
    public  Object pay(double price, Integer userId,String orderNo,HttpServletRequest request){
        LitemallUser user = userService.findById(userId);
        String productName="SweetCity";
        String openId = user.getWeixinOpenid();

        WxOrderEntity orderEntity = new WxOrderEntity();

        orderEntity.setAppid(WxAuthorization.APP_ID);
        orderEntity.setMch_id(WxAuthorization.MCH_ID);
        orderEntity.setBody(productName);
        orderEntity.setDevice_info("WEB");
        orderEntity.setTrade_type("JSAPI");
        orderEntity.setOut_trade_no(orderNo);
        orderEntity.setTotal_fee((int)(price*100));
        orderEntity.setOpenid(openId);
        //接收支付结果的地址
        orderEntity.setNotify_url(WxAuthorization.NOTIFY_URL);
        //32位随机数(UUID去掉-就是32位的)
        String uuid = UUID.randomUUID().toString().replace("-", "");
        orderEntity.setNonce_str(uuid);
        //生成签名
        String sign = null;
        try {
            sign = PayUtil.generateSignature(orderEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        orderEntity.setSign(sign);
        String xml = null;
        try {
            xml = WxAuthorization.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", orderEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Map map =null;
        try {
            map= XmlUtil.doXMLParse(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String  return_code = (String) map.get("return_code");
        if (!return_code.equals("SUCCESS")){
            xml="";
        }
        return xml;
    }

    /**
     * 验证签名
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {

        //与token 比较
        String[] arr = new String[] { TOKEN, timestamp, nonce };

        // 将token、timestamp、nonce三个参数进行字典排序
        Arrays.sort(arr);

        StringBuilder content = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }

        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");

            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将sha1加密后的字符串可与signature对比
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }
    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }
    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
