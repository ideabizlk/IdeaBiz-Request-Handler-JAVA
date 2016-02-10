package lk.dialog.ideabiz.library.APICall;

import com.google.gson.Gson;
import lk.dialog.ideabiz.library.APICall.DataProvider.DataProviderInterface;
import lk.dialog.ideabiz.library.model.APICall.APICallResponse;
import lk.dialog.ideabiz.library.model.APICall.OAuth2Model;
import lk.dialog.ideabiz.library.model.APICall.OAuth2RefreshResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by Malinda_07654 on 2/7/2016.
 */
class OAuth2Handler {
    Logger logger;
    Gson gson;
    DataProviderInterface dataProvider;
    APICall apicall;

    /***
     * refresh the token
     * @param id
     * @return
     * @throws Exception
     */
    public OAuth2Model refreshToken(int id) throws Exception {
        OAuth2Model oAuth2Model = dataProvider.getToken(id);
        if (oAuth2Model == null)
            throw new Exception("Cant find auth for ID");

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + base64Encode(oAuth2Model.getConsumerKey() + ":" + oAuth2Model.getConsumerSecret()));

        APICallResponse apiCallResponse = apicall.sendAPICall(oAuth2Model.getTokenURL() + "?grant_type=refresh_token&refresh_token=" + oAuth2Model.getRefreshToken() + "&scope=" + oAuth2Model.getScope(),
                "POST", headers, null, false);

        logger.info("Refresh :" + id + ":" + apiCallResponse.getStatusCode() );
        logger.debug("Refresh :" + apiCallResponse.getBody());

        if (apiCallResponse.getStatusCode() == 200) {

            OAuth2RefreshResponse oAuth2RefreshResponse = gson.fromJson(apiCallResponse.getBody(), OAuth2RefreshResponse.class);
            dataProvider.updateToken(id, oAuth2RefreshResponse.getAccess_token(), oAuth2RefreshResponse.getRefresh_token(), oAuth2RefreshResponse.getExpires_in());
            oAuth2Model.setAccessToken(oAuth2RefreshResponse.getAccess_token());
            oAuth2Model.setRefreshToken(oAuth2RefreshResponse.getRefresh_token());
            oAuth2Model.setExpire(Long.parseLong(oAuth2RefreshResponse.getExpires_in()));
            return oAuth2Model;
        } else {
            logger.error("Refreshing token error.:" + apiCallResponse.getError().getMessage());
            throw new Exception("Refreshing token error: " + apiCallResponse.getError().getMessage());
        }

    }

    public DataProviderInterface getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProviderInterface dataProvider) {
        this.dataProvider = dataProvider;
    }

    public OAuth2Handler(DataProviderInterface dataProvider, APICall apicall) {
        this.logger = Logger.getLogger(OAuth2Handler.class);
        this.gson = new Gson();
        this.dataProvider = dataProvider;
        this.apicall = apicall;
    }

    public String base64Encode(String text) {
        byte[] encodedBytes = Base64.encodeBase64(text.getBytes());
        return new String(encodedBytes);

    }


}
