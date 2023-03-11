/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.blog.util;

import com.app.blog.models.Users;
import com.app.blog.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Date;

import org.springframework.stereotype.Component;


/**
 *
 * @author 1460344
 */

@Component
public class JWTUtils {
	
	private UserRepository userRepository;

	public String CreateJWTToken(Users user) {

		Claims claims = Jwts.claims();
		claims.put("name", user.getUserName());
		claims.put("email", user.getEmail());
		claims.put("user_id", user.getUserId());
		claims.setSubject("MY Blog");
		claims.setIssuedAt(new Date());

		String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, Constants.JWT_SECRET)
				.compact();

		return token;
	}

	public void verifyJwtAuth(String auth) throws AccessDeniedException {
		try {
			
			Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(auth).getBody();
			System.out.println("passed");
		} catch (Exception e) {
			throw new AccessDeniedException("Access Denied");
		}
//
//		try {
//			Claims claims = Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(auth).getBody();
//			int userId = Integer.parseInt((claims.getIssuer()));
//			System.out.println(userId);
//			if(!userRepository.existsById(userId)) {
//				throw new UserPrincipalNotFoundException("User name not found");
//			}
//		} catch (Exception e) {
//			throw new AccessDeniedException("Access Denied Unable to read JSON value");
//		}
	}
}
