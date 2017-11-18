package work.uet.anhdt.ftpstorageofficial.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import work.uet.anhdt.ftpstorageofficial.util.Constant;

/**
 * Created by anansaj on 11/18/2017.
 */

public class InitServiceRetrofit {

    public static InitServiceRetrofit _instance;

    private OkHttpClient.Builder httpClient;

    public static InitServiceRetrofit getInstance() {
        if (_instance == null) {
            _instance = new InitServiceRetrofit();
        }
        return _instance;
    }

    private static Retrofit.Builder builderNormal =
            new Retrofit.Builder()
                    .baseUrl(Constant.SERVER)
                    .addConverterFactory(GsonConverterFactory.create());


    public <S> S createService(Class<S> serviceClass) {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builderNormal.client(client).build();
        return retrofit.create(serviceClass);
    }

}
