package com.redzuandika.musiqlo.guru.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseKelurahan{

	@SerializedName("kelurahan")
	private List<KelurahanItem> kelurahan;

	public List<KelurahanItem> getKelurahan(){
		return kelurahan;
	}
}