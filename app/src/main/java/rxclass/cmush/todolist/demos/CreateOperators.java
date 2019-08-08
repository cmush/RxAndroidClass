package rxclass.cmush.todolist.demos;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import rxclass.cmush.todolist.view_model.MainViewModel;

public class CreateOperators {
    private static final String TAG = "CreateOperators";

    /*
     * A future is essentially a pending task. It's a promise for a
     * result from a task when it runs sometime in the future e.g an ExecutorService
     *
     * Future<T>
     * Ouput: Observable<T>
     */
    public static void fromFuture(FragmentActivity context) {
        MainViewModel viewModel = ViewModelProviders.of(context).get(MainViewModel.class);
        try {
            viewModel.makeFutureQuery().get()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "fromFuture onSubscribe: called.");
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Log.d(TAG, "fromFuture onNext: got the response from server!");
                            try {
                                Log.d(TAG, "fromFuture onNext: " + responseBody.string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "fromFuture onError: ", e);
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "fromFuture onComplete: called.");
                        }
                    });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * used to convert LiveData objects to reactive streams
     * (rare & especially in cases when one might wanna apply an operator),
     * or from reactive streams to LiveData objects.
     * Input: LiveData<T>
     * Ouput: Observable<T>
     *     OR
     * Input: Observable<T>
     * Ouput: LiveData<T>
     */
    public static void fromPublisher(FragmentActivity context) {
        MainViewModel viewModel = ViewModelProviders.of(context).get(MainViewModel.class);
        viewModel.makeLiveDataQuery().observe(context, new androidx.lifecycle.Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                Log.d(TAG, "fromPublisher onChanged: this is a live data response!");
                try {
                    Log.d(TAG, "fromPublisher onChanged: " + responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
