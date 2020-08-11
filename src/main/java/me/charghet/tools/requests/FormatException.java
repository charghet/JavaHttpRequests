package me.charghet.tools.requests;

/**
 * 对url字符串或url参数进行urlencode编码过程中发生错误。
 * @author charghet
 *
 */
public class FormatException extends Exception{
    private static final long serialVersionUID = 1L;
    /**
     * 构造带指定详细消息的新异常。
     * 
     * @param message 详细消息
     */
    public FormatException(String message) {
        super(message);
    }
    /**
     * 构造带指定详细消息和原因的新异常。
     * 
     * @param message 详细消息
     * @param throwable 原因
     */
    public FormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
