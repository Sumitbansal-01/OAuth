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
	String id;
	String email;
	String apiKey;
	String name;
	String phoneNumber;
	long requestLeft;
	String httpsuri;
	
	public Client() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public long getRequestLeft() {
		return requestLeft;
	}

	public void setRequestLeft(long requestLeft) {
		this.requestLeft = requestLeft;
	}

	public String getHttpsuri() {
		return httpsuri;
	}

	public void setHttpsuri(String httpsuri) {
		this.httpsuri = httpsuri;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", email=" + email + ", apiKey=" + apiKey + ", name=" + name + ", phoneNumber="
				+ phoneNumber + ", requestLeft=" + requestLeft + ", httpsuri=" + httpsuri + "]";
	}

	
	
}
