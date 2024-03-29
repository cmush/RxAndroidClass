package rxclass.cmush.todolist.demos;

import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;

public class CreateJustRangeRepeat {
    private static final String TAG = "CreateJustRangeRepeat";

    public static void repeat() {
        // repeat() is another intuitively named operator.
        // However, repeat must be used in conjunction with another operator.
        // A good example is with the range() operator.
        Observable.range(0, 10)
                .repeat(2)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "repeat onNext: " + integer);
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
     * Input: [x, x+1, ..., x + y]
     * Output: Observable<Integer>
     */
    public static void range() {
        Observable.range(0, 20)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Function<Integer, Task>() {
                    @Override
                    public Task apply(Integer integer) throws Exception {
                        Log.d(TAG, "range + map apply: " + Thread.currentThread().getName());
                        return new Task(
                                "This is a task with priority: " + String.valueOf(integer),
                                false,
                                integer
                        );
                    }
                })
                .takeWhile(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        return task.getPriority() < 9;
                    }
                })
                .subscribe(new Observer<Task>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Task task) {
                        Log.d(TAG, "range onNext: task.getDescription():" + task.getDescription());
                        Log.d(TAG, "range onNext: task.getPriority():" + task.getPriority());
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
     * Input: T... (Optional Array[10])
     * Output: Observable<T>
     * - not a practical example but makes a great demo
     */
    public static void just() {
        Observable.just("first", "second", "third", "fourth", "fifth", "sixth",
                "seventh", "eighth", "ninth", "tenth")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "just onSubscribe: called");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "just onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "just onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "just onComplete: done...");
                    }
                });
    }

    /*
     * Input:T
     * Output:Observable<T>
     */
    public static void create_taskListObservable() {
        final List<Task> tasks = DataSource.createTasksList();

        Observable<Task> taskListObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        for (Task task : tasks) {
                            // Inside the subscribe method iterate through
                            // the list of tasks and call onNext(task)
                            if (!emitter.isDisposed()) {
                                emitter.onNext(task);
                            }
                        }

                        // Once the loop is complete, call the onComplete() method
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        // Subscribe to the Observable and get the emitted objects
        taskListObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "create_taskListObservable onNext: task: " + task.getDescription());
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
     * Input:T
     * Output:Observable<T>
     */
    public static void create_singleTaskObservable() {
        // Instantiate the object to become an Observable
        final Task task = new Task("Walk the dog", false, 4);

        // Create the Observable
        Observable<Task> singleTaskObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        if (!emitter.isDisposed()) { // process is unique to the create operator
                            emitter.onNext(task);  // basically, it should happen if emitter is not
                            emitter.onComplete();  // disposed of yet.
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        singleTaskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "create_singleTaskObservable onNext: task: " + task.getDescription());
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
