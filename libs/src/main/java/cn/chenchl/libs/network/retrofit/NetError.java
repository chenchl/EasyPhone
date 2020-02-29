package cn.chenchl.libs.network.retrofit;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.UnknownHostException;

/**
 * Created by ccl on 2016/12/24.
 */

public class NetError extends Throwable {
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

    public static NetError handleException(Throwable e) {
        NetError error = null;
        if (e != null) {
            if (e instanceof UnknownHostException) {
                error = new NetError(e, NetError.NoConnectError);
            } else if (e instanceof JSONException || e instanceof JsonParseException) {
                error = new NetError(e, NetError.ParseError);
            } else if (e instanceof NetError) {
                error = (NetError) e;
            } else {
                error = new NetError(e, NetError.OtherError);
            }
            /*if (useCommonErrorHandler()
                    && RetrofitUtil.getCommonProvider() != null) {
                if (RetrofitUtil.getCommonProvider().handleError(error)) {        //使用通用异常处理
                    return;
                }
            }
            onFail(error);*/
        } else {
            error = new NetError(new RuntimeException("unknown Exception"), NetError.OtherError);
        }
        return error;
    }
}
