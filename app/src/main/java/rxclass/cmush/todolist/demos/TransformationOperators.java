package rxclass.cmush.todolist.demos;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import rxclass.cmush.todolist.R;
import rxclass.cmush.todolist.models.Post;
import rxclass.cmush.todolist.models.Task;
import rxclass.cmush.todolist.util.DataSource;
import rxclass.cmush.todolist.view_model.MainViewModel;
import rxclass.cmush.todolist.view_model.RecyclerAdapter;

public class TransformationOperators {
    private static final String TAG = "TransformationOperators";

    private static long timeSinceLastRequest;

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
    public static void bufferGroupEmissions() {
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
                        for (Task task : tasks) {
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
    public static void bufferTrackUiInteractions(final CompositeDisposable disposables, View view) {
        RxView.clicks(view.findViewById(R.id.btnBuffer))
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

    /*
     * filters out items emitted by the source Observable that are
     * rapidly followed by another emitted item.
     * - order is maintained
     */

    public static void debounceSearchView(final CompositeDisposable disposables, final SearchView searchView) {
        // for log printouts only. Not part of logic.
        timeSinceLastRequest = System.currentTimeMillis();

        // create the Observable
        Observable<String> observableQueryTest = Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                        // Listen for text input into the SearchView
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                if (!emitter.isDisposed()) {
                                    emitter.onNext(newText); // Pass the query to the emitter
                                }
                                return false;
                            }
                        });
                    }
                })
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());

        // Subscribe an Observer
        observableQueryTest.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(String queryString) {
                Log.d(TAG,
                        "debounceSearchView onNext: time  since last request: "
                                + (System.currentTimeMillis() - timeSinceLastRequest)
                );
                Log.d(TAG,
                        "debounceSearchView onNext: search queryString: "
                                + queryString
                );
                timeSinceLastRequest = System.currentTimeMillis();

                // method for sending a request to the server
                sendRequestToServer(queryString);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // Fake method for sending a request to the server
    private static void sendRequestToServer(String query) {
        // do nothing
    }

    /*
     * filters out items emitted by the source
     * Observable that are within a timespan.
     * - order is maintained
     */
    public static void throttleFirstRestrictButtonSpamming(final CompositeDisposable disposables, final Button btnThrottleFirst) {
        RxView.clicks(btnThrottleFirst)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(Unit unit) {
                        Log.d(TAG,
                                "throttleFirstRestrictButtonSpamming onNext: time since last clicked: "
                                        + (System.currentTimeMillis() - timeSinceLastRequest)
                        );
                        someMethod(); // Execute some method when a click is registered
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void someMethod() {
        timeSinceLastRequest = System.currentTimeMillis();
        // do something
    }

    /*
     * 2 major functions:
     * - Create Observables out of objects emitted by other Observables.
     * - Flattening: Combine multiple Observable sources into a
     *   single Observable
     * MediatorLiveData can do something very similar.
     *
     * - order is not maintained
     */
    public static void flatMapRecViewPostsWithComments(
            final CompositeDisposable disposables,
            FragmentActivity context,
            final RecyclerAdapter adapter
    ) {
        final MainViewModel viewModel = ViewModelProviders.of(context).get(MainViewModel.class);

        viewModel
                .makePostsQuery(adapter)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Exception {
                        return viewModel.makePostWithCommentsQuery(post);
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        updatePost(disposables, adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "flatMapRecViewPostsWithComments onError: ", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
     * 2 major functions:
     * - Create Observables out of objects emitted by other Observables.
     * - Flattening: Combine multiple Observable sources into a
     *   single Observable while maintaining order
     * MediatorLiveData can do something very similar.
     *
     * - order is maintained
     */
    public static void concatMapRecViewPostsWithComments(
            final CompositeDisposable disposables,
            FragmentActivity context,
            final RecyclerAdapter adapter
    ) {
        final MainViewModel viewModel = ViewModelProviders.of(context).get(MainViewModel.class);

        viewModel
                .makePostsQuery(adapter)
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Exception {
                        return viewModel.makePostWithCommentsQuery(post);
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        updatePost(disposables, adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "concatMapRecViewPostsWithComments onError: ", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static void updatePost(
            final CompositeDisposable disposables,
            @NotNull final RecyclerAdapter adapter
    ) {
        Observable
                .fromIterable(adapter.getPosts())
                .filter(new Predicate<Post>() {
                    @Override
                    public boolean test(Post post) throws Exception {
                        return post.getId() == post.getId();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        Log.d(TAG,
                                "flatMapRecViewPostsWithComments onNext: updating post: "
                                        + post.getId()
                                        + ", thread: "
                                        + Thread.currentThread().getName()
                        );
                        adapter.updatePost(post);
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
