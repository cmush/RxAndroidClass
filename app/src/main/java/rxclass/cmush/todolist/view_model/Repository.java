package rxclass.cmush.todolist.view_model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import rxclass.cmush.todolist.models.Comment;
import rxclass.cmush.todolist.models.Post;

public class Repository {
    private static final String TAG = "Repository";

    private static Repository instance;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    // using an Executor to make a network call,
    // returning a Future Observable to the ViewModel.
    public Future<Observable<ResponseBody>> makeFutureQuery() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Callable<Observable<ResponseBody>> myNetworkCallable = new Callable<Observable<ResponseBody>>() {
            @Override
            public Observable<ResponseBody> call() throws Exception {
                return ServiceGenerator.getRequestApi().makeObservableQuery();
            }
        };


        final Future<Observable<ResponseBody>> futureObservable = new Future<Observable<ResponseBody>>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (mayInterruptIfRunning) {
                    executor.shutdown();
                }
                return false;
            }

            @Override
            public boolean isCancelled() {
                return executor.isShutdown();
            }

            @Override
            public boolean isDone() {
                return executor.isTerminated();
            }

            @Override
            public Observable<ResponseBody> get() throws ExecutionException, InterruptedException {
                return executor.submit(myNetworkCallable).get();
            }

            @Override
            public Observable<ResponseBody> get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                return executor.submit(myNetworkCallable).get(timeout, unit);
            }
        };

        return futureObservable;

    }

    public LiveData<ResponseBody> makeLiveDataQuery() {
        return LiveDataReactiveStreams
                .fromPublisher(
                        ServiceGenerator.getRequestApi()
                                .makeFlowableQuery()
                                .subscribeOn(Schedulers.io())
                );
    }

    public Observable<Post> getPostsObservable(final RecyclerAdapter adapter) {
        return ServiceGenerator.getRequestApi()
                .getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Post>, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(List<Post> posts) throws Exception {
                        adapter.setPosts(posts);
                        return Observable
                                .fromIterable(posts)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    public Observable<Post> getCommentsObservable(final Post post) {
        return ServiceGenerator.getRequestApi()
                .getComments(post.getId())
                .subscribeOn(Schedulers.io())
                .map(new Function<List<Comment>, Post>() {
                    @Override
                    public Post apply(List<Comment> comments) throws Exception {
                        int delay = ((new Random()).nextInt(5) + 1) * 1000; // sleep thread for x ms
                        Thread.sleep(delay);
                        Log.d(TAG,
                                "apply: sleeping thread "
                                        + Thread.currentThread().getName()
                                        + " for "
                                        + String.valueOf(delay) + "ms"
                        );
                        post.setComments(comments);
                        return post;
                    }
                });
    }
}
