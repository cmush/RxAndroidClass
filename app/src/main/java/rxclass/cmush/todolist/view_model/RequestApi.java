package rxclass.cmush.todolist.view_model;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface RequestApi {
    @GET("todos/1")
        // able to return an Observable because of the
        // RxJava Call Adapter dependency
    Observable<ResponseBody> makeObservableQuery();

    @GET("todos/1")
        // able to return an Observable/Flowable because of the
        // RxJava Call Adapter dependency
    Flowable<ResponseBody> makeFlowableQuery();
}
