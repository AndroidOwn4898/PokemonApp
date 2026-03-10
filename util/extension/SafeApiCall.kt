import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SafeApiCall {
    public static <T> void callApi(Call<T> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}

interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(Throwable t);
}