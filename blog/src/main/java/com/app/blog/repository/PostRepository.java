package com.app.blog.repository;

import com.app.blog.models.Posts;
import com.app.blog.models.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 1460344
 */
public interface PostRepository extends JpaRepository<Posts,Integer> {

  
}
