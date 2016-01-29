package com.mercuriy94.stickyheaderexpandablelistview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * Created by Nikita on 25.01.2016.
 */
public abstract class CustomExpandableListAdapter extends BaseExpandableListAdapter {


    public abstract View getGroupHeaderView(int group, View convertView,ViewGroup parent);

}
