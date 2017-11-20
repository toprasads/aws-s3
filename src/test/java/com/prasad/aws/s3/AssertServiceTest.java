package com.prasad.aws.s3;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;


public class AssertServiceTest {
	
	AmazonS3 amazonS3;
	AwsRequestParams defaultTimeouts;
	AssetService classUnderTest;

	@Before
	public void init(){
	    amazonS3 = Mockito.mock(AmazonS3.class);
	    defaultTimeouts = new AwsRequestParams("1", "60", "");
	    classUnderTest = new AssetServiceImpl(amazonS3, defaultTimeouts);
	}
	
	@Test
	public void testGetUploadURL() throws SdkClientException, MalformedURLException {
		URL urlToBeReturned = new URL("http://test");
		Mockito.when(amazonS3.generatePresignedUrl(Mockito.any())).thenReturn(urlToBeReturned);
		Asset asset = classUnderTest.uploadAsset();
		URL urlReturned = asset.getUploadUrl();
		Assert.assertNotNull("url returned is null", urlReturned);
		Assert.assertEquals(urlToBeReturned, urlReturned);
	}
	
	@Test
	public void testUpdateDownloadStatusWithBadAssetId(){
		try{
			classUnderTest.updateAsset("bla", Status.PENDING_UPLOAD);
		} catch(Exception e){
			Assert.assertTrue("did not catch exception as expected", true);
		}
	}
	
	@Test
	public void testUpdateDownloadStatusWithGoodAssetId() throws MalformedURLException{
		URL urlToBeReturned = new URL("http://test");
		Mockito.when(amazonS3.generatePresignedUrl(Mockito.any())).thenReturn(urlToBeReturned);
		Asset asset = classUnderTest.uploadAsset();
		Asset updatedAsset = classUnderTest.updateAsset(asset.getId(), Status.UPLOADED);
		Assert.assertEquals(Status.UPLOADED, updatedAsset.getStatus());
	}
	
	@Test
	public void testGetDownloadUrlShouldFailWhenStatusHasNotBeenChangedToUploaded() throws MalformedURLException{
		URL urlToBeReturned = new URL("http://test");
		Mockito.when(amazonS3.generatePresignedUrl(Mockito.any())).thenReturn(urlToBeReturned);
		Asset asset = classUnderTest.uploadAsset();
		try{
			Asset assetWithDownloadUrl = classUnderTest.getAsset(asset.getId(), new Long(0));
		} catch (Exception e){
			Assert.assertTrue("did not get exception as expected", true);
		}
		
	}
	
	@Test
	public void testGetDownloadUrlSucceeds() throws MalformedURLException{
		URL urlToBeReturned = new URL("http://test");
		Mockito.when(amazonS3.generatePresignedUrl(Mockito.any())).thenReturn(urlToBeReturned);
		Asset asset = classUnderTest.uploadAsset();
		Asset updatedAsset = classUnderTest.updateAsset(asset.getId(), Status.UPLOADED);
		Asset assetWithDownloadUrl = classUnderTest.getAsset(updatedAsset.getId(), new Long(0));
		Assert.assertTrue(asset.getDownloadUrl().equals(urlToBeReturned));
	}

}
