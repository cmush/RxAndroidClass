package rxclass.cmush.todolist;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import rxclass.cmush.todolist.models.Post;
import rxclass.cmush.todolist.view_model.RecyclerAdapter;

import static rxclass.cmush.todolist.demos.FilterOperators.*;
import static rxclass.cmush.todolist.demos.Introduction.*;
import static rxclass.cmush.todolist.demos.CreateJustRangeRepeat.*;
import static rxclass.cmush.todolist.demos.IntervalAndTimer.*;
import static rxclass.cmush.todolist.demos.From_ArrayIterableCallable.*;
import static rxclass.cmush.todolist.demos.CreateOperators.*;
import static rxclass.cmush.todolist.demos.TransformationOperators.*;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnPostClickListener {
    private static final String TAG = "MainActivity";

    //ui
    private TextView textView;
    private Button btnBuffer, btnThrottleFirst;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ProgressBar progressBar;

    //vars
    private CompositeDisposable disposables = new CompositeDisposable();

    // for selecting a post -> switchMapRecViewPostsWithComments
    private static PublishSubject<Post> publishSubject = PublishSubject.create();

    private void initRecyclerView(MainActivity mainActivity) {
        adapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerViewOnclick(MainActivity mainActivity) {
        adapter = new RecyclerAdapter(mainActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBuffer = (Button) findViewById(R.id.btnBuffer);
        btnThrottleFirst = (Button) findViewById(R.id.btnThrottleFirst);
        searchView = (SearchView) findViewById(R.id.searchView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //initRecyclerView(this);
        initRecyclerViewOnclick(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Introduction
        fromIterable_taskObservable(disposables);

        // Create, Just, Range, Repeat
        create_singleTaskObservable();
        create_taskListObservable();
        just();
        range();
        repeat();

        // Interval and Timer
        handlerRunnable();
        intervalObservable();
        timer();

        // fromArray, fromIterable, fromCallable - RxJava Operators
        fromArray();
        fromIterable();
        fromCallable();

        // Create Operators - fromFuture
        fromFuture(this);
        fromPublisher(this);

        // Filter Operators
        stringFilter();
        booleanFilter(); // implemented in Introduction.fromIterable_taskObservable
        distinctFilter();
        takeFilter();
        takeWhileFilter();

        // Transformation Operators
        mapExtractFieldString();
        mapExtractUpdatedTask();
        bufferGroupEmissions();
        bufferTrackUiInteractions(disposables, btnBuffer);
        debounceSearchView(disposables, searchView);
        throttleFirstRestrictButtonSpamming(disposables, btnThrottleFirst);
        //flatMapRecViewPostsWithComments(disposables,this, adapter);
        //concatMapRecViewPostsWithComments(disposables,this, adapter);

        // onResume -> switchMapRecViewPostsWithComments
        retrievePosts(disposables);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setProgress(0);
        // Transformation Operators
        switchMapRecViewPostsWithComments(
                disposables, publishSubject, this, adapter, progressBar
        );
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called.");
        disposables.clear();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // remove current observers/observables
        // disposables.dispose(); // will no longer allow anything to subscribe to the observable
    }

    @Override
    public void onPostClick(final int position) {
        Log.d(TAG, "onPostClick: clicked.");

        // submit the selected post object to be queried
        publishSubject.onNext(adapter.getPosts().get(position));
    }
}
