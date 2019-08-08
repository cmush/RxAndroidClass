package rxclass.cmush.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //ui
    private TextView text;

    //vars
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskObservable();
        singleTaskObservable();
        taskListObservable();
        just();
        range();
        repeat();
    }

    private void repeat() {
        // repeat() is another intuitively named operator.
        // However, repeat must be used in conjunction with another operator.
        // A good example is with the range() operator.
        Observable.range(0,10)
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

    private void range() {
        Observable.range(0,20)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "range onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // not a practical example but makes a great demo
    private void just() {
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

    private void taskListObservable() {
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
                Log.d(TAG, "taskListObservable onNext: task: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void singleTaskObservable() {
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
                Log.d(TAG, "singleTaskObservable onNext: task: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void taskObservable() {
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
                Log.d(TAG, "taskObservable onSubscribe: called.");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // remove current observers/observables
        // disposables.dispose(); // will no longer allow anything to subscribe to the observable
    }
}
