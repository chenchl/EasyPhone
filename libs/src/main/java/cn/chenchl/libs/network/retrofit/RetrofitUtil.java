package cn.chenchl.libs.network.retrofit;

import android.text.TextUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.chenchl.libs.Utils;
import cn.chenchl.libs.network.bean.IModel;
import cn.chenchl.libs.network.cookie.CookieJarImpl;
import cn.chenchl.libs.network.cookie.MemoryCookieStore;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ccl on 2016/12/24.
 */

public class RetrofitUtil {
    private static NetProvider sProvider = null;

    private Map<String, NetProvider> providerMap = new HashMap<>();
    private Map<String, Retrofit> retrofitMap = new HashMap<>();
    private Map<String, OkHttpClient> clientMap = new HashMap<>();

    public static final long DefConnectTimeoutMills = 15 * 1000l;
    public static final long DefReadTimeoutMills = 15 * 1000l;
    public static final long DefWriteTimeoutMills = 15 * 1000l;

    private RetrofitUtil() {
    }

    private static class Holder {
        private static RetrofitUtil INSTANCE = new RetrofitUtil();
    }

    public static RetrofitUtil getInstance() {
        return Holder.INSTANCE;
    }


    public static <S> S get(String baseUrl, Class<S> service) {
        return getInstance().getRetrofit(baseUrl, true).create(service);
    }

    public static void registerProvider(NetProvider provider) {
        RetrofitUtil.sProvider = provider;
    }

    public static void registerProvider(String baseUrl, NetProvider provider) {
        getInstance().providerMap.put(baseUrl, provider);
    }


    public Retrofit getRetrofit(String baseUrl, boolean useRx) {
        return getRetrofit(baseUrl, null, useRx);
    }


    public Retrofit getRetrofit(String baseUrl, NetProvider provider, boolean useRx) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (retrofitMap.get(baseUrl) != null) return retrofitMap.get(baseUrl);

        if (provider == null) {
            provider = providerMap.get(baseUrl);
            if (provider == null) {
                provider = sProvider;
            }
        }
        checkProvider(provider);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getClient(baseUrl, provider))
                .addConverterFactory(GsonConverterFactory.create());
        if (useRx) {
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        }

        Retrofit retrofit = builder.build();
        retrofitMap.put(baseUrl, retrofit);
        providerMap.put(baseUrl, provider);

        return retrofit;
    }

    private OkHttpClient getClient(String baseUrl, NetProvider provider) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (clientMap.get(baseUrl) != null) return clientMap.get(baseUrl);

        checkProvider(provider);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(provider.configConnectTimeoutMills() != 0
                ? provider.configConnectTimeoutMills()
                : DefConnectTimeoutMills, TimeUnit.MILLISECONDS);
        builder.readTimeout(provider.configReadTimeoutMills() != 0
                ? provider.configReadTimeoutMills() : DefReadTimeoutMills, TimeUnit.MILLISECONDS);
        builder.writeTimeout(provider.configWriteTimeoutMills() != 0
                ? provider.configWriteTimeoutMills() : DefWriteTimeoutMills, TimeUnit.MILLISECONDS);

        CookieJar cookieJar = provider.configCookie();
        if (cookieJar == null) {
            cookieJar = new CookieJarImpl(new MemoryCookieStore());
        }
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        provider.configHttps(builder);
        /*builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });*/
        if (provider.configLogEnable()) {
            builder.addInterceptor(new LogInterceptor());
        }

        //缓存
        if (provider.isNeedCache()) {
            // 缓存目录
            File file = new File(Utils.getApp().getFilesDir(), "okHttp_cache");
            // 缓存大小 10m
            int cacheSize = 10 * 1024 * 1024;
            builder.cache(new Cache(file, cacheSize));
            builder.addInterceptor(new CacheInterceptor());
        }

        Interceptor[] interceptors = provider.configInterceptors();
        if (interceptors != null && interceptors.length != 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        OkHttpClient client = builder.build();
        clientMap.put(baseUrl, client);
        providerMap.put(baseUrl, provider);

        return client;
    }


    private void checkProvider(NetProvider provider) {
        if (provider == null) {
            throw new IllegalStateException("must register provider first");
        }
    }

    public static NetProvider getCommonProvider() {
        return sProvider;
    }

    public Map<String, Retrofit> getRetrofitMap() {
        return retrofitMap;
    }

    public Map<String, OkHttpClient> getClientMap() {
        return clientMap;
    }

    public static void clearCache() {
        getInstance().retrofitMap.clear();
        getInstance().clientMap.clear();
    }

    /**
     * 异常处理变换
     *
     * @return
     */
    public static <T extends IModel> FlowableTransformer<T, T> getDefaultTransformer() {

        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.flatMap(new Function<T, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(T model) throws Exception {
                        if (!model.isSuccess()) {
                            switch (model.responseCode()) {
                                case NetError.ServerError:
                                    return Flowable.error(new NetError(model.responseMsg(), NetError.ServerError));
                                case NetError.NoLogin:
                                    return Flowable.error(new NetError(model.responseMsg(), NetError.NoLogin));
                                case NetError.OthrerDevice:
                                    return Flowable.error(new NetError(model.responseMsg(), NetError.OthrerDevice));
                                default:
                                    return Flowable.just(model);
                            }
                        } else {
                            return Flowable.just(model);
                        }
                        /*if (model.getCode() == 500 || !model.isSuccess()) {
                            return Flowable.error(new NetError(model.getMsg(), ServerError));
                        } else if (model.isAuthError()) {
                            return Flowable.error(new NetError(model.getErrorMsg(), NetError.AuthError));
                        } else if (model.isBizError()) {
                            return Flowable.error(new NetError(model.getErrorMsg(), NetError.BusinessError));
                        }*/
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


}
