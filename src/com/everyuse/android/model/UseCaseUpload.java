package com.everyuse.android.model;

import java.io.File;

public class UseCaseUpload {
	public String item;
	public String purpose;
	public File photo_file;

	public UseCaseUpload() {
	}

	public UseCaseUpload(String product, String function, File photo_file) {
		this.item = product;
		this.purpose = function;
		this.photo_file = photo_file;
	}
}
