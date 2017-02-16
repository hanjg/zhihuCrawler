package com.crawl.parser;

import java.util.ArrayList;
import java.util.List;

import com.crawl.entity.Page;
import com.jayway.jsonpath.JsonPath;

/**
 * 使用用户关注的人json解析关注列表
 * @author hjg
 *
 */
public class ZhiHuUserFollowingListParser extends UserFollowingListParser{
    private static ZhiHuUserFollowingListParser zhiHuUserFollowingListParser;
    public static ZhiHuUserFollowingListParser getInstance(){
        if(zhiHuUserFollowingListParser == null){
            zhiHuUserFollowingListParser = new ZhiHuUserFollowingListParser();
        }
        return zhiHuUserFollowingListParser;
    }
   
    @Override
    public List<String> parse(Page page) {
    	List<String> list=new ArrayList<>(20);
    	//取根目录下data下所有url_token的值
    	List<String> urlTokenList=JsonPath.parse(page.getHtml()).read("$.data..url_token");
    	for(String url_token:urlTokenList){
    		list.add("https://www.zhihu.com/people/"+url_token+"/following");
    	}
    	return list;
    }
    
}
