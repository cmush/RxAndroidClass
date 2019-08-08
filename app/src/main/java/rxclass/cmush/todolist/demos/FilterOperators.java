package rxclass.cmush.todolist.demos;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;

/*
 * "The Filter operator filters an Observable by only allowing
 * items through that pass a test that you specify
 * in the form of a predicate function."
 */
public class FilterOperators {
    private static final String TAG = "FilterOperators";

    public static void stringFilter() {
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        return task.getDescription().equals("Walk the dog");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG,
                        "stringFilter onNext: This task matches the description: "
                                + task.getDescription()
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

    // See Introduction.fromIterable_taskObservable
    public static void booleanFilter(){

    }
}
