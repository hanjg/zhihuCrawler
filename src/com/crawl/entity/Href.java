package com.crawl.entity;

/**
 * URL：包括源码，超链接，标题
 * @author hjg
 *
 */
public class Href {
    /**
     * 源码
     */
    private String src;
    /**
     * 超链接
     */
    private String href;
    /**
     * 标题
     */
    private String title;
    public Href(){

    }
    public Href(String src, String href,String title) {
        this.src = src;
        this.href = href;
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
	    this.src = src;
	}
	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
