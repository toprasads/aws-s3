package com.prasad.aws.s3;

import java.net.MalformedURLException;
import java.net.URL;

public interface AssetService {
	Asset uploadAsset() throws MalformedURLException;

	Asset updateAsset(String id, Status status);

	Asset getAsset(String id, Long timeout) throws MalformedURLException;
}
