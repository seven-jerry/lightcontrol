package jerry.util;

public interface IWebsocketErrorHandler {
    void socketHasClosed(Integer id,int statusCode, String reason);
    void socketErrorReceived(Integer id,Throwable t);
}
