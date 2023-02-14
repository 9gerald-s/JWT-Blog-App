package com.app.blog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.app.blog.dto.LoginDto;
import com.app.blog.dto.PostDTO;
import com.app.blog.dto.UpdatePostDTO;

@TestMethodOrder(Alphanumeric.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class BlogApplicationTests {
	@LocalServerPort
	int port;
	@Autowired
	private MockMvc mvc;
	@Autowired
	TestRestTemplate template;
	static String user, pass, jwt, postBody, postTitle;
	static int postCount, postId = -1;

	public String generateString() {
		String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Random random = new Random();
		candidateChars.charAt(random.nextInt(candidateChars.length()));
		String randStr = "";
		while (randStr.length() < 8)
			randStr += candidateChars.charAt(random.nextInt(candidateChars.length()));
		return randStr;
	}

	@Test
	public void test1_register() {
		try {
			user = generateString();
			pass = generateString();
			JSONObject json = new JSONObject();
			json.put("name", user).put("password", pass);
			mvc.perform(post("http://localhost:" + port + "/register").contentType(MediaType.APPLICATION_JSON)
					.content(json.toString())).andExpect(status().isOk())
					.andExpect(jsonPath("$.data", is("email Email cannot be blank")));
			json.put("email", user + "@gmail.com");
			mvc.perform(post("http://localhost:" + port + "/register").contentType(MediaType.APPLICATION_JSON)
					.content(json.toString())).andExpect(status().isOk())
					.andExpect(jsonPath("$.data", is("User Registered")));
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test2_login() {
		try {
			LoginDto obj = new LoginDto();
			obj.setEmail(user);
			obj.setPassword(pass);
			JSONObject json = new JSONObject(
					template.postForEntity("http://localhost:" + port + "/login/", obj, String.class).getBody());
			assertEquals(json.getString("data"), "Invalid Username or Password");
			obj.setEmail(user + "@gmail.com");
			json = new JSONObject(
					template.postForEntity("http://localhost:" + port + "/login/", obj, String.class).getBody());
			jwt = json.getString("data");
			assert (!jwt.equals(""));
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + (char) ((int) jwt.charAt(0) + 1) + jwt.substring(1, jwt.length()));
			ResponseEntity<String> res = template.exchange("http://localhost:" + port + "/api/getPostCount",
					HttpMethod.GET, new HttpEntity<String>(headers), String.class);
			assert (res.getBody().contains("Unable to read JSON value"));
			assertEquals(500, res.getStatusCodeValue());
			headers.set("authorization", "Bearer " + jwt);
			json = new JSONObject(template.exchange("http://localhost:" + port + "/api/getPostCount", HttpMethod.GET,
					new HttpEntity<String>(headers), String.class).getBody());
			postCount = json.getInt("data");
			assert (postCount >= 0);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test3_publishPost() {
		try {
			PostDTO post = new PostDTO();
			postTitle = generateString();
			postBody = generateString();
			post.setTitle(postTitle);
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + (char) ((int) jwt.charAt(0) + 1) + jwt.substring(1, jwt.length()));
			HttpEntity<PostDTO> request = new HttpEntity<>(post, headers);
			ResponseEntity<String> res = template.postForEntity("http://localhost:" + port + "/api/publish", request,
					String.class);
			assert (res.getBody().contains("Unable to read JSON value"));
			assertEquals(500, res.getStatusCodeValue());
			headers.set("authorization", "Bearer " + jwt);
			request = new HttpEntity<>(post, headers);
			JSONObject json = new JSONObject(template
					.postForEntity("http://localhost:" + port + "/api/publish", request, String.class).getBody());
			assertEquals(json.getString("data"), "body should not be empty");
			post.setBody(postBody);
			request = new HttpEntity<>(post, headers);
			json = new JSONObject(template
					.postForEntity("http://localhost:" + port + "/api/publish", request, String.class).getBody());
			assertEquals(json.getString("data"), "Published");
			json = new JSONObject(template.exchange("http://localhost:" + port + "/api/getPost", HttpMethod.GET,
					new HttpEntity<String>(headers), String.class).getBody());
			JSONArray arr = (JSONArray) json.get("data");
			for (int i = 0; i < arr.length(); i++)
				if (((JSONObject) arr.get(i)).getString("title").contentEquals(postTitle)
						&& ((JSONObject) arr.get(i)).getString("body").contentEquals(postBody)) {
					postId = ((JSONObject) arr.get(i)).getInt("post_id");
					break;
				}
			assert (postId > -1);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test4_postCount() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + jwt);
			JSONObject json = new JSONObject(template.exchange("http://localhost:" + port + "/api/getPostCount",
					HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody());
			assertEquals(json.getInt("data"), postCount + 1);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test5_getPostById() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + jwt);
			JSONObject json = (JSONObject) (new JSONObject(
					template.exchange("http://localhost:" + port + "/api/getPost/" + postId, HttpMethod.GET,
							new HttpEntity<String>(headers), String.class).getBody())).get("data");
			assertEquals(json.getInt("post_id"), postId);
			assertEquals(json.getString("title"), postTitle);
			assertEquals(json.getString("body"), postBody);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test6_getPostByUserId() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + jwt);
			JSONObject json;
			boolean pass = false;
			for (int i = 0; i < 10; i++) {
				json = new JSONObject(template.exchange("http://localhost:" + port + "/api/getPostByUser/" + i,
						HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody());
				if (!json.get("data").toString().contentEquals("No posts by user Id " + i)
						&& json.getJSONArray("data").getJSONObject(0).getString("title").contentEquals(postTitle)
						&& json.getJSONArray("data").getJSONObject(0).getString("body").contentEquals(postBody)
						&& json.getJSONArray("data").getJSONObject(0).getString("created_by").contentEquals(user)) {
					pass = true;
					break;
				}
			}
			assert (pass);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test7_updatePost() {
		try {
			UpdatePostDTO post = new UpdatePostDTO();
			post.setPost_id(postId);
			post.setTitle(postTitle);
			String newBody = generateString();
			post.setBody(newBody);
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + jwt);
			HttpEntity<UpdatePostDTO> request = new HttpEntity<>(post, headers);
			JSONObject json = new JSONObject(template
					.postForEntity("http://localhost:" + port + "/api/updatePost", request, String.class).getBody());
			assertEquals(json.getString("data"), "Post updated");
			json = new JSONObject(template.exchange("http://localhost:" + port + "/api/getPost/" + postId,
					HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody()).getJSONObject("data");
			assertEquals(json.getInt("post_id"), postId);
			assertEquals(json.getString("title"), postTitle);
			assertEquals(json.getString("body"), newBody);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

	@Test
	public void test8_delPost() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("authorization", "Bearer " + jwt);
			JSONObject json = new JSONObject(template.exchange("http://localhost:" + port + "/api/deletePost/" + postId,
					HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody());
			assertEquals(json.getString("data"), "Post Deleted");
			json = new JSONObject(template.exchange("http://localhost:" + port + "/api/getPost/" + postId,
					HttpMethod.GET, new HttpEntity<String>(headers), String.class).getBody());
			assertEquals(json.getString("data"), "Post Not Found");
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

}
