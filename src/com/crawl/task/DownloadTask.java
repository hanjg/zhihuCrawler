package com.crawl.task;

import org.apache.log4j.Logger;

import com.crawl.entity.Page;
import com.crawl.httpclient.ZhiHuHttpClient;
import com.crawl.util.SimpleLogger;


/**
 * 下载网页
 * @author hjg
 *
 */
public class DownloadTask implements Runnable{
	private static Logger logger = SimpleLogger.getSimpleLogger(DownloadTask.class);
	
	private String url;
	private static ZhiHuHttpClient httpClient = ZhiHuHttpClient.getInstance();
	
	public DownloadTask(String url){
		this.url = url;
	}
	
	public void run(){
		Page page=httpClient.getWebPage(url);
		int status=page.getStatusCode();
		logger.info(Thread.currentThread().getName()
				+" downloaded: "+page.getUrl()+" status: "+status);
		if (status==200) {
			httpClient.getParseThreadExecutor().execute(new ParseTask(page));
		}
		else if (status==500||status==502||status==504||status==429) {
			//服务器出错则将任务重新加入下载线程池
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.info("InterruptedException",e);
			}
			httpClient.getDownloadThreadExecutor().execute(new DownloadTask(url));
		}
	}
}
