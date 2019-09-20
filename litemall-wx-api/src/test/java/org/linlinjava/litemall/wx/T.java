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
import org.linlinjava.litemall.db.domain.LitemallUserRelations;
import org.linlinjava.litemall.db.service.LitemallUserRelationsService;
import org.linlinjava.litemall.wx.util.WxAuthorization;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class T {
    public static String accToken = "21_0CCWI9oDu3LignaE96B1d6R0gdSLaSLGpnl03a-JpaQoqudNWkZUiDavbKN8_j9CWWNJkl-24o3249nZdw4WtpmjrIl7ZV1s1QMtB1SNFAr3ypjN-R0kY5QWM_QpG0P5gE3JisYaam91ZIKGJSXcAIAENS";
    @Autowired
    private LitemallUserRelationsService userRelationsService;

    @Test
    public void getqrCode() throws Exception {
        System.out.println("加盟服务合同\\r\\n（AI旗舰店）\\r\\n\\r\\n\\r\\n甲 方: 河南创尖科技有限公司 \\r\\n乙 方（加盟方）： 用户（客户）\\r\\n\\r\\n各条款标题仅为帮助各方理解该条款表达的主旨之用，不影响或限制本协议条款的含义或解释。\\r\\n审慎阅读\\r\\n乙方在操作流程中点击同意本协议之前，应当认真阅读本协议，务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款、法律适用和争议解决条款。\\r\\n签约动作\\r\\n阅读本协议的过程中，如果乙方不同意本协议或其中任何条款约定，乙方应立即停止接下来的步骤。\\r\\n乙方按照页面提示填写信息、阅读并同意本协议后，即表示乙方已充分阅读、理解并接受本协议的全部内容，且与甲方达成一致意见。 \\r\\n为扩大微达共享智能品牌产品市场占有率、维护公司品牌形象、加强和规范市场管理、保障共同利益，在平等互利的基础上，甲乙双方根据《中华人民共和国合同法》及相关的法律法规，本着精诚合作、共同发展、共同成长的原则，经甲乙双方充分友好共同协商，在明确权、责、利的基础上，就经营产品、市场支持及售后服务等事宜，达成如下协议：\\r\\n一、 加盟授权\\r\\n1.1甲方授权乙方为创尖科技（抖一斗）全国连锁的加盟推广商，乙方必须拥有工商个体或有限公司法人资格，乙方同意按照本合同的约定进行推广。甲方系物联网服务平台，旨在通过智能综合技术服务获取用户与收益，乙方加盟我公司的AI旗舰物联店，（乙方拥有31家尚店物联分享资格），乙方需向甲方一次性支付加盟服务费90000元，大写 九万元整。 \\r\\n1.2 本协议有效期 1 年，点击“阅读并同意”签署合同，进入统一管理系统方可有效。\\r\\n1.3乙方自签订合同之日起，乙方拥有 31家AI旗舰物联的推广资格，一年内完成该目标，自动续约，再获得31家旗舰物联店推广资格；否则，再获得31家旗舰物联店推广资格，须向甲方支付90000元。\\r\\n二、 打款及推广\\r\\n2.1 名称：AI旗舰物联店 ，推广资格： 31家。\\r\\n甲方向乙方提供旗舰物联店系统推广资格。\\r\\n2.2 方式：款到后开通端口，款必须汇到甲方所指定账号，一经确认，不得退款。\\r\\n2.3 乙方的推广流程必须严格执行甲方规定的推广模式 。\\r\\n三、权益\\r\\n3.1 在合同有效期内，乙方推广分享体验官，获得相应分享服务费；推广尚店物联、AI社区体验店、AI旗舰店获得相应招商佣金。\\r\\n3.2 在推广过程中，乙方享受甲方提供的《销讲》课，《讲师》课，《领导力》课等。\\r\\n3.3甲方赠送乙方九万元的期权优先认购资格。\\r\\n3.4甲方赠送乙方九万抖豆，用于在商城兑换产品。\\r\\n四、售后服务\\r\\n4.1 所有甲方公司产品，甲方都将依据国家颁布的相关法律、法规，提供产品的三包服务。\\r\\n4.2 具体维修配件收费标准，见附件甲方售后维修服务标准。\\r\\n五、知识产权、品牌形象维护及保密规定\\r\\n5.1 乙方未经授权不得使用创尖科技的商标或相近的商标或商号。\\r\\n5.2 乙方不得，也不得授权第三人做有损于创尖科技知识产权与品牌形象的事情；若发现创尖科技的知识产权与品牌形象可能受损或已经发生受损情况的，乙方应及时通知并配合甲方的工作。\\r\\n5.3 甲方将对乙方提供不同程度的内部信息，乙方应该竭力保守秘密，不得私自向他人泄密。\\r\\n六、双方权利和义务\\r\\n6.1 议有效期内，甲方保证乙方正常推广权益，若（一、加盟授权，二、打款及推广，三、权益）有变动或调整，甲方会提前通知乙方，解释权归甲方所有。\\r\\n\\r\\n6.2 乙方不得利用获得甲方授权推广商的身份，以创尖科技的名义或利用其它方式进行非法的集资融资传销等不符合国家法律法规的行为，否则由此产生的一切后果由乙方负责，甲方不承担任何责任。\\r\\n6.3 乙方必须严格遵守本协议规定的推广方式，如有违反，经甲方调查核实，甲方有权按市场管理规定中的相关条款进行处罚。\\r\\n6.4 乙方经营必须符合工商规范，未经甲方授权不得在任何场所以任何方式冒用创尖科技智能名义进行非法经营。\\r\\n6.5 如因协议解除，乙方代理推广资格失效，乙方所推荐的分享体验官、尚店物联、社区物联店、AI旗舰店等利益分配关系同时解除。\\r\\n6.6 乙方有义务自觉维护创尖科技智能及产品的形象和声誉并在甲方指导下做好消费者投诉、产品质量问题及有关部门监督检查的配合工作。\\r\\n6.7 甲方有权通知乙方，对产品的市场推广策略进行调整，对乙方进行的宣传推广进行指导协助。\\r\\n6.8乙方有义务配合甲方在全国范围内展开的促销活动，达到资源共享，共同促进产品销售市场的成长。\\r\\n6.9甲方承诺：在与乙方建立长期合作运营的基础上，若乙方达到合伙人或联合发起人资格，甲方给予乙方的利润回报包括甲方整个平台技术服务的硬件、软件、广告、商城及按照绩效分红。\\r\\n七、协议的变更、续期及解除\\r\\n7.1本协议有效期1年，参考1.3条款，双方无异议，自动续约。\\r\\n7.2 乙方推广假冒产品或推广非甲方提供的同类产品，甲方有权终止本协议并保留对乙方法律追究的权利。\\r\\n7.3 若一方出现重大违约行为，同时违约方超过30天不能承担违约责任时，另一方有权终止协议并只需以书面通知违约方。本协议终止，不影响违约方应承担的违约责任。\\r\\n八、违约责任\\r\\n1、乙方有下列行为之一的或违反甲方营运细则及规章制度的，甲方有权根据乙方违约行为，采取以下相应措施:终止使用协议、加盟服务费不予退还；不再对乙方承担任何义务和责任：（1）乙方提供虚假注册资料；（2）乙方违反本协议规定的内容；（3）通过非法手段、不正当手段或其它不公平手段获得加盟资格；（4）有损害甲方正当利益行为的；（5）有严重损害其他推广者权益行为的；（6）有违反中华人民共和国的法律、法规、行政命令的行为或言论；（7）有违反社会风俗、社会道德和互联网一般道德和礼仪行为的；（8）其他妨碍甲方提供推广服务或甲方认为属于严重不当行为的；    \\r\\n2、乙方应维护甲方的利益.如因乙方违反有关法律、法规或本协议项下的任何条款而给甲方或任何其他第三方造成损失，乙方同意承担由此产生的一切害赔偿责任。\\r\\n3、如因此给我方造成不利后果的，您应负责消除影响，并且赔偿我方因此导致的一切损失，包括且不限于财产损害赔偿、名誉损害赔偿、律师费、交通费等因维权而产生的合理费用或其他所有平台要求的合理费用。\\r\\n九、其他规定\\r\\n9.1本协议中的任一条款因任何原因完全或部分无效或不具有执行力，  \\r\\n不影响其他条款的法律效力和执行； \\r\\n9.2双方就本协议内容或其执行中发生争议，双方应友好协商解决；协商不成时，双方同意由甲方所在地的法院管辖。\\r\\n9.3签署本协议前，甲方就协议内容向乙方作了充分说明和解释，乙方已全面了解和认真考虑后同意签订本协议。若对本协议内容有任何争议，最终解释权归属于甲方。\\r\\n");
    }

}
