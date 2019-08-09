package rxclass.cmush.todolist.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Future;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import rxclass.cmush.todolist.models.Post;

public class MainViewModel extends ViewModel {

    private Repository repository;

    public MainViewModel() {
        repository = Repository.getInstance();
    }

    public Future<Observable<ResponseBody>> makeFutureQuery(){
        return repository.makeFutureQuery();
    }

    public LiveData<ResponseBody> makeLiveDataQuery(){
        return repository.makeLiveDataQuery();
    }

    public Observable<Post> makePostsQuery(RecyclerAdapter adapter){
        return repository.getPostsObservable(adapter);
    }

    public Observable<Post> makePostWithCommentsQuery(Post post){
        return repository.getCommentsObservable(post);
    }
}
