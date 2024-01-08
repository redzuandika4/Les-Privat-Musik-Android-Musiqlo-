package com.redzuandika.musiqlo.guru.model;

import com.google.gson.annotations.SerializedName;

public class KelurahanItem{

	@SerializedName("id_kecamatan")
	private String idKecamatan;

	@SerializedName("nama")
	private String nama;

	@SerializedName("id")
	private long id;

	public String getIdKecamatan(){
		return idKecamatan;
	}

	public String getNama(){
		return nama;
	}

	public long getId(){
		return id;
	}
}