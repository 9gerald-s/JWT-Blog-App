package com.app.blog.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.blog.dto.LoginDto;
import com.app.blog.dto.RegisterUserDTO;
import com.app.blog.models.Users;
import com.app.blog.repository.UserRepository;
import com.app.blog.util.EntitiyHawk;
import com.app.blog.util.JWTUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 1460344
 */
@RestController
@RequestMapping("/")
public class UserController extends EntitiyHawk {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	JWTUtils jwtUtils;
	
	@PostMapping("/register")
	public ResponseEntity registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO,BindingResult result) {
		
		if(result.hasErrors()) {
			return genericError(result.getFieldError().getField()+" "+result.getFieldError().getDefaultMessage());
		}
		
		Users users = new Users();
		users.setUserName(registerUserDTO.getName());
		users.setEmail(registerUserDTO.getEmail());
		users.setPassword(registerUserDTO.getPassword());
		
		userRepository.save(users);
		return genericResponse("User Registered");
	}
	
	@PostMapping("/login")
	public ResponseEntity loginUser(@Valid @RequestBody LoginDto loginDto,BindingResult result) {
		if(result.hasErrors()) {
			return genericError(result.getFieldError());
		}
		
		Users users = userRepository.findOneByEmailIgnoreCaseAndPassword(loginDto.getEmail(),loginDto.getPassword());
		System.out.println(users);
		String data = null;
		if(users != null) {
			data = jwtUtils.CreateJWTToken(users);
			return genericResponse(data);
		}else {
			data = "Invalid Username or Password";
			return genericResponse(data);
		}
		
//		return ResponseEntity.status(500).body("Unable to read JSON value");
	}

}
