/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.blog;

import com.app.blog.util.Constants;
import com.app.blog.util.JWTUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author 1460344
 */
public class JwtFilter extends GenericFilterBean {
  @Autowired
	private JWTUtils jwtUtils;
    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest) sr;
      String auth = request.getHeader("auth");

		if (!((request.getRequestURI().contains("register")) || (request.getRequestURI().contains("login")))) {
			jwtUtils.verifyJwtAuth(auth);
		}  
      
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
}
