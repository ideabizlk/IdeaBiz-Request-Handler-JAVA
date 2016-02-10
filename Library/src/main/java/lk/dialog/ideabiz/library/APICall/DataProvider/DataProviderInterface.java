package lk.dialog.ideabiz.library.APICall.DataProvider;

import lk.dialog.ideabiz.library.model.APICall.OAuth2Model;

/**
 * Created by Malinda_07654 on 2/9/2016.
 */
public interface DataProviderInterface {
    public OAuth2Model getToken(int id);
    public void updateToken(int id, String accessToken, String refreshToken, String expire);
}

