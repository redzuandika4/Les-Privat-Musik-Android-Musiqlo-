package com.redzuandika.musiqlo.guru.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseKota{

	@SerializedName("kota_kabupaten")
	private List<KotaKabupatenItem> kotaKabupaten;

	public List<KotaKabupatenItem> getKotaKabupaten(){
		return kotaKabupaten;
	}
}