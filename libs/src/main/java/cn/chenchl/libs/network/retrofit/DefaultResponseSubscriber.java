package cn.chenchl.libs.network.retrofit;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.UnknownHostException;

import cn.chenchl.libs.log.LogUtil;
import cn.chenchl.libs.network.bean.IModel;
import io.reactivex.subscribers.DisposableSubscriber;


/**
 * Created by ccl on 2016/12/26.
 */

public abstract class DefaultResponseSubscriber<T extends IModel, D> extends DisposableSubscriber<T> {

    @Override
    public void onNext(T t) {
        LogUtil.INSTANCE.e(t.toString());
        if (t.isSuccess()) {
            onSuccess((D) t.responseData());
        } else {
            switch (t.responseCode()) {
                case NetError.ServerError:
                    onFail(new NetError(t.responseMsg(), NetError.ServerError));
                case NetError.NoLogin:
                    onFail(new NetError(t.responseMsg(), NetError.NoLogin));
                case NetError.OthrerDevice:
                    onFail(new NetError(t.responseMsg(), NetError.OthrerDevice));
                default:
                    onFail(new NetError(t.responseMsg(), NetError.OtherError));
                    break;
            }
        }

    }

    @Override
    public void onError(Throwable e) {
        NetError error;
        if (e != null) {
            if (e instanceof UnknownHostException) {
                error = new NetError(e, NetError.NoConnectError);
            } else if (e instanceof JSONException || e instanceof JsonParseException) {
                error = new NetError(e, NetError.ParseError);
            } else {
                error = new NetError(e, NetError.OtherError);
            }
            if (useCommonErrorHandler()
                    && RetrofitUtil.getCommonProvider() != null) {
                if (RetrofitUtil.getCommonProvider().handleError(error)) {        //使用通用异常处理
                    return;
                }
            }
            onFail(error);
        }

    }

    protected abstract void onSuccess(D data);

    protected abstract void onFail(NetError error);

    @Override
    public void onComplete() {

    }


    private boolean useCommonErrorHandler() {
        return false;
    }

}
