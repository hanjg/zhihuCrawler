package com.crawl.httpclient;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.crawl.entity.Page;
import com.crawl.util.HttpClientUtil;
import com.crawl.util.SimpleLogger;

/**
 * @author hjg
 *
 */
public abstract class HttpClient {
    private Logger logger = SimpleLogger.getSimpleLogger(HttpClient.class);
    
    protected CloseableHttpClient closeableHttpClient;
    /**
     * 存储在不同http请求之间传递的信息
     */
    protected HttpClientContext httpClientContext;
    private CloseableHttpResponse closeableHttpResponse;
    
    protected HttpClient(){
        this.closeableHttpClient = HttpClientUtil.getMyHttpClient();
        this.httpClientContext = HttpClientUtil.getMyHttpClientContext();
    }
    private CloseableHttpResponse getResponse(HttpRequestBase request){
        try {
        	closeableHttpResponse = closeableHttpClient.execute(request, httpClientContext);
            return closeableHttpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获得网页源码或者json
     * @param url
     * @return
     */
    public Page getWebPage(String url){
        return getWebPag(new HttpGet(url));
    }
    public Page getWebPag(HttpRequestBase request){
    	Page page=new Page();
    	CloseableHttpResponse response=getResponse(request);
    	page.setUrl(request.getURI().toString());
    	page.setStatusCode(response.getStatusLine().getStatusCode());
    	if (page.getStatusCode()==200) {
			try {
				page.setHtml(IOUtils.toString(response.getEntity().getContent()));
			} catch (UnsupportedOperationException e) {
				logger.error("UnsupportedOperationException",e);
			} catch (IOException e) {
				logger.error("IOException",e);
			}
		}
    	request.releaseConnection();
    	return page;
    }
    /**
     * 反序列化CookiesStore
     * @return
     */
    public boolean deserializeCookieStore(String path){
    	try {
			CookieStore cookieStore = (CookieStore) HttpClientUtil.
			    	deserializeObject(path);
			httpClientContext.setCookieStore(cookieStore);
		} catch (IOException e) {
			logger.info("cookie文件不存在");
			return false;
		}
    	return true;
    }
    public CloseableHttpClient getCloseableHttpClient() {
        return closeableHttpClient;
    }

    public HttpClientContext getHttpClientContext() {
        return httpClientContext;
    }

    public abstract void initHttpClient();
}
