package rxclass.cmush.todolist.view_model;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rxclass.cmush.todolist.models.Comment;
import rxclass.cmush.todolist.models.Post;

public interface RequestApi {
    @GET("todos/1")
        // able to return an Observable because of the
        // RxJava Call Adapter dependency
    Observable<ResponseBody> makeObservableQuery();

    @GET("todos/1")
        // able to return an Observable/Flowable because of the
        // RxJava Call Adapter dependency
    Flowable<ResponseBody> makeFlowableQuery();

    @GET("posts")
    Observable<List<Post>> getPosts();

    @GET("posts/{id}/comments")
    Observable<List<Comment>> getComments(
            @Path("id") int id
    );
}
