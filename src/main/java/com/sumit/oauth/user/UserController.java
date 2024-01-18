package com.sumit.oauth.user;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.sumit.oauth.utility.EncryptionDecryption;

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

	@PostMapping("/users/login")
	public ResponseEntity<String> login( @RequestBody Map<String, String> requestBody) throws Exception {
		String email =requestBody.get("email");
		String password =requestBody.get("password");
		Optional<User> oUser = repo.findByEmail(email);
		if (oUser.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		User user = oUser.get();
		if (!passwordEncoder.matches(password, user.getPassword()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		String tokenArray = String.join(",",new String[] {""+Instant.now().getEpochSecond()+(60*60),email, password});
		String token = EncryptionDecryption.encrypt(tokenArray);
		return ResponseEntity.status(HttpStatus.OK).body(token);
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
	
	@PutMapping("/users")
	public ResponseEntity<MappingJacksonValue> updateUser(@RequestBody Map<String, String> body) throws Exception {
		String token = body.get("token");
		String phoneNumber = body.get("phoneNumber");
		String password = body.get("password");
		String profilePhoto = body.get("profilePhoto");
		String[] decToken = EncryptionDecryption.decrypt(token).split(",");
		long tokenExpiry = Long.parseLong(decToken[0]);
		if(tokenExpiry >Instant.now().getEpochSecond())
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		Optional<User> oUser = repo.findByEmail(decToken[1]);
		if (oUser.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		User user = oUser.get();
		if (!passwordEncoder.matches(decToken[2], user.getPassword()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		if(phoneNumber !=null) user.setPhoneNumber(phoneNumber);
		System.out.println("\n"+profilePhoto+" "+body);
		if(profilePhoto !=null) user.setProfilePhoto(profilePhoto);
		if(password !=null) user.setPassword(passwordEncoder.encode(password));
		repo.save(user);
		return ResponseEntity.ok().build();
	}
}
