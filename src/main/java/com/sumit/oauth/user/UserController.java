package com.sumit.oauth.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@RestController
public class UserController {

	@Autowired
	private UserRepo repo;

	BCryptPasswordEncoder passwordEncoder;

	public UserController() {
		super();
		passwordEncoder = new BCryptPasswordEncoder();
	}

	private MappingJacksonValue getFilter(Object user) {
		MappingJacksonValue map = new MappingJacksonValue(user);
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email",
				"phoneNumber", "profilePhoto");
		FilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", filter);
		map.setFilters(filters);
		return map;
	}

	@GetMapping("/users")
	public MappingJacksonValue getUsers() {
		List<User> user = repo.findAll();
		return getFilter(user);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<MappingJacksonValue> getUser(@PathVariable long id) {
		Optional<User> oUser = repo.findById(id);
		if (oUser.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		return ResponseEntity.status(HttpStatus.OK).body(getFilter(oUser.get()));
	}

	@PostMapping("/users")
	public ResponseEntity<MappingJacksonValue> addUser(@RequestBody User user) {
		String pass = user.getPassword();
		String hassPaswword = passwordEncoder.encode(pass);
		user.setPassword(hassPaswword);
		repo.save(user);
		MappingJacksonValue map = getFilter(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(map);
	}
}
