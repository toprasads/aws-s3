package com.prasad.aws.s3;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssetController {
	
	@Autowired
	AssetService assetService;
	
	@RequestMapping(value = "/asset", method = RequestMethod.POST)
	public Asset uploadAsset(){
		Asset asset = null;
		String error = "";
		try {
			asset = assetService.uploadAsset();
		} catch (MalformedURLException e) {
			error = e.getMessage();
		}
		return asset;//TODO: handle error
	}
	
	@RequestMapping(value = "/asset/{id}", method = RequestMethod.PUT)
	public Asset updateAsset(@PathVariable("id") String assetId, @RequestBody Asset asset){
		return assetService.updateAsset(assetId, asset.getStatus());
	}
	
	@RequestMapping(value = "/asset/{id}", method = RequestMethod.GET)
	public Asset getAsset(@PathVariable("id") String assetId, @RequestParam(value="timeout", required = false) Long timeout) throws MalformedURLException{
		return assetService.getAsset(assetId,  timeout);
	}

}
