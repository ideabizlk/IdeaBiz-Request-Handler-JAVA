package lk.dialog.ideabiz.library.APICall;

import com.google.gson.Gson;
import lk.dialog.ideabiz.library.APICall.DataProvider.IdeabizOAuthDataProviderInterface;
import lk.dialog.ideabiz.library.model.APICall.APICallResponse;
import lk.dialog.ideabiz.library.model.APICall.OAuth2Model;
import lk.dialog.ideabiz.library.model.APICall.OAuth2RefreshResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Malinda_07654 on 2/7/2016.
 */
public class OAuth2Handler {
    Logger logger;
    Gson gson;
    IdeabizOAuthDataProviderInterface ideabizOAuthDataProviderInterface;
    APICall apicall;

    /***
     * refresh the token
     *
     * @param id
     * @return
     * @throws Exception
     */
    public synchronized OAuth2Model refreshToken(int id) throws Exception {
        OAuth2Model oAuth2Model = ideabizOAuthDataProviderInterface.getToken(id);
        if (oAuth2Model == null)
            throw new Exception("Cant find auth for ID");

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + base64Encode(oAuth2Model.getConsumerKey() + ":" + oAuth2Model.getConsumerSecret()));

        logger.debug("REFRESH " + id + " , RT :  " + oAuth2Model.getRefreshToken() + " , AT : " + oAuth2Model.getAccessToken() + " , CC : " + oAuth2Model.getConsumerKey());
        APICallResponse apiCallResponse = apicall.sendAPICall(oAuth2Model.getTokenURL() + "?grant_type=refresh_token&refresh_token=" + oAuth2Model.getRefreshToken() + "&scope=" + oAuth2Model.getScope(),
                "POST", headers, null, false);

        logger.info("Refresh :" + id + ":" + apiCallResponse.getStatusCode());
        logger.debug("Refresh :" + apiCallResponse.getBody());

        if (apiCallResponse.getStatusCode() == 200) {
            try {
                OAuth2RefreshResponse oAuth2RefreshResponse = gson.fromJson(apiCallResponse.getBody(), OAuth2RefreshResponse.class);
                if (oAuth2RefreshResponse != null && oAuth2RefreshResponse.getAccess_token() != null && oAuth2Model.getAccessToken().length() > 1) {
                    ideabizOAuthDataProviderInterface.updateToken(id, oAuth2RefreshResponse.getAccess_token(), oAuth2RefreshResponse.getRefresh_token(), oAuth2RefreshResponse.getExpires_in());
                    oAuth2Model.setAccessToken(oAuth2RefreshResponse.getAccess_token());
                    oAuth2Model.setRefreshToken(oAuth2RefreshResponse.getRefresh_token());
                    oAuth2Model.setExpire(Long.parseLong(oAuth2RefreshResponse.getExpires_in()));
                    return oAuth2Model;
                } else {
                    throw new Exception("Refreshing token error : Invalid response");
                }

            } catch (Exception e) {
                throw new Exception("Refreshing token error: " + apiCallResponse.getError().getMessage());
            }
        } else if (apiCallResponse.getStatusCode() == 400 && oAuth2Model.canCreateAccessToken() && apiCallResponse.getBody().contains("invalid_grant")) {
            oAuth2Model = createNewAccessToken(oAuth2Model);
            return oAuth2Model;

        } else {
            logger.error("Refreshing token error.:" + apiCallResponse.getError().getMessage());
            throw new Exception("Refreshing token error: " + apiCallResponse.getError().getMessage());
        }

    }

    public synchronized OAuth2Model createNewAccessToken(OAuth2Model oAuth2Model) throws Exception {


        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + base64Encode(oAuth2Model.getConsumerKey() + ":" + oAuth2Model.getConsumerSecret()));
        logger.debug("CREATE " + oAuth2Model.getId() + " , RT :  " + oAuth2Model.getRefreshToken() + " , AT : " + oAuth2Model.getAccessToken() + " , CC : " + oAuth2Model.getConsumerKey());

        APICallResponse apiCallResponse = apicall.sendAPICall(oAuth2Model.getTokenURL() + "?grant_type=password&username=" + URLEncoder.encode(oAuth2Model.getUsername()) + "&password=" + URLEncoder.encode(oAuth2Model.getPassword()) + "&scope=" + oAuth2Model.getScope(),
                "POST", headers, null, false);

        logger.info("Creating new token :" + oAuth2Model.getId() + ":" + apiCallResponse.getStatusCode());
        logger.debug("Refresh :" + apiCallResponse.getBody());

        if (apiCallResponse.getStatusCode() == 200) {
            try {
                OAuth2RefreshResponse oAuth2RefreshResponse = gson.fromJson(apiCallResponse.getBody(), OAuth2RefreshResponse.class);
                if (oAuth2RefreshResponse != null && oAuth2RefreshResponse.getAccess_token() != null && oAuth2Model.getAccessToken().length() > 1) {
                    ideabizOAuthDataProviderInterface.updateToken(oAuth2Model.getId(), oAuth2RefreshResponse.getAccess_token(), oAuth2RefreshResponse.getRefresh_token(), oAuth2RefreshResponse.getExpires_in());
                    oAuth2Model.setAccessToken(oAuth2RefreshResponse.getAccess_token());
                    oAuth2Model.setRefreshToken(oAuth2RefreshResponse.getRefresh_token());
                    oAuth2Model.setExpire(Long.parseLong(oAuth2RefreshResponse.getExpires_in()));
                    return oAuth2Model;
                } else {
                    throw new Exception("Creating token error : Invalid response");
                }

            } catch (Exception e) {
                throw new Exception("Creating token error: " + apiCallResponse.getError().getMessage());
            }

        } else {
            logger.error("Creating token error.:" + apiCallResponse.getError().getMessage());
            throw new Exception("Creating token error: " + apiCallResponse.getError().getMessage());
        }
    }

    public IdeabizOAuthDataProviderInterface getDataProvider() {
        return ideabizOAuthDataProviderInterface;
    }

    public void setDataProvider(IdeabizOAuthDataProviderInterface dataProvider) {
        this.ideabizOAuthDataProviderInterface = dataProvider;
    }

    public OAuth2Handler(IdeabizOAuthDataProviderInterface ideabizOAuthDataProviderInterface, APICall apicall) {
        this.logger = Logger.getLogger(OAuth2Handler.class);
        this.gson = new Gson();
        this.ideabizOAuthDataProviderInterface = ideabizOAuthDataProviderInterface;
        this.apicall = apicall;
    }

    public String base64Encode(String text) {
        byte[] encodedBytes = Base64.encodeBase64(text.getBytes());
        return new String(encodedBytes);

    }


}
