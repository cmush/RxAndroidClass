package rxclass.cmush.todolist;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

import static rxclass.cmush.todolist.demos.FilterOperators.*;
import static rxclass.cmush.todolist.demos.Introduction.*;
import static rxclass.cmush.todolist.demos.CreateJustRangeRepeat.*;
import static rxclass.cmush.todolist.demos.IntervalAndTimer.*;
import static rxclass.cmush.todolist.demos.From_ArrayIterableCallable.*;
import static rxclass.cmush.todolist.demos.CreateOperators.*;
import static rxclass.cmush.todolist.demos.TransformationOperators.*;

public class MainActivity extends AppCompatActivity {
    //ui
    private TextView text;

    //vars
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // remove current observers/observables
        // disposables.dispose(); // will no longer allow anything to subscribe to the observable
    }
}
