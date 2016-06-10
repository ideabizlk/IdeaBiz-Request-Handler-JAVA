## Ideabiz Auth Handler sample 

This is spring maven sample project. This project created sample  `MySQLIdeabizOAuthDataProviderImpl` to save and read tokens on Mysql DB
This project All object created on XML. 

### Flow
* Maven install bellow projects

	Ideabiz-APICallHandler (https://github.com/ideabizlk/IdeaBiz-Request-Handler-JAVA)
	ideabiz-common-java-class (https://github.com/ideabizlk/Classes-JAVA)
* Run `Database.sql` on mysql DB
* Create Ideabiz App
* Subscribe for relevent API's (SMS for this sample)
* Generate `consumer key`, `consumer secret`, `refresh token`, `access token` 
* add above to newly created table
* config `database.properties`
* Deply project on tomcat or run as meven goal `jetty:run` (this project can run independently with IDE)
* Send SMS inboud API call to `{serverURL}/inbound/inbound/SMS

	
### DB Config
```
#Application database
jdbc.OAUTH.driverClassName=com.mysql.jdbc.Driver
jdbc.OAUTH.url=jdbc:mysql://localhost:3306/sampleapp
jdbc.OAUTH.username=user
jdbc.OAUTH.password=password
```

### Bean Initialization in initialization.xml

```
 <!--Setting Database connection-->
    <bean id="OAuthDatabase" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="${jdbc.OAUTH.driverClassName}"/>
        <property name="url" value="${jdbc.OAUTH.url}?useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="username" value="${jdbc.OAUTH.username}"/>
        <property name="password" value="${jdbc.OAUTH.password}"/>
    </bean>

    <!--Construct Mysql class with datasource-->
    <bean id="OAuthDataSourceLibrary"
          class="lk.dialog.ideabiz.library.APICall.DataProvider.MySQLIdeabizOAuthDataProviderImpl">
        <property name="AuthDataSource" ref="OAuthDatabase"/>
    </bean>

    <!--Construct API call library.  -->
    <bean id="IdeabizAPICall" class="lk.dialog.ideabiz.library.APICall.APICall">
        <constructor-arg value="${application.http.timeout}"/>
        <constructor-arg ref="OAuthDataSourceLibrary"/>
    </bean>

    <!--Construct library manager for shared libs-->
    <bean id="LibraryManager" class="lk.dialog.ideabiz.library.LibraryManager">
        <property name="apiCall" ref="IdeabizAPICall"/>
    </bean>
```

### Without Bean XML can use

```
    public void sampleWithoutXMLBean(){
	
		//Setup Data Source
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/sampleapp");
        driverManagerDataSource.setUsername("user");
        driverManagerDataSource.setPassword("password");

		//Create OAuth Data Provider
        MySQLIdeabizOAuthDataProviderImpl mySQLIdeabizOAuthDataProvider =new MySQLIdeabizOAuthDataProviderImpl();
        mySQLIdeabizOAuthDataProvider.setAuthDataSource(driverManagerDataSource);

		//Construct API Call
        APICall apiCall = new APICall(3000,mySQLIdeabizOAuthDataProvider);
		
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

    }
```