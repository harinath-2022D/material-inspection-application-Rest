package com.zettamine.mi.entities;

import com.zettamine.mi.customannotation.StrongPassword;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table
@Data
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Size(min = 3, max = 25, message = "username should be min 5 char and max char 25")
	private String username;
	
	@StrongPassword
	private String password;
	
	@Email(message = "please provide valid email")
	private String email;
	
	private String mobileNum;

}
