package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mRootView;
    private ExpandableListViewStickyHeader mExpLisView;


    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();
    MyExpandableAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        mExpLisView = new ExpandableListViewStickyHeader.Builder()
                                                        .setContext(this)
                                                        .setAutoCloseGroupe(false)
                                                        .build();

        mRootView.addView(mExpLisView);
        setGroupParents(20);
        setChildData(30);
        ExpandableListView list = (ExpandableListView) mExpLisView.getChildAt(0);
        list.setGroupIndicator(null);
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

        for(int i =0; i< parentItems.size()-1;i++){
            ArrayList<String> child = new ArrayList<String>();
            for(int g =0;g < num;g++){
                child.add("Section"+i +" Row "+ g);
            }
            childItems.add(child);
        }
        ArrayList<String> child = new ArrayList<String>();
        for(int g = 0;g<5;g++){
            child.add("Section"+ (parentItems.size()-1)+" Row "+ g);
        }
        childItems.add(child);

    }

}
