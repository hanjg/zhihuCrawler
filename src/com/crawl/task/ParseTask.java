package com.crawl.task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.crawl.config.Config;
import com.crawl.dao.ZhiHuDAO;
import com.crawl.entity.Page;
import com.crawl.entity.User;
import com.crawl.httpclient.ZhiHuHttpClient;
import com.crawl.parser.UserInfoParser;
import com.crawl.parser.ZhiHuUserInfoParser;
import com.crawl.parser.ZhiHuUserFollowingListParser;
import com.crawl.util.Md5Util;
import com.crawl.util.SimpleLogger;

/**
 * 解析网页
 * @author hjg
 *
 */
public class ParseTask implements Runnable {
    private static Logger logger = SimpleLogger.getSimpleLogger(ParseTask.class);
    
    private Page page;
    private static ZhiHuHttpClient zhiHuHttpClient = ZhiHuHttpClient.getInstance();
    /**
     * 已解析的用户数量
     */
    public static AtomicInteger parsedUserCount = new AtomicInteger(0);
    /**
     * 停止下载
     * 
     */
    //多任务环境下的共享标志使用volatile：该变量不稳定，每次使用需要从主存中读取，改变之后也需要写入主存
    //这样在任何时刻，两个不同的线程总是看到某个成员变量的同一个值
    public static volatile boolean isStopDownload = false;
    
    public ParseTask(Page page){
        this.page = page;
    }
    
    public void run() {
    	Document document=Jsoup.parse(page.getHtml());
    	if (document.select("title").size()!=0) {
    		//包含title标签，是用户following页面
    		UserInfoParser parser=ZhiHuUserInfoParser.getInstance();
    		User user=parser.parse(page);
			logger.info("用户解析成功： "+user.toString());
			
			if (Config.dbEnable) {
				ZhiHuDAO.insertToDB(user);
			}
			
			parsedUserCount.incrementAndGet();
			for(int i=0;i<user.getFollowingCount()/20+1;i++){
				//网页下载队列小于100时获取用户关注列表
				if (!isStopDownload&&zhiHuHttpClient.getDownloadThreadExecutor().getQueue().size()<100) {
					//获得用户关注列表的地址
					String toGetFollowingListUrl=toGetFollowing(i*20, user.getUserId());
					handleUrl(toGetFollowingListUrl);
				}
			}
		}
    	else {
			//用户关注的人json
    		if (!isStopDownload&&zhiHuHttpClient.getDownloadThreadExecutor().getQueue().size()<100) {
    			//获得用户关注的人的following页面url列表
				List<String> userFollowingUrls=ZhiHuUserFollowingListParser.getInstance().parse(page);
				for(String url:userFollowingUrls){
					handleUrl(url);
				}
			}
		}
    	logger.info(Thread.currentThread().getName()
				+" parsed: "+page.getUrl()+" status: "+page.getStatusCode());
    }
    /**
     * 通过用户的url_token获得用户关注的人json的地址
     * @param offset
     * @param userToken
     * @return
     */
    private String toGetFollowing(int offset, String userToken){
    	String url="https://www.zhihu.com/api/v4/members/"+userToken+
    			"/followees?include=data%5B*%5D.answer_count%2Carticles_count%2Cgender%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset="+
    			offset+"&limit=20";
        return url;
    }
    /**
     * 获得url对应的following页面源码或者关注的人的json
     * @param url
     */
    private void handleUrl(String url){
        if(!Config.dbEnable){
            zhiHuHttpClient.getDownloadThreadExecutor().execute(new DownloadTask(url));
            return ;
        }
        String md5Url = Md5Util.Convert2Md5(url);
        boolean isRepeat = ZhiHuDAO.insertHref(md5Url);
        if(!isRepeat ||
                (!zhiHuHttpClient.getDownloadThreadExecutor().isShutdown() &&
                		zhiHuHttpClient.getDownloadThreadExecutor().getQueue().size() < 30)){
            /**
             * 防止互相等待，导致死锁。
             * 当抓取的用户数量足够多时，可能会出现用户的关注列表已经全部抓取，如果
             * 只是使用（!isRepeat）可能会导致下载线程池中没有下载线程，解析线程池中
             * 也没有解析线程的情况，这样就会相互等待。
             * 加入之后的条件可以避免相互等待的情况，但是会重复下载和解析，直到解析
             * 的网页数达到预定值
             */
            zhiHuHttpClient.getDownloadThreadExecutor().execute(new DownloadTask(url));
        }
    }
    public static void main(String[] args) {
		String string="https://www.zhihu.com/node/ProfileFolloweesListV2?params={%22offset%22:0,%22order_by%22:%22created%22,%22hash_id%22:%22xian-ji-gan-2%22}";
		String string2=new ParseTask(new Page()).toGetFollowing(0, "xian-ji-gan-2");
		System.out.println(string+"\n"+string2);
	}
}
