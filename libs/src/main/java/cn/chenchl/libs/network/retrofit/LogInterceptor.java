package cn.chenchl.libs.network.retrofit;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.chenchl.libs.log.LogUtil;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by chenchl on 2018/6/7.
 */

public class LogInterceptor implements Interceptor {
    private static final String TAG = "chenchl_Net";

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logRequest(request);
        Response response = chain.proceed(request);
        return logResponse(response);
    }


    private void logRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            LogUtil.INSTANCE.i(TAG, "url : " + url);
            LogUtil.INSTANCE.i(TAG, "method : " + request.method());
            if (headers != null && headers.size() > 0) {
                LogUtil.INSTANCE.i(TAG, "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        LogUtil.INSTANCE.i(TAG, "params : " + bodyToString(request));
                    } else {
                        LogUtil.INSTANCE.i(TAG, "params : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response logResponse(Response response) {
        try {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            if (clone.networkResponse() != null) {
                LogUtil.INSTANCE.i(TAG, "response: from network" + clone.request().url().toString());
            } else {
                LogUtil.INSTANCE.i(TAG, "response: from cache" + clone.request().url().toString());
            }
            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        String resp = body.string();
                        //LogUtil.INSTANCE.i(TAG, "response:" + clone.request().url().toString());
                        LogUtil.INSTANCE.i(TAG, "header:" + clone.headers().toString());
                        LogUtil.INSTANCE.json(resp);
                        body = ResponseBody.create(mediaType, resp);
                        return response.newBuilder().body(body).build();
                    } else {
                        LogUtil.INSTANCE.i(TAG, "data : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    private boolean isText(MediaType mediaType) {
        if (mediaType == null) return false;

        return ("text".equals(mediaType.subtype())
                || "json".equals(mediaType.subtype())
                || "xml".equals(mediaType.subtype())
                || "html".equals(mediaType.subtype())
                || "webviewhtml".equals(mediaType.subtype())
                || "x-www-form-urlencoded".equals(mediaType.subtype()));
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            if (copy.body() != null) {
                copy.body().writeTo(buffer);
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
