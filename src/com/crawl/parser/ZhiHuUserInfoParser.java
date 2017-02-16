package com.crawl.parser;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.crawl.entity.Page;
import com.crawl.entity.User;
import com.crawl.util.SimpleLogger;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * 使用用户following页面解析用户详细信息
 * http:www.zhihu.com/people/xian-ji-gan-2/following
 * @author hjg
 *
 */
public class ZhiHuUserInfoParser extends UserInfoParser{
    private static Logger logger = SimpleLogger.getSimpleLogger(ZhiHuUserInfoParser.class);
    
	private static ZhiHuUserInfoParser zhiHuUserInfoParser;
    public static ZhiHuUserInfoParser getInstance(){
        if(zhiHuUserInfoParser == null){
            zhiHuUserInfoParser = new ZhiHuUserInfoParser();
        }
        return zhiHuUserInfoParser;
    }
	@Override
    public User parse(Page page) {
    	User user=new User();
    	Document document=Jsoup.parse(page.getHtml());
    	String userId=getUserId(page.getUrl());
    	user.setUserId(userId);
    	
    	//从有data-state属性的标签中获取data-state属性值
    	String dataState=document.select("[data-state]").first().attr("data-state");
    	JSONObject jsonObject=new JSONObject(dataState);
    	String userInfo=jsonObject.getJSONObject("entities").getJSONObject("users").getJSONObject(userId).toString();
    	
    	setUserByReflect(user, "hashId", userInfo, "$.id");
    	setUserByReflect(user, "name", userInfo, "$.name");
    	setUserByReflect(user, "location", userInfo, "$.location[0].name");
    	setUserByReflect(user, "business", userInfo, "$.business.name");
    	Integer gender=JsonPath.parse(userInfo).read("$.gender");
    	if (gender!=null&&gender==1) {
			user.setGender("男");
		}
    	else if (gender!=null&&gender==0) {
			user.setGender("女");
		}
    	setUserByReflect(user, "voteupCount", userInfo, "$.voteupCount");
    	setUserByReflect(user, "thankedCount", userInfo, "$.thankedCount");
    	setUserByReflect(user, "questionCount", userInfo, "$.questionCount");
    	setUserByReflect(user, "answerCount", userInfo, "$.answerCount");
    	setUserByReflect(user, "articlesCount", userInfo, "$.articlesCount");
    	setUserByReflect(user, "followingCount", userInfo, "$.followingCount");
    	setUserByReflect(user, "followerCount", userInfo, "$.followerCount");
    	
    	return user;
    }
    
    /**
     * 通过userInfo和jsonPath获取值，并通过反射注入user
     * @param user
     * @param fieldName
     * @param userInfo
     * @param path
     */
    private void setUserByReflect(User user,String fieldName,String userInfo,String jsonPath){
    	try {
			Object object=JsonPath.parse(userInfo).read(jsonPath);
			Field field=user.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(user, object);
		} catch (SecurityException e) {
			logger.error("SecurityException",e);
		} catch (PathNotFoundException e) {
			//"userInfo"中没有相应的路径
		} catch (Exception e) {
			logger.error("Exception", e);
		}
    }
    /**
     * 根据url解析出用户id(userToken)
     * @param url
     * @return
     */
    private String getUserId(String url){
    	Pattern pattern=Pattern.compile("https://www.zhihu.com/people/(.*)/following");
    	Matcher matcher=pattern.matcher(url);
	    if(matcher.find()){
	        String userId = matcher.group(1);
	        return userId;
	    }
	    return null;
    }
    
    public static void main(String[] args) {
		System.out.println(new ZhiHuUserInfoParser().getUserId(
				"https://www.zhihu.com/people/xian-ji-gan-2/following"));
	}
}