package org.linlinjava.litemall.wx.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;

import java.util.UUID;

/**
 * @program: bulk-security
 * @description: AlipayFunt
 **/
public class AlipayFund {
    public static int aliPay(String orderNo,String payeeAccount,String ampunt,String name) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.partner, AlipayConfig.merchant_private_key,"json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizContent("{" +
                "\"out_biz_no\":\""+orderNo+"\"," +  //订单号
                "\"payee_type\":\"ALIPAY_LOGONID\"," + //默认
                "\"payee_account\":\""+payeeAccount+"\"," + //支付宝号
                "\"amount\":\""+ampunt+"\"," +     //金额
                "\"payee_real_name\":\""+name+"\"," + //支付宝名字
                "\"remark\":\"转账\"" +
                "  }");
        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
            return 1;
        } else {
            System.out.println("调用失败");
            return 0;
        }
    }

    public static void main(String[] args) throws AlipayApiException {
        int num=aliPay("","2784552501@qq.com","0.1","刘文杰");
        if(num==1){
            System.out.println(1);
        }else{
            System.out.println(0);
        }

    }
}
