package lk.dialog.ideabiz.library.APICall;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by Malinda on 7/15/2015.
 */
 class AsyncAPICall extends Thread {

    public static int ASYNC_APICALL_COUNT = 0;
    public static int ASYNC_APICALL_FINISH = 0;

    final static Logger logger = Logger.getLogger(AsyncAPICall.class);

    String url;
    String method;
    Map<String, String> headers;
    String body;
    int timeout;
    Boolean sendViaRouter;
    APICall apiCall;

    public AsyncAPICall(String url, String method, Map<String, String> headers, String body, int timeout,APICall apiCall) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.timeout = timeout;
        this.apiCall = apiCall;
    }

    public void run() {
        ASYNC_APICALL_COUNT++;
        logger.debug("START SYNC FINISH/PENDING/TOTAL : " + ASYNC_APICALL_FINISH + "/ " + (ASYNC_APICALL_COUNT - ASYNC_APICALL_FINISH) + "/" + ASYNC_APICALL_COUNT);
        try {
            apiCall.runAPICall(url, method, headers, body);
        } catch (Exception e) {
            logger.warn("ASYNC : " + e.getMessage());
        }
        ASYNC_APICALL_FINISH++;
        logger.debug("END SYNC  FINISH/PENDING/TOTAL : " + ASYNC_APICALL_FINISH + "/ " + (ASYNC_APICALL_COUNT - ASYNC_APICALL_FINISH) + "/" + ASYNC_APICALL_COUNT);

    }
}
