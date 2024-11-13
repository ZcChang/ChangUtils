package com.example.changutils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApkDownloader {
    private static final String TAG = "ApkDownloader";

    public static void downloadApk(Context context, String url, File outputFile, DownloadCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(new IOException("Failed to download file: " + response)));
                    return;
                }

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(response.body().bytes());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(outputFile));
                } catch (IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e));
                }
            }
        });
    }

    public interface DownloadCallback {
        void onSuccess(File file);
        void onFailure(Exception e);
    }
}
