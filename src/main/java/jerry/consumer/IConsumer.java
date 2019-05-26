package jerry.consumer;

public interface IConsumer {
    void write(String message) throws Exception;
    void handleError(Exception e);
    void closeConnection();
}
