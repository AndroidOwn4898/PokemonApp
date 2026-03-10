import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class RetryInterceptor implements Interceptor {
    private final int maxRetries;

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                response = chain.proceed(request);
                // If response is successful, then return it
                if (response.isSuccessful()) {
                    return response;
                }
                // If response is not successful, log it and retry
            } catch (IOException e) {
                lastException = e; // Keep track of the last exception
            }
        }

        // If we reach here, it means retries are exhausted. Throw the last exception.
        throw lastException != null ? lastException : new IOException("Failed to get a successful response.");
    }
}