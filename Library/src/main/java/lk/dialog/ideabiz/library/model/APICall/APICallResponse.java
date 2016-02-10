package lk.dialog.ideabiz.library.model.APICall;

/**
 * Created by Malinda_07654 on 1/31/2016.
 */
public class APICallResponse {
    int statusCode;
    String body;
    Exception error;
    Long exeTime;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public Long getExeTime() {
        return exeTime;
    }

    public void setExeTime(Long exeTime) {
        this.exeTime = exeTime;
    }
}
