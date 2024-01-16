package com.sumit.oauth.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ClientController {
	
	@Autowired
	private ClientRepo repo;
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
	
	@GetMapping("/clients/{id}")
	public ResponseEntity<MappingJacksonValue> getAClient(@PathVariable long id){
		Optional<Client> oClient= repo.findById(id);
		if(oClient.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok().body(getFilter(oClient.get()));
		
	}
	
	@PostMapping("/clients")
	public ResponseEntity<MappingJacksonValue> createClient(@RequestBody Client client){
		client.setApiKey(passwordEncoder.encode(client.getApiKey()));
		repo.save(client);
		return ResponseEntity.created(null).body(getFilter(client));
	}
}
