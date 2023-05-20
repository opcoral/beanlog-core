package io.github.opcoral.beanlog.exception;

/**
 * 操作日志的异常<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-12 17:31
 */
public class BeanLogException extends RuntimeException {

    private Exception targetException;

    public BeanLogException() {
    }

    public BeanLogException(String message) {
        super(message);
    }

    public BeanLogException(String message, Exception targetException) {
        super(message);
        this.targetException = targetException;
    }

    public BeanLogException(Exception targetException) {
        super(targetException);
        this.targetException = targetException;
    }

    public BeanLogException(String format, String... params) {
        super(format(format, params));
    }

    private static String format(String format, String... params) {
        String res = format;
        for(String p : params) {
            res = res.replaceFirst("\\{}", p);
        }
        return res;
    }

    public Exception getTargetException() {
        return targetException;
    }

    public String getMessage() {
        return super.getMessage();
    }
}
