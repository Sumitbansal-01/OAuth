package com.sumit.oauth.client;

import com.fasterxml.jackson.annotation.JsonFilter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@JsonFilter("clientFilter")
@Entity
public class Client {

	@Id
	@GeneratedValue
	long id;
	String email;
	String apiKey;
	String name;
	String phoneNumber;
	
	public Client() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", email=" + email + ", apiKey=" + apiKey + ", name=" + name + ", phoneNumber="
				+ phoneNumber + "]";
	}
	
	
}
