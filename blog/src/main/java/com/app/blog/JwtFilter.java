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
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
		 HttpServletResponse response = (HttpServletResponse) sr1;
		
			if (!((request.getRequestURI().contains("register")) || request.getRequestURI().contains("h2-console") ||(request.getRequestURI().contains("login")))) {
				System.out.println("filter");
				String header = request.getHeader("authorization");
				System.out.println(header);
				String token = null;
				if (header != null && header.startsWith("Bearer ")) {
					token = header.substring(7);
					try {
			            Claims claims = Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(token).getBody();
			            request.setAttribute("claims", claims);
			        }catch(MalformedJwtException e) {
			        	
			            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to read JSON value");
			            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			            return;
			           
			        }
					catch (Exception e) {
			            response.setStatus(HttpStatus.UNAUTHORIZED.value());
			            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access denied");
			        }

				}else {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
		            
				}

			}

			fc.doFilter(request, response);
		 // To change body of generated methods,
																			// choose
																			// Tools | Templates.
		}
	}


