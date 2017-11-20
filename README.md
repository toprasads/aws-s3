# aws-s3
Asset uploader service

Creates an S3 asset uploader API as described below:
1. The service has an http POST endpoint to upload a new asset.
POST /asset
Body: empty
Example response
{
    "id": "2eb1fe43-1dec-40a9-a29f-1b9c809def04",
    "uploadUrl": "https://some-signed-url",
    "downloadUrl": null,
    "downloadTimeout": 0,
    "status": "PENDING_UPLOAD"
}
2. The user can make a POST call to the s3 signed url to upload the asset
Response will be an HTTP response of 200 OK

3. To mark the upload operation as complete, the service provides a PUT endpoint
as follows:
PUT /asset/<asset-id>
Body in the request should have a field with name "Status" and value "UPLOADED" OR "PENDING_UPLOAD"
Example Body:
{
“Status”: “UPLOADED”
}
Sample response:
{
    "id": "5d3b9d67-e5f2-46f6-a04e-986862277aac",
    "uploadUrl": "https://some url"
    "downloadUrl": 
    "downloadTimeout": 0,
    "status": "UPLOADED"
}
4. When a Get request is made on the asset, the service returns an s3 signed url for
download with the timeout in seconds as a url parameter. If the timeout is not specified
this service endpoint assumes 60 seconds.
GET /asset/<asset-id>?timeout=100
Sample response
{
    "id": "5d3b9d67-e5f2-46f6-a04e-986862277aac",
    "uploadUrl": "https://some url"
    "downloadUrl": "https://some url"
    "downloadTimeout": 60,
    "status": "UPLOADED"
}
A get call made on an asset which has not been set to “status: UPLOADED” will return
an error with message indicating that either
          1. Asset does not exist OR
          2. The asset does not have a status of UPLOADED
5. The uploaded asset can be fetched successfully using the returned signed download url.

TO BUILD AND RUN THIS SERVICE (make sure you have Java version 8 installed)
1. git clone this project locally
2. change directory to the top level of this cloned project => cd aws-s3
3. run command: "./mvnw clean install" => this will build and run the unit tests
4. change directory to "./target/
5. Run "java -jar aws-s3-0.0.1-SNAPSHOT.jar" => this will start the service

Now from a Rest Client (e.g. Postman), you can issue REST requests as detailed above to upload/download assets

TODO:
1. This service does not persist the IDs of the assets. They are all in memory at the moment. Implement the Repository pattern and 
hook up a database to this service so that the IDs are persisted
2. Add integration tests for the REST controller.
