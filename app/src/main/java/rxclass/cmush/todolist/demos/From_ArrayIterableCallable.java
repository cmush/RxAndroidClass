package rxclass.cmush.todolist.demos;

import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;

public class From_ArrayIterableCallable {
    private static final String TAG = "ArrayIterableCallable";

    static final Task[] taskList = DataSource.createTasksArray();

    /*
     * used to emit an arbitrary number of items that are known upfront.
     *
     * Input: T[]
     * Ouput: Observable<T>
     */
    public static void fromArray() {
        Observable<Task> taskObservable = Observable
                .fromArray(taskList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "fromArray onNext: : " + task.getDescription());
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
     * used to emit an arbitrary number of items that are known upfront.
     *
     * Input: List<T>, ArrayList<T>, Set<T>, etc...
     * Ouput: Observable<T>
     */
    public static void fromIterable() {
        Observable<Task> taskObservable = Observable
                .fromArray(taskList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "fromIterable onNext: task: " + task.getDescription());
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
     * used to emit a result returned byw a
     * "to be executed" block of code (usually a method)
     *
     * Input: Callable<T>
     * Ouput: T
     */
    public static void fromCallable(){
        // create Observable (method will not execute yet)
//        Observable<Task> callable = Observable
//                .fromCallable(new Callable<Task>() {
//                    @Override
//                    public Task call() throws Exception {
//                        return MyDatabase.getTask();
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//
//        // method will be executed since now something has subscribed
//        callable.subscribe(new Observer<Task>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Task task) {
//                Log.d(TAG, "onNext: : " + task.getDescription());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }
}
