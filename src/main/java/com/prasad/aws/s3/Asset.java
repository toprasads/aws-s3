package com.prasad.aws.s3;

import java.net.URL;

import lombok.Data;

@Data
public class Asset {
	
	String id;
	URL uploadUrl;
	URL downloadUrl;
	long downloadTimeout;//in seconds
	Status status;

}
