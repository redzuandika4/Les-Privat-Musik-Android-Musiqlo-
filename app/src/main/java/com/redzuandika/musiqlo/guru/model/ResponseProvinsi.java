package com.redzuandika.musiqlo.guru.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseProvinsi{

	@SerializedName("provinsi")
	private List<ProvinsiItem> provinsi;

	public List<ProvinsiItem> getProvinsi(){
		return provinsi;
	}
}