package com.mercuriy94.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import com.mercuriy94.stickyheaderexpandablelistview.ExpandableListViewStickyHeader;
import com.mercuriy94.stickyheaderexpandablelistview.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener{


    public static final String TAG = "myLogs";
    private ViewGroup mRootView;
    private ExpandableListViewStickyHeader mExpLisView;


    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();
    MyExpandableAdapter adapter;
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        container = (FrameLayout)findViewById(R.id.container);
        mExpLisView = new ExpandableListViewStickyHeader.Builder()
                .setContext(this)
                .setAutoCloseGroupe(false)
                .setOnScrollListener(this)
                .build();

        container.addView(mExpLisView);
        setGroupParents(20);
        setChildData(20);
        final ExpandableListView list = mExpLisView.getExpandableListView();
        list.setGroupIndicator(null);
        list.setDividerHeight(3);


        adapter = new MyExpandableAdapter(parentItems, childItems);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        mExpLisView.setAdapter(adapter);
    }

    public void setGroupParents(int num) {

        for(int i=0;i<num;i++){
            parentItems.add("Sector "+i);
        }
    }

    public void setChildData(int num) {

        for (int i = 0; i < parentItems.size() - 1; i++) {
            ArrayList<String> child = new ArrayList<String>();
            for (int g = 0; g < num; g++) {
                child.add("Section" + i + " Row " + g);
            }
            childItems.add(child);
        }
        ArrayList<String> child = new ArrayList<String>();
        for (int g = 0; g < 5; g++) {
            child.add("Section" + (parentItems.size() - 1) + " Row " + g);
        }
        childItems.add(child);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "переопределённый слушатель firstVisibleItem = " + firstVisibleItem);
    }


}
