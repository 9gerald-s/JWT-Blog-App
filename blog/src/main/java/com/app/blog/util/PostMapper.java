/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.blog.util;

import com.app.blog.models.Posts;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 1460344
 */
public class PostMapper {
    
    public Map postDetailsToMap(Posts post) {
        Map map = new HashMap();
        map.put("post_id", post.getPostId().toString());
        map.put("title", post.getPostTitle());
        map.put("body", post.getPostBody());
        map.put("created_on",post.getCreatedOn());
        map.put("created_by", post.getPublishedBy().getUserName());
        map.put("last_updated", post.getUpdatedOn());
        return map;
    } 
}
