package com.redzuandika.musiqlo.guru.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseKecamatan{

	@SerializedName("kecamatan")
	private List<KecamatanItem> kecamatan;

	public List<KecamatanItem> getKecamatan(){
		return kecamatan;
	}
}