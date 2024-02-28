package com.zettamine.mi.services;

import com.zettamine.mi.requestdtos.NewUser;

public interface UserService {
	boolean saveUser(NewUser user);

	boolean checkUserCredentails(String username, String password);
}
