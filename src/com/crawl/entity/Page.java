package com.crawl.entity;

/**
 * 请求响应：包括请求url,状态码，响应的网页源码或者json
 * @author hjg
 *
 */
public class Page {
    private String url;
    private int statusCode;//响应状态码
    private String html;
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
