package rxclass.cmush.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    private CompositeDisposable disposables = new CompositeDisposable();

    //ui
    private TextView text;

    //vars

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskObservable();
        singleTaskObservable();
    }

    private void singleTaskObservable() {
        // Instantiate the object to become an Observable
        final Task task = new Task("Walk the dog", false, 4);

        // Create the Observable
        Observable<Task> singleTaskObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        if(!emitter.isDisposed()){ // process is unique to the create operator
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
                Log.d(TAG, "singleTaskObservable onNext: : " + task.getDescription());
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
                Log.d(TAG, "taskObservable onNext: : " + Thread.currentThread().getName());
                Log.d(TAG, "taskObservable onNext: : " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "taskObservable onError: : " + e);
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
