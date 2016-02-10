package lk.dialog.ideabiz.library;


import lk.dialog.ideabiz.library.APICall.APICall;
import org.apache.log4j.Logger;

/**
 * Created by Malinda_07654 on 2/9/2016.
 */
public class LibraryManager {
    public static APICall apiCall;

    public static APICall getApiCall() {
        return apiCall;
    }

    public void setApiCall(APICall apiCall) {
        this.apiCall = apiCall;
    }
    public LibraryManager() {

    }
}
