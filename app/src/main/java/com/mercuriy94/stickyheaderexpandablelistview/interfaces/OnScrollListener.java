package com.mercuriy94.stickyheaderexpandablelistview.interfaces;

import android.widget.AbsListView;

/**
 * Created by Nikita on 01.02.2016.
 */
public interface OnScrollListener {

    public void onScrollStateChanged(AbsListView view, int scrollState);

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount);
}
