## Java Auth Library for Ideabiz

This module manage ideabiz tokens and it will refresh token and continue API calls
 
For setup this code, you need to include this module to your project
Then Impliment `lk.dialog.ideabiz.library.APICall.DataProvider.DataProviderInterface` Class

##DataProviderInterface
With your data management machanisame,  save token and provide way to read and update token
So you can impliment this based on MYSQL or File. Please refer sample code for this

## Usage
### Construct APICall 

     * @param timeout in ms
     * @param ideabizOAuthDataProviderInterface
	 
```
  //Contruct API Call
  APICall apiCall = new APICall(1000,new MySQLIdeabizOAuthDataProviderImpl());
```




### Send authenticated API call

     * @param `oauthId` App Id (this for use multiple apps on one system)
     * @param `url`     full url to APICall
     * @param `method`  Method GET POST PUT DELETE
     * @param `headers` Header array
     * @param `body`    raw body as string
     * @param `async`   Send API call asynchronous or not
     * @return
     * @throws Exception
 
 
```
  //Setting headers
  Map<String, String> header = new HashMap<String, String>();
  header.put("Content-Type", "application/json");
  header.put("Accept", "application/json");
		
  try {
	//Send API Call
    apiCall.sendAuthAPICall(1, "https://ideabiz.lk/apicall/api/url/here", "POST", header, "{SAMPLE BODY}", false);
  } catch (Exception e) {
    e.printStackTrace();
  }
```
