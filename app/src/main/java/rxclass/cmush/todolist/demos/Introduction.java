package rxclass.cmush.todolist.demos;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;

public class Introduction {
    private static final String TAG = "Introduction";

    public static void fromIterable_taskObservable(final CompositeDisposable disposables) {
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        Log.d(TAG, "taskObservable test:" + Thread.currentThread().getName());
                        return task.isComplete();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "fromIterable_taskObservable onSubscribe: called.");
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) { // run on main thread
                Log.d(TAG, "taskObservable onNext: task: " + Thread.currentThread().getName());
                Log.d(TAG, "taskObservable onNext: task: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "taskObservable onError: task: " + e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
