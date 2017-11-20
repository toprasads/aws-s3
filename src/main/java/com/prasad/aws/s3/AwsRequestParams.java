package com.prasad.aws.s3;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
class AwsRequestParams {
	
	private String awsS3BucketName;
	
	private Long defaultUploadUrlTimeout;
	
	private Long defaultDownloadUrlTimeout;
	
	AwsRequestParams(String uploadTimeout, String downloadTimeout, String bucketName){
		defaultUploadUrlTimeout = Long.parseLong(uploadTimeout);
		defaultDownloadUrlTimeout = Long.parseLong(downloadTimeout);
		this.awsS3BucketName = bucketName;
	}
	
	

}
