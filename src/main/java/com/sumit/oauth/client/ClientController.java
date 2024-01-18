package com.sumit.oauth.client;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.sumit.oauth.user.User;
import com.sumit.oauth.user.UserRepo;
import com.sumit.oauth.utility.EncryptionDecryption;

@RestController
public class ClientController {
	
	@Autowired
	private ClientRepo repo;
	@Autowired
	private UserRepo userRepo;
	BCryptPasswordEncoder passwordEncoder;
	
	public ClientController() {
		super();
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	@GetMapping("/clients")
	public MappingJacksonValue getClients(){
		return getFilter(repo.findAll());
	}
	
	private MappingJacksonValue getFilter(Object user) {
		MappingJacksonValue map = new MappingJacksonValue(user);
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email","phoneNumber");
		FilterProvider filters = new SimpleFilterProvider().addFilter("clientFilter", filter);
		map.setFilters(filters);
		return map;
	}
	
	
	@PostMapping("/clients/userLogin")
	public ResponseEntity<MappingJacksonValue> getUser(@RequestBody Map<String, String> requestBody) throws Exception{
		String id =requestBody.get("id");
		String apiKey =requestBody.get("apiKey");
		String token = requestBody.get("token");
		
		Optional<Client> oClient= repo.findById(id);
		if(oClient.isEmpty()) return ResponseEntity.notFound().build();
		Client client = oClient.get();
		
		if (!passwordEncoder.matches(apiKey, client.getApiKey()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		if(client.getRequestLeft()<=0)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		
		String[] decToken = EncryptionDecryption.decrypt(token).split(",");
		long tokenExpiry = Long.parseLong(decToken[0]);
		if(tokenExpiry >Instant.now().getEpochSecond())
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		Optional<User> oUser = userRepo.findByEmail(decToken[1]);
		if (oUser.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		User user = oUser.get();
		if (!passwordEncoder.matches(decToken[2], user.getPassword()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
		client.setRequestLeft(client.getRequestLeft()-1);
		repo.save(client);
		return ResponseEntity.ok().body(getFilter(user));	
	}
	
	@PostMapping("/clients/login")
	public ResponseEntity<MappingJacksonValue> getAClient(@RequestBody Map<String, String> requestBody){
		String id =requestBody.get("id");
		String  apiKey =requestBody.get("apiKey");
		Optional<Client> oClient= repo.findById(id);
		if(oClient.isEmpty()) return ResponseEntity.notFound().build();
		Client client = oClient.get();
		if (!passwordEncoder.matches(apiKey, client.getApiKey()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		return ResponseEntity.ok().body(getFilter(oClient.get()));	
	}
	
	@PostMapping("/clients")
	public ResponseEntity<MappingJacksonValue> createClient(@RequestBody Client client){
		client.setApiKey(passwordEncoder.encode(client.getApiKey()));
		client.setRequestLeft(10);
		repo.save(client);
		return ResponseEntity.created(null).body(getFilter(client));
	}
	
	@PostMapping("/clients/buyPlan")
	public ResponseEntity<MappingJacksonValue> buyPlan(@RequestBody Map<String, String> requestBody){
		String id = requestBody.get("id");
		long plan = Long.parseLong(requestBody.get("plan"));
		String apiKey = requestBody.get("apiKey");
		Optional<Client> oClient= repo.findById(id);
		if(oClient.isEmpty()) return ResponseEntity.notFound().build();
		Client client = oClient.get();
		if (!passwordEncoder.matches(apiKey, client.getApiKey()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		client.setRequestLeft(client.getRequestLeft()+plan);
		repo.save(client);
		return ResponseEntity.ok().body(getFilter(client));
	}
	
}
