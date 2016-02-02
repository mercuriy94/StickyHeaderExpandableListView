package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Nikita on 01.02.2016.
 */
public class CustomExpandableListView extends ExpandableListView {

    public static final String TAG = "myLogs";
    public CustomExpandableListView(Context context) {
        super(context);

    }

    public CustomExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }


}
