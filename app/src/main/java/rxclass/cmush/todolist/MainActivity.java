package rxclass.cmush.todolist;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

import static rxclass.cmush.todolist.demos.Introduction.*;
import static rxclass.cmush.todolist.demos.CreateJustRangeRepeat.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // remove current observers/observables
        // disposables.dispose(); // will no longer allow anything to subscribe to the observable
    }
}
