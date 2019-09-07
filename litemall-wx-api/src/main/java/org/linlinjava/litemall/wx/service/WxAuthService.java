package org.linlinjava.litemall.wx.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
@Service
public class WxAuthService {

    @Value("${litemall.wx.app-id}")
    String oppenId = null;//
    public Object Base64Analysis(HttpServletRequest request,
                                 HttpServletResponse response){

        try {
            String redirectUriEncoder = URLEncoder.encode("https://www.xxxkeji.com/shop/index","UTF-8");
            WxMaJscode2SessionResult result = new WxMaJscode2SessionResult();
            String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+oppenId+"&redirect_uri="+redirectUriEncoder+"" +
                    "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
            response.sendRedirect(url);

            return url;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseUtil.serious();
    }
}
