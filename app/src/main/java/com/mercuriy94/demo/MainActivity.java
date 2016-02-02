package com.mercuriy94.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mercuriy94.stickyheaderexpandablelistview.ExpandableListViewStickyHeader;
import com.mercuriy94.stickyheaderexpandablelistview.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{


    public static final String TAG = "myLogs";
    private ViewGroup mRootView;
    private ExpandableListViewStickyHeader mExpLisView;


    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();
    MyExpandableAdapter adapter;
    ExpandableListViewStickyHeader listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setGroupParents(20);
        setChildData(20);
        listView = (ExpandableListViewStickyHeader)findViewById(R.id.list);
        adapter = new MyExpandableAdapter(parentItems, childItems);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);


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





}
