package rxclass.cmush.todolist.demos;


import android.os.Handler;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class IntervalAndTimer {
    private static final String TAG = "IntervalAndTimer";

    public static void handlerRunnable() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int elapsedTime = 0;

            @Override
            public void run() {
                if (elapsedTime >= 5) {
                    handler.removeCallbacks(this);
                } else {
                    elapsedTime = elapsedTime + 1;
                    handler.postDelayed(this, 1000);
                    Log.d(TAG, "handlerRunnable: run: " + elapsedTime);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    // emit an observable every time interval
    public static void intervalObservable() {
        Observable<Long> intintervalObservable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .takeWhile(new Predicate<Long>() {// stop the process if more than 5 seconds passes
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong <= 5;
                    }
                });
        intintervalObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "intervalObservable onNext: interval: " + aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void timer() {
        // emit single observable after a given delay
        Observable<Long> timeObservable = Observable
                .timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        timeObservable.subscribe(new Observer<Long>() {
            long time = 0; // variable for demonstating how much time has passed

            @Override
            public void onSubscribe(Disposable d) {
                time = System.currentTimeMillis() / 1000;
            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "timer onNext: " +
                        ((System.currentTimeMillis() / 1000) - time) +
                        " seconds have elapsed.");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
