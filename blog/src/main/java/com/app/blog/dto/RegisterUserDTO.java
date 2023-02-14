/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.blog.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author 1460344
 */
public class RegisterUserDTO {

    @NotBlank(message = "Email cannot be blank")
    @Length(max = 40)
    @Valid
    String email;
    @NotBlank(message = "Name cannot be blank")
    @Length(max = 40)
    @Valid
    String name;
    @NotBlank(message = "password cannot be blank")
    @Length(min = 3, max = 45)
    @Valid
    String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	@Override
	public String toString() {
		return "RegisterUserDTO [email=" + email + ", name=" + name + ", password=" + password + "]";
	}
}
