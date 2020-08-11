package me.charghet.tools.requests;

/**
 * 在请求过程中发生错误。
 * @author charghet
 *
 */
public class RequestException extends Exception{
    private static final long serialVersionUID = 1L;
    /**
     * 构造带指定详细消息新异常。
     * 
     * @param message 详细消息
     */
    public RequestException(String message) {
        super(message);
    }
    /**
     * 构造带指定详细消息和原因的新异常。
     * 
     * @param message 详细消息
     * @param throwable 原因
     */
    public RequestException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
