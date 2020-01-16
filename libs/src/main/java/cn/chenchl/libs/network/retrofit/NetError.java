package cn.chenchl.libs.network.retrofit;

/**
 * Created by ccl on 2016/12/24.
 */

public class NetError extends Exception {
    private Throwable exception;
    private int type = ServerError;

    public static final int Success = 200;   //数据解析异常
    public static final int ServerError = 500;   //数据解析异常
    public static final int NoLogin = 4010;   //未登录异常
    public static final int OthrerDevice = 2010;   //其他设备登陆异常
    public static final int OtherError = 5;   //其他异常
    public static final int NoConnectError = 1;   //无连接异常
    public static final int ParseError = 0;   //数据解析异常

    public NetError(Throwable exception, int type) {
        this.exception = exception;
        this.type = type;
    }

    public NetError(String detailMessage, int type) {
        super(detailMessage);
        this.type = type;
    }

    @Override
    public String getMessage() {
        if (exception != null) return exception.getMessage();
        return super.getMessage();
    }

    public int getType() {
        return type;
    }
}
