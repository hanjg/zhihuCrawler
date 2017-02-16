package com.crawl.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 * httpclient工具
 * @author hjg
 *
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil {
	private static Logger logger = SimpleLogger.getSimpleLogger(HttpClientUtil.class);
	/**
	 * 
	 * @param httpClient 客户端
	 * @param context 上下文
	 * @param request 请求
	 * @param encoding 字符编码
	 * @param isPrintConsole 是否打印到控制台
     * @return 请求的返回内容
     */
	public static String getResponseContent(CloseableHttpClient httpClient
			, HttpClientContext context
			, HttpRequestBase request
			, String encoding
			, boolean isPrintConsole){
		StringBuilder builder=new StringBuilder();
		try(CloseableHttpResponse response=httpClient.execute(request, context);
			BufferedReader reader=new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(), encoding))){
			
			logger.info("statusCode:"+response.getStatusLine().getStatusCode());
			String line="";
			while((line=reader.readLine())!=null){
				builder.append(line);
				if (isPrintConsole) {
					logger.info(line);
				}
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} 
		return builder.toString();
	}
	
	/**
	 * 序列化对象object至filePath
	 * @param object
	 * @param filePath
	 */
	public static void serializeObject(Object object,String filePath){
		try(ObjectOutputStream out=new ObjectOutputStream(
				new FileOutputStream(filePath))){
			
			out.writeObject(object);
			out.flush();
			logger.info("序列化成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从filePath反序列化对象
	 * @param filePath
	 * @throws IOException 文件不存在则抛出异常
	 */
	public static Object deserializeObject(String filePath) throws IOException {
		Object object=null;
		try(ObjectInputStream in=new ObjectInputStream(
				new FileInputStream(filePath))){
			
			object=in.readObject();
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException",e);
		} 
		return object;
	}
	/**
	 * 设置Cookies策略
	 * @return CloseableHttpClient
	 */
	@Deprecated
	public static CloseableHttpClient getMyHttpClient2(){
		RequestConfig globalConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
				.build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(globalConfig)
				.build();
		return httpClient;
	}
	/**
	 * 设置上下文
	 * @return HttpClientContext
	 */
	@Deprecated
	public static HttpClientContext getMyHttpClientContext2(){
		HttpClientContext context = HttpClientContext.create();
		Registry<CookieSpecProvider> registry = RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BROWSER_COMPATIBILITY,
						new BrowserCompatSpecFactory())
				.build();
		context.setCookieSpecRegistry(registry);
		return context;
	}
	/**
	 * 设置Cookies策略
	 * @return CloseableHttpClient
	 */
	public static CloseableHttpClient getMyHttpClient(){
		RequestConfig globalConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.STANDARD_STRICT)
				.build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(globalConfig)
				.setDefaultCookieStore(new BasicCookieStore())
				.build();
		
		return httpClient;
	}

	/**
	 * 设置上下文
	 * @return HttpClientContext
	 */
	public static HttpClientContext getMyHttpClientContext(){
		HttpClientContext context = HttpClientContext.create();
		return context;
	}

	/**
	 * 下载文件
	 * @param fileURL 文件url
	 * @param catalogue 保存目录
	 * @param saveFileName 文件名，包括后缀名
	 * @param isReplaceFile 若存在文件时，是否还需要下载文件
	 */
	public static void downloadFile(CloseableHttpClient httpClient
			, HttpClientContext context
			, String fileURL
			, String catalogue
			, String saveFileName
			, Boolean isReplaceFile){
		HttpGet httpGet=new HttpGet(fileURL);
		try(CloseableHttpResponse response=httpClient.execute(httpGet,context)){
			
			logger.info("status:"+response.getStatusLine().getStatusCode());
			
			File file=new File(catalogue);
			if (!file.exists()&&file.isDirectory()) {
				file.mkdir();
				logger.info("目录创建成功");
			}
			else {
				logger.info("目录已存在");
			}
			
			file=new File(catalogue+saveFileName);
			if (!file.exists()||isReplaceFile) {
				try(FileOutputStream out=new FileOutputStream(file);
					BufferedInputStream in=new BufferedInputStream(response.getEntity().getContent())){
					
					int b=0;
					while((b=in.read())!=-1){
						out.write(b);
					}
					out.flush();
					logger.info("文件下载成功，已下载至"+catalogue+saveFileName);
				} catch (IOException e) {
					logger.error("IOException",e);
				}
			}
			else {
				logger.info("文件存在且无需替换");
			}
		} catch (ClientProtocolException e1) {
			logger.error("ClientProtocolException",e1);
		} catch (IOException e1) {
			logger.error("IOException",e1);
		}
		
	}
	/**
	 * 输出Cookies
	 * @param cookieStore
	 */
	public static void getCookies(CookieStore cookieStore){
		List<Cookie> cookies = cookieStore.getCookies();
		if(cookies == null){
			logger.info("该CookiesStore无Cookie");
		}else{
			for(int i = 0;i < cookies.size();i++){
				logger.info("cookie：" + cookies.get(i).getName() + ":"+ cookies.get(i).getValue()
						+ "----过期时间"+ cookies.get(i).getExpiryDate()
						+ "----Comment"+ cookies.get(i).getComment()
						+ "----CommentURL"+ cookies.get(i).getCommentURL()
						+ "----domain"+ cookies.get(i).getDomain()
						+ "----ports"+ cookies.get(i).getPorts()
				);
			}
		}
	}
	public static void getAllHeaders(Header [] headers){
		logger.info("------标头开始------");
		for(int i = 0;i < headers.length;i++){
			logger.info(headers[i]);
		}
		logger.info("------标头结束------");
	}
	/**
	 * InputStream转换为String
	 * @param is
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String isToString(InputStream is,String encoding) throws Exception{
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, encoding);
		return writer.toString();
	}
	/**
	 * 设置request请求参数
	 * @param httpPost
	 * @param parameters
     */
	public static void setHttpPostParams(HttpPost httpPost,Map<String,String> parameters){
		List<NameValuePair> formData=new ArrayList<>();
		for(String key:parameters.keySet()){
			formData.add(new BasicNameValuePair(key, parameters.get(key)));
		}
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(formData);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			logger.info("UnsupportedEncodingException",e);
		}
	}
}
