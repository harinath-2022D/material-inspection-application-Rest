package com.zettamine.mi.services;

import com.zettamine.mi.entities.User;

public interface UserService {
	boolean saveUser(User user);

	boolean checkUserCredentails(String username, String password);
}
