package com.zettamine.mi.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zettamine.mi.requestdtos.NewUser;
import com.zettamine.mi.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")

public class UserController {

	private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@PostMapping("/save")
	public ResponseEntity<?> saveNewUser( @RequestBody @Valid NewUser user) {

		boolean isUserSaved = userService.saveUser(user);
		if (isUserSaved) {
			LOGGER.info("new user saving is success : {}", user);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			LOGGER.info("new user saving is failed due to duplicate : {}", user);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/login/{username}/{password}")
	public ResponseEntity<?> login(@PathVariable String username, @PathVariable String password) {
		boolean isValidUser = userService.checkUserCredentails(username, password);

		if (isValidUser) {
			LOGGER.info("successfull login from user : {}", username);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			LOGGER.warn("Unauthorized accessing to account : {}", username);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

}
