package com.redzuandika.musiqlo.guru.model;

import com.google.gson.annotations.SerializedName;

public class ProvinsiItem{

	@SerializedName("nama")
	private String nama;

	@SerializedName("id")
	private int id;

	public String getNama(){
		return nama;
	}

	public int getId(){
		return id;
	}
}