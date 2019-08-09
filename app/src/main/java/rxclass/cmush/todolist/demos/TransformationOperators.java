package rxclass.cmush.todolist.demos;

import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import rxclass.cmush.todolist.R;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;

public class TransformationOperators {
    private static final String TAG = "TransformationOperators";

    /*
     * transforms each emitted item by applying a function to it.
     * - order is maintained
     */
    public static void mapExtractFieldString() {
        Observable<String> extractDescriptionObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .subscribeOn(Schedulers.io())
                .map(new Function<Task, String>() {
                    @Override
                    public String apply(Task task) throws Exception {
                        Log.d(TAG,
                                "mapExtractFieldString apply: doing work on thread: "
                                        + Thread.currentThread().getName()
                        );
                        return task.getDescription();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        extractDescriptionObservable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "mapExtractFieldString onNext: extracted description: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // how do I update n objects and have only updated objects emitted?
    public static void mapExtractUpdatedTask() {
        Observable<Task> completeTaskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .subscribeOn(Schedulers.io())
                .map(new Function<Task, Task>() {
                    @Override
                    public Task apply(Task task) throws Exception {
                        Log.d(TAG,
                                "mapExtractUpdatedTask apply: doing work on thread: "
                                + Thread.currentThread().getName()
                        );
                        task.setComplete(true);
                        return task;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        completeTaskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "mapExtractUpdatedTask onNext: is the task "
                        + task.getDescription() + " complete? "
                        + task.isComplete()
                );
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /*
     * Periodically gather items from an Observable into bundles and emit the bundles
     * (rather than single emissions).
     * - order is maintained
     */
    // bundling emitted objects into groups.
    // e.g emit 2 objects per emission with a time delay in-between
    public static void bufferGroupEmissions(){
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable
                .buffer(2)
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Task> tasks) {
                        Log.d(TAG, "bufferGroupEmissions onNext: bundle results: -------------------");
                        for(Task task: tasks){
                            Log.d(TAG, "bufferGroupEmissions onNext: " + task.getDescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // uses the RxBinding library (by Jake Wharton) to make click events observable.
    public static void bufferTrackUiInteractions(final CompositeDisposable disposables, View view){
        RxView.clicks(view.findViewById(R.id.button))
        .map(new Function<Unit, Integer>() {
            @Override
            public Integer apply(Unit unit) throws Exception {
                return 1;
            }
        })
        .buffer(4, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(List<Integer> integers) {
                Log.d(TAG, "bufferTrackUiInteractions onNext: You clicked " + integers.size() + " times in 4 seconds!");
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
