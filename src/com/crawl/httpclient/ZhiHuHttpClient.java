package com.crawl.httpclient;

import com.crawl.config.Config;
import com.crawl.dao.DBConnectionManage;
import com.crawl.dao.ZhiHuDAO;
import com.crawl.task.DownloadTask;
import com.crawl.task.ModelLogin;
import com.crawl.task.ParseTask;
import com.crawl.util.SimpleLogger;
import com.crawl.util.ThreadPoolMonitor;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * 知乎客户端
 * @author hjg
 *
 */
public class ZhiHuHttpClient extends HttpClient{
    private static Logger logger = SimpleLogger.getSimpleLogger(ZhiHuHttpClient.class);
    
    private static final ZhiHuHttpClient INSTANCE=new ZhiHuHttpClient();
        
    /**
     * 解析网页线程池
     */
    private ThreadPoolExecutor parseThreadExecutor;
    /**
     * 下载网页线程池
     */
    private ThreadPoolExecutor downloadThreadExecutor;
    
    public ZhiHuHttpClient() {
    	super();
        initHttpClient();
        intiThreadPool();
    }
    /**
     * 初始化知乎客户端。
     * 模拟登录知乎，持久化Cookie到本地，不用以后每次都登录
     */
    @Override
    public void initHttpClient() {
        if(!deserializeCookieStore(Config.cookiePath)){
            new ModelLogin().login(this, Config.zhihuAccount, Config.zhihuPassword);
        }
        if(Config.dbEnable){
            ZhiHuDAO.DBTablesInit(DBConnectionManage.getConnection());
        }
    }

    public static final ZhiHuHttpClient getInstance(){
	    return INSTANCE;
	}
	/**
     * 初始化线程池
     */
    private void intiThreadPool(){
        parseThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());//使用线程安全的阻塞队列
        downloadThreadExecutor = new ThreadPoolExecutor(Config.downloadThreadSize,
                Config.downloadThreadSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        new Thread(new ThreadPoolMonitor(downloadThreadExecutor, "DownloadPage ThreadPool")).start();
        new Thread(new ThreadPoolMonitor(parseThreadExecutor, "ParsePage ThreadPool")).start();
    }
    
    public void startCrawl(String url){
        downloadThreadExecutor.execute(new DownloadTask(url));
        manageZhiHuClient();
    }

    /**
     * 管理知乎客户端，当解析的网页数目到预定值时关闭整个爬虫
     */
    public void manageZhiHuClient(){
    	while(true){
    		long parsedPageCount=parseThreadExecutor.getTaskCount();
    		System.out.println("parsedPages:"+parsedPageCount);
    		if (parsedPageCount>=Config.parsedPageCount&&
    				!ZhiHuHttpClient.getInstance().getDownloadThreadExecutor().isShutdown()) {
				ParseTask.isStopDownload=true;
				//关闭下载线程池
				ZhiHuHttpClient.getInstance().getDownloadThreadExecutor().shutdown();
			}
    		if (ZhiHuHttpClient.getInstance().getDownloadThreadExecutor().isTerminated()&&
    				!ZhiHuHttpClient.getInstance().getParseThreadExecutor().isShutdown()) {
				//关闭下载线程池之后，关闭解析线程池
    			ZhiHuHttpClient.getInstance().getParseThreadExecutor().shutdown();
			}
    		if (ZhiHuHttpClient.getInstance().getParseThreadExecutor().isTerminated()) {
				//关闭线程池监视器
    			ThreadPoolMonitor.isStopMonitor=true;
    			logger.info("--------爬取结果---------");
    			logger.info("获得用户信息数量:"+ParseTask.parsedUserCount);
    			break;
			}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("InterruptedException",e);
			}
    	}
    }
    
    public ThreadPoolExecutor getParseThreadExecutor() {
        return parseThreadExecutor;
    }

    public ThreadPoolExecutor getDownloadThreadExecutor() {
        return downloadThreadExecutor;
    }
}
