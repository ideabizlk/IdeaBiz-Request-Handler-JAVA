package lk.dialog.ideabiz.library.APICall;

import com.google.gson.Gson;
import lk.dialog.ideabiz.library.APICall.DataProvider.IdeabizOAuthDataProviderInterface;
import lk.dialog.ideabiz.library.model.APICall.APICallResponse;
import lk.dialog.ideabiz.library.model.APICall.OAuth2Model;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Malinda on 7/13/2015.
 */
public class APICall {

    //Timeout value in ms for APIcall
    int timeout;
    Logger logger = Logger.getLogger(APICall.class);
    OAuth2Handler oAuth;
    Gson gson;

    /***
     * Send authenticated API call
     * This will automatically insert headers for authorize
     *
     * @param oauthId App Id (this used for use multiple apps on system
     * @param url     full url to APICall
     * @param method  Method GET POST
     * @param headers Header array
     * @param body    raw body
     * @param async   Send API call asynchronous
     * @return
     * @throws Exception
     */
    public APICallResponse sendAuthAPICall(Integer oauthId, String url, String method, Map<String, String> headers, String body, boolean async) throws Exception {


        OAuth2Model oAuth2Model = null;

        //get application from Data interface
        if (oauthId != null)
            oAuth2Model = oAuth.getDataProvider().getToken(oauthId);

        //Validating application and token
        if (oAuth2Model == null) {
            logger.info("AuthAPICall Failed : Not found id " + oauthId);
        } else {
            if (oAuth2Model.getAccessToken() != null && oAuth2Model.getAccessToken().length() > 0) {
                if (headers == null)
                    headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + oAuth2Model.getAccessToken());
            } else if (oAuth2Model.canCreateAccessToken()) {
                oAuth.createNewAccessToken(oAuth2Model);
                if (oAuth2Model.getAccessToken() != null && oAuth2Model.getAccessToken().length() > 0) {
                    if (headers == null)
                        headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + oAuth2Model.getAccessToken());
                } else {
                    logger.info("AuthAPICall Failed : No access token for id " + oauthId);
                }
            } else {
                logger.info("AuthAPICall Failed : No access token for id " + oauthId);

            }
        }


        //Send API call
        APICallResponse apiCallResponse = sendAPICall(url, method, headers, body, async);
        if (oAuth2Model == null)
            return apiCallResponse;

        //Check token expired or not
        if (apiCallResponse.getStatusCode() == 401) {
            if (apiCallResponse.getBody().contains("Expired")) {
                oAuth2Model = oAuth.refreshToken(oAuth2Model.getId());
            } else if (apiCallResponse.getBody().contains("Expired")) {
                oAuth2Model = oAuth.createNewAccessToken(oAuth2Model);
            } else {
                return apiCallResponse;
            }
            headers.put("Authorization", "Bearer " + oAuth2Model.getAccessToken());
            return sendAPICall(url, method, headers, body, async);
        } else {
            return apiCallResponse;
        }
    }

    /***
     * /***
     * Send normal API calls
     *
     * @param url     full url to APICall
     * @param method  Method GET POST
     * @param headers Header array
     * @param body    raw body
     * @return
     * @throws Exception
     */
    public APICallResponse sendAPICall(String url, String method, Map<String, String> headers, String body, boolean async) throws Exception {
        if (async) {
            try {
                AsyncAPICall asyncAPICall = new AsyncAPICall(url, method, headers, body, timeout, this);
                asyncAPICall.start();
                return null;
            } catch (Exception e) {
                logger.warn("Async Error : " + e.getMessage());
            }
        } else {
            return runAPICall(url, method, headers, body);
        }
        return null;
    }


    APICallResponse runAPICall(String url, String method, Map<String, String> headers, String body) throws Exception {
        APICallResponse r = new APICallResponse();

        //this for calculate execution time
        long startTime = System.currentTimeMillis();

        try {
            logger.info("Sending request to URL : " + url);
            logger.debug("Post parameters : " + body);
            logger.debug("Headers : " + gson.toJson(headers));

            URL obj = new URL(url);
            HttpURLConnection con = null;

            //set HTTP or HTTPS
            if (url.startsWith("https"))
                con = (HttpsURLConnection) obj.openConnection();
            else
                con = (HttpURLConnection) obj.openConnection();

            //Set params
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            con.setRequestMethod(method);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


            //setting headers
            try {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {

            }

            //Send body if PUT or POST
            if ((method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) && body != null) {
                try {
                    con.setDoOutput(true);
                    DataOutputStream wr = null;

                    wr = new DataOutputStream(con.getOutputStream());

                    wr.writeBytes(body);
                    wr.flush();
                    wr.close();
                } catch (IOException e1) {

                }
            }


            int responseCode = con.getResponseCode();
            logger.info("Response Code : " + responseCode + " :  " + url);

            BufferedReader in;
            if (responseCode >= 400) {
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), "UTF-8"));
            } else {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "UTF-8"));
            }


            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.debug("Response  : " + response.toString() + " : " + url);

            r.setBody(response.toString());
            r.setStatusCode(responseCode);


        } catch (Exception e) {
            logger.error("API CALL FAILED " + e.getMessage() + " : " + url);
            r.setError(e);
        }

        //Calculate execution time
        long duration = (System.currentTimeMillis() - startTime);
        r.setExeTime(duration);

        return r;
    }

    /***
     * @param timeout
     * @param ideabizOAuthDataProviderInterface
     */
    public APICall(Integer timeout, IdeabizOAuthDataProviderInterface ideabizOAuthDataProviderInterface) {
        this.timeout = timeout;
        this.oAuth = new OAuth2Handler(ideabizOAuthDataProviderInterface, this);
        this.gson = new Gson();

    }

    public OAuth2Handler getoAuth() {
        return oAuth;
    }


}
