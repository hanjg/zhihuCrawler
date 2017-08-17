package com.crawl.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.crawl.config.Config;
import com.crawl.httpclient.ZhiHuHttpClient;
import com.crawl.util.HttpClientUtil;
import com.crawl.util.SimpleLogger;

/**
 * 模拟登陆知乎
 * @author hjg
 *
 */
public class ModelLogin {
    private static Logger logger = SimpleLogger.getSimpleLogger(ModelLogin.class);
    /**
     * 知乎首页
     */
	final public static String HOME_PAGE_URL = "https://www.zhihu.com";
    /**
     * 邮箱登陆地址
     */
    final public static String EMAIL_LOGIN_URL = "https://www.zhihu.com/login/email";
    /**
     * 手机登陆地址
     */
    final public static String PHONENUM_LOGIN_URL = "https://www.zhihu.com/login/phone_num";
    /**
     * 验证码下载地址
     */
    final public static String VERIFICATION_URL = "https://www.zhihu.com/captcha.gif?type=login";
    /**
     *
     * @param account 邮箱或手机号码
     * @param password 密码
     * @return
     */
    public boolean login(ZhiHuHttpClient zhiHuHttpClient,
    		String account, String password){
        CloseableHttpClient httpClient = zhiHuHttpClient.getCloseableHttpClient();
        HttpClientContext context = zhiHuHttpClient.getHttpClientContext();
        
        //设置post信息
        HttpPost httpPost=null;
        Map<String, String> parameters=new HashMap<>();
        if (account.contains("@")) {
        	httpPost=new HttpPost(EMAIL_LOGIN_URL);
        	parameters.put("email", account);
		}
        else {
			httpPost=new HttpPost(PHONENUM_LOGIN_URL);
			parameters.put("phone_num", account);
		}
        parameters.put("_xsrf", "");//这个参数可以不用
        parameters.put("password", password);
        parameters.put("remember_me", "true");
        String verificationCode = getVerificationCode(httpClient, context, VERIFICATION_URL);
        parameters.put("captcha", verificationCode);
        HttpClientUtil.setHttpPostParams(httpPost, parameters);
        
        //登录并且返回登录状态
        String loginState = HttpClientUtil.getResponseContent(
        		httpClient,context, httpPost, "utf-8", false);
        JSONObject jo = new JSONObject(loginState);
        if(jo.get("r").toString().equals("0")){
            logger.info("登录知乎成功");
            //序列化cookie:通过http上下文传递cookie，序列化至本地
            HttpClientUtil.serializeObject(context.getCookieStore(), Config.cookiePath);
            return true;
        }else{
            logger.info("登录知乎失败");
            logger.info(loginState);
            return false;
        }
    }
    /**
     * 肉眼识别验证码
     * @param httpClient 客户端
     * @param context 上下文
     * @param url 验证码地址
     * @return
     */
    public String getVerificationCode(CloseableHttpClient httpClient,HttpClientContext context, String url){
        String verificationCodePath = Config.verificationCodePath;
        String cataLogue = verificationCodePath.substring(0, verificationCodePath.lastIndexOf("/") + 1);
        String fileName = verificationCodePath.substring(verificationCodePath.lastIndexOf("/") + 1);
        HttpClientUtil.downloadFile(httpClient, context, url, cataLogue, fileName,true);
        logger.info("请输入 " + verificationCodePath + " 下的验证码：");
        String verificationCode=null;
        try(Scanner scanner = new Scanner(System.in)){
        	verificationCode = scanner.nextLine();
        }
        return verificationCode;
    }
}
