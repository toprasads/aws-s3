package com.prasad.aws.s3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.amazonaws.HttpMethod;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class AssetServiceImpl implements AssetService {

	private AmazonS3 amazonS3;
	private AwsRequestParams awsRequestParams;
	
	private HashMap<String, Asset> idToAsset = new HashMap<String, Asset>();
//	private static final long UPLOAD_URL_EXPIRATION_INTVAL_INHOURS = 1; // In HOURS
//	private static final long DOWNLOAD_URL_EXPIRATION_INTVAL_INSEC = 60;//In seconds
	
	
	public AssetServiceImpl(AmazonS3 amazonS3, AwsRequestParams requestParams) {
		this.amazonS3 = amazonS3;
		this.awsRequestParams = requestParams;
	}

	@Override
	public Asset uploadAsset() throws MalformedURLException {
		String id = UUID.randomUUID().toString();
		log.info("id is ****************" + id);
		
		log.info("bucket name is " + awsRequestParams.getAwsS3BucketName());
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(awsRequestParams.getAwsS3BucketName(), id);
		generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
		generatePresignedUrlRequest.setExpiration(getUploadExpiration());
		URL presignedUploadUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
		
		Asset asset = new Asset();
		asset.setId(id);
		asset.setUploadUrl(presignedUploadUrl);
		asset.setStatus(Status.PENDING_UPLOAD);
		idToAsset.put(id, asset);
		return asset;
	}
	
	private Date getUploadExpiration(){
		Date expiration = new Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * 60 * awsRequestParams.getDefaultUploadUrlTimeout(); // 1 hour default
		expiration.setTime(msec);
		log.info("set expiration time to : " + expiration);
		return expiration;
	}

	@Override
	public Asset updateAsset(String assetId, Status status) {
		Asset asset = idToAsset.get(assetId);
		if(asset != null){
			asset.setStatus(status);
			idToAsset.remove(assetId);
			idToAsset.put(assetId, asset);
			return asset;
		} else {
			throw new RuntimeException("asset does not exist");
		}
		
		
	}

	@Override
	public Asset getAsset(String id, Long downloadTimeout) throws MalformedURLException {
		long downloadAssetTimeout = (downloadTimeout == null || downloadTimeout == 0)?awsRequestParams.getDefaultDownloadUrlTimeout():downloadTimeout;
		Asset asset = idToAsset.get(id);
		if(asset != null && asset.getStatus().equals(Status.UPLOADED)){
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(this.awsRequestParams.getAwsS3BucketName(), id);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			
			generatePresignedUrlRequest.setExpiration(getDownloadExpirationTime(downloadAssetTimeout));
			URL presignedDownloadUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
			asset.setDownloadUrl(presignedDownloadUrl);
			asset.setDownloadTimeout(downloadAssetTimeout);
			idToAsset.remove(id);
			idToAsset.put(id, asset);
			return asset;
		} else 
			throw new RuntimeException("Asset does not exist or asset status not updated to UPLOADED yet");
		
	}
	
	private Date getDownloadExpirationTime(long offsetSeconds){
		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += 1000 * offsetSeconds;
		expiration.setTime(msec);
		return expiration;
	}

}
