package com.redzuandika.musiqlo.guru.model;

import com.google.gson.annotations.SerializedName;

public class KotaKabupatenItem{

	@SerializedName("nama")
	private String nama;

	@SerializedName("id")
	private int id;

	@SerializedName("id_provinsi")
	private String idProvinsi;

	public String getNama(){
		return nama;
	}

	public int getId(){
		return id;
	}

	public String getIdProvinsi(){
		return idProvinsi;
	}
}