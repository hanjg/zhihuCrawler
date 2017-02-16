package com.crawl.parser;

import com.crawl.entity.Page;

import java.util.List;

public abstract class UserFollowingListParser implements Parser {
    /**
     * 返回关注用户following页面列表
     * @param page
     * @return
     */
    public abstract List<String> parse(Page page);
}
