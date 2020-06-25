package com.mercado.com.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties ( ignoreUnknown = true )
public class Item {
	
	String id;
	Float price;
	String status;
	
	public String getStatus ( ) {
		return status;
	}
	
	public void setStatus ( String status ) {
		this.status = status;
	}
	
	public String getId ( ) {
		return id;
	}
	
	public void setId ( String id ) {
		this.id = id;
	}
	
	public Float getPrice ( ) {
		return price;
	}
	
	public void setPrice ( Float price ) {
		this.price = price;
	}
}
