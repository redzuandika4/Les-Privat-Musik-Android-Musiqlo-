package com.redzuandika.musiqlo.guru.model;

import com.google.gson.annotations.SerializedName;

public class KecamatanItem{

	@SerializedName("nama")
	private String nama;

	@SerializedName("id_kota")
	private String idKota;

	@SerializedName("id")
	private int id;

	public String getNama(){
		return nama;
	}

	public String getIdKota(){
		return idKota;
	}

	public int getId(){
		return id;
	}
}