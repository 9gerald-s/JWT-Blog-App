package com.app.blog.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.blog.dto.PostDTO;
import com.app.blog.dto.UpdatePostDTO;
import com.app.blog.dto.UserPostDTO;
import com.app.blog.models.Posts;
import com.app.blog.models.Users;
import com.app.blog.repository.PostRepository;
import com.app.blog.repository.UserRepository;
import com.app.blog.util.Constants;
import com.app.blog.util.EntitiyHawk;
import com.app.blog.util.PostMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

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
@RequestMapping("/api")
public class GlobalController extends EntitiyHawk {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/getPostCount")
	public ResponseEntity getPostsCount() {
		List<Posts> postsList = postRepository.findAll();

		int totalPosts = postsList.size();

		return genericResponse(totalPosts);
	}

	@PostMapping("/publish")
	public ResponseEntity publishPost(@Valid @RequestBody PostDTO postDTO, BindingResult result,
			@RequestHeader("authorization") String header,@RequestAttribute("claims") Claims claims) {
		if (result.hasErrors()) {
			return genericError(result.getFieldError().getField() + " " + result.getFieldError().getDefaultMessage());
		}
//		String jwt = header.substring(7);
//		Claims claims = Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(jwt).getBody();
//		Claims claims = 
		int userId = Integer.parseInt(claims.get("user_id").toString());
		Users user = userRepository.findById(userId).get();
		Posts posts = new Posts();
		posts.setPostTitle(postDTO.getTitle());
		posts.setPostBody(postDTO.getBody());
		posts.setCreatedOn(new Date());
		posts.setUpdatedOn(new Date());
		posts.setPublishedBy(user);
		postRepository.save(posts);

		return genericResponse("Published");
	}

	@GetMapping("/getPost")
	public ResponseEntity getPosts() {
		List<Posts> postsList = postRepository.findAll();
		List<UpdatePostDTO> finalList = new ArrayList<>();
		for(Posts p:postsList) {
			UpdatePostDTO postDTO = new UpdatePostDTO();
			postDTO.setTitle(p.getPostTitle());
			postDTO.setBody(p.getPostBody());
			postDTO.setPost_id(p.getPostId());
			finalList.add(postDTO);
		}
		return genericResponse(finalList);
	}

	@GetMapping("/getPostByUser/{userId}")
	public ResponseEntity getPostByUser(@PathVariable(name = "userId") int id) {
		if (userRepository.existsById(id)) {
			List<Posts> userList = userRepository.findById(id).get().getPostsList();
			List<UserPostDTO> postList = new ArrayList<>();
			for(Posts p : userList) {
				UserPostDTO up = new UserPostDTO();
				up.setTitle(p.getPostTitle());
				up.setBody(p.getPostBody());
				up.setCreated_by(p.getPublishedBy().getUserName());
				postList.add(up);
			}
			return genericResponse(postList);
		}
		return genericError("No posts by user Id " + id);

	}

	@PostMapping("/updatePost")
	public ResponseEntity updatePost(@Valid @RequestBody UpdatePostDTO updatePostDTO, BindingResult result,
			@RequestHeader("authorization") String header) {
		if (result.hasErrors()) {
			return genericError(result.getFieldError().getField() + " " + result.getFieldError().getDefaultMessage());
		}
		String jwt = header.substring(7);
		Claims claims = Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(jwt).getBody();
		int userId = Integer.parseInt(claims.get("user_id").toString());
		System.out.println(userId + " ger");
		System.out.println(updatePostDTO.getPost_id());
		if (postRepository.existsById(updatePostDTO.getPost_id())) {
			Posts posts = postRepository.findById(updatePostDTO.getPost_id()).get();
			int puid = posts.getPublishedBy().getUserId().intValue();
			System.out.println(posts);
			if (puid == userId) {
				System.out.println("jack");
				posts.setPostTitle(updatePostDTO.getTitle());
				posts.setPostBody(updatePostDTO.getBody());
				postRepository.save(posts);
				return genericResponse("Post updated");
			}
		}
		return genericError("user id not match");
	}

	@GetMapping("/getPost/{postID}")
	public ResponseEntity getPostByPostId(@PathVariable(name = "postID") int id) {
		if (postRepository.existsById(id)) {
			Posts posts = postRepository.findById(id).get();
			UpdatePostDTO updatePostDTO = new UpdatePostDTO();
			updatePostDTO.setPost_id(posts.getPostId());
			updatePostDTO.setBody(posts.getPostBody());
			updatePostDTO.setTitle(posts.getPostTitle());
			return genericResponse(updatePostDTO);
		}
		return genericError("Post Not Found");
	}

	@GetMapping("/deletePost/{postID}")
	public ResponseEntity deletePostById(@PathVariable(name = "postID") int id,
			@RequestHeader("authorization") String header) {
		String jwt = header.substring(7);
		Claims claims = Jwts.parser().setSigningKey(Constants.JWT_SECRET).parseClaimsJws(jwt).getBody();
		int userId = Integer.parseInt(claims.get("user_id").toString());
		System.out.println(userId);
		Posts posts = postRepository.findById(id).get();
		if (posts.getPublishedBy().getUserId() == userId) {
			postRepository.delete(posts);
			return genericResponse("Post Deleted");
		}
		return genericError("user id not match");
	}
}
