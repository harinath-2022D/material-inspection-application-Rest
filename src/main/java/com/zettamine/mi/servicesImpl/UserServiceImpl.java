package com.zettamine.mi.servicesImpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zettamine.mi.entities.User;
import com.zettamine.mi.repositories.UserRepository;
import com.zettamine.mi.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	private static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public boolean saveUser(User user) {
		userRepo.save(user);
		LOGGER.info("new User Saved to DB");
		return true;
	}

	@Override
	public boolean checkUserCredentails(String username, String password) {
		Optional<User> optUser = userRepo.findByUsername(username);
		
		if(optUser.isPresent()) {
			System.out.println(optUser.get());
			return optUser.get().getPassword().equals(password) ? true : false;	
		}
		return false;
	}

}
