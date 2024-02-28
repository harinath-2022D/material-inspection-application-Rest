package com.zettamine.mi.servicesImpl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zettamine.mi.entities.User;
import com.zettamine.mi.repositories.UserRepository;
import com.zettamine.mi.requestdtos.NewUser;
import com.zettamine.mi.services.UserService;
import com.zettamine.mi.utils.StringUtil;

@Service
public class UserServiceImpl implements UserService{
	
	private static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public boolean saveUser(NewUser user) {
		User user1 = User.builder()
				.username(StringUtil.removeAllSpaces(user.getUsername()))
				.password(user.getPassword())
				.email(user.getEmail())
				.mobileNum(StringUtil.removeAllSpaces(user.getMobileNum()))
				.build();
		
		userRepo.save(user1);
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
