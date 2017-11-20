package com.prasad.aws.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;
    
    @Value("${aws.s3.upload.url.timeout}")
    private String defaultUploadUrlTimeoutt;
    
    @Value("${aws.s3.download.url.timeout}")
    private String defaultDownloadUrlTimeoutt;
    
    @Value("${amazon.s3.bucketname}")
    private String bucketName;
    
    @Bean
    public AwsRequestParams getAwsRequestParams(){
    	return new AwsRequestParams(defaultUploadUrlTimeoutt, defaultDownloadUrlTimeoutt, bucketName);
    }

    @Bean
    public AWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3 getAmazonS3(AWSCredentials awsCredentials) {
    	AWSCredentials creds = getAwsCredentials();
    	return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(region).build();
    }
    
    @Bean
    public AssetService assetService(AmazonS3 amazonS3, AwsRequestParams awsRequestParams){
    	return new AssetServiceImpl(amazonS3, awsRequestParams);
    }
}
