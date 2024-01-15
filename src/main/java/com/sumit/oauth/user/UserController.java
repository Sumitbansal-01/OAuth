package com.sumit.oauth.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@Autowired
	private UserRepo repo;
	
	BCryptPasswordEncoder passwordEncoder;
	
    
	public UserController() {
		super();
		passwordEncoder = new BCryptPasswordEncoder();
	}

	@GetMapping("/users")
	public List<User> getUsers(){
		return repo.findAll();
	}
	
	@GetMapping("/users/{id}")
	public User getUser(@PathVariable long id) {
		return repo.findById(id).get();
	}
	
	@PostMapping("/users")
	public ResponseEntity<User> addUser(@RequestBody User user) {
		String pass = user.getPassword();
		String hassPaswword = passwordEncoder.encode(pass);
		System.out.println(user+" "+pass+" "+hassPaswword);
		user.setPassword(hassPaswword);
		repo.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}
}
