package com.crawl.parser;

import com.crawl.entity.Page;
import com.crawl.entity.User;

public abstract class UserInfoParser implements Parser {
    /**
     * 返回用户信息
     * @param page
     * @return
     */
    public abstract User parse(Page page);
}
