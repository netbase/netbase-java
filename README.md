netbase-java
============

A Java wrapper library for the RESTful NetBase Insight API

Using
============
The library is quite simple and easy to use. You can use the basic authentication or OAuth authentication.

	NetBase nb = NetBaseFactory.getInstance("account", "pwd", true);
	//NetBase nb_oauth = NetBaseFactory.getInstance("access_token", true);
	
	Request req = new Request();
	req.addQueryParam("scope", "USER");
	try {
		Response res = nb.topics(req);
	}
	catch (ParamErrorException e) {
		return;
	}
	catch (ApiErrorException e) {
		return;
	}

Once you have registered your client application, this library help you get/refresh tokens in few steps.

	NetbaseOAuth oauth = NetBaseFactory.getOAuthInstance("redirectUri", "clientId", "clientSecret", true);

	String refreshToken = null;
	String accessToken = null;

	try {
		TokenResponse tokenResponse = oauth.getAccessTokenByAuthCode("authorization code");
		refreshToken = tokenResponse.getRefreshToken();
		accessToken = tokenResponse.getAccessToken();
	}
	catch (ApiErrorException e) {
		return;
	}
	catch (ParamErrorException e) {
		return;
	}

You can find more demos under the `package com.netbase.insightapi.v2.samples`.


License
============ 
This project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).