package com.abdurazaaqmohammed.utils;


import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.abdurazaaqmohammed.AntiSplit.main.MainActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RunUtil {

    private final Handler handler;
    private final MainActivity context;
    private final CharSequence msg;

    public RunUtil(Handler handler, MainActivity context, CharSequence msg) {
        this.handler = handler;
        this.context = context;
        this.msg = msg;
    }

    public static void runInBackground(Runnable runnable) {
        try {
            Executors.newSingleThreadExecutor().submit(runnable);
        } catch (Exception ignored) { }
    }

    public void runInBackground(Callable<Boolean> callable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(callable);

        executor.submit(() -> {
            try {
                Boolean success = future.get();

                if (success && handler != null && !TextUtils.isEmpty(msg))
                    handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
            } catch (Throwable e) {
                reportFailure(e);
            } finally {
                executor.shutdown();
            }
        });
    }

    public void runInBackground(Callable<Boolean> callable, Runnable doAfter, boolean onlyIfSuccessful) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(callable);

        executor.submit(() -> {
            try {
                if (future.get() && handler != null) handler.post(doAfter);
            } catch (Throwable e) {
                if (!onlyIfSuccessful && handler != null) handler.post(doAfter);
                reportFailure(e);
            } finally {
                executor.shutdown();
            }
        });
    }

    /**
     * Future.get() wraps the real failure in an ExecutionException, so unwrap it
     * before deciding whether we ran out of memory (#653 / #143).
     */
    private void reportFailure(Throwable e) {
        Throwable cause = e;
        if (e instanceof java.util.concurrent.ExecutionException && e.getCause() != null)
            cause = e.getCause();
        if (cause instanceof OutOfMemoryError)
            context.showError(context.getRss().getString(com.abdurazaaqmohammed.AntiSplit.R.string.out_of_memory));
        else
            context.showError(cause);
    }
}