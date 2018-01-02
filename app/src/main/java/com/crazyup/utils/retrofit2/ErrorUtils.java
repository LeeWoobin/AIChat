package com.crazyup.utils.retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by jeongmin on 17. 3. 20.
 */

public class ErrorUtils {

    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter = RetrofitUtil.getRetrofit()
                        .responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
            error.setResponse(response.code(), response.message());
        } catch (IOException e) {
            e.printStackTrace();
            error = new ApiError();
            error.setResponse(response.code(), response.message());
//            return new ApiError();
        }

        return error;
    }
}
