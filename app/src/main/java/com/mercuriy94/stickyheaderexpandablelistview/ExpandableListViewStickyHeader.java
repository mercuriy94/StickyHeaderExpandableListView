package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;


public class ExpandableListViewStickyHeader extends RelativeLayout implements ExpandableListView.OnGroupExpandListener {
    public static final String TAG = "myLogs";
    private Context mContext;
    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter mAdapter; //Адаптер
    private boolean mAutoCLoseGroup = true; //При открытии новой группы, предыдущая открытая закрывается



    private ExpandableListViewStickyHeader(Context mContext,
                                           boolean mAutoCLoseGroup) {
        super(mContext);
        this.mContext = mContext;
        this.mAutoCLoseGroup = mAutoCLoseGroup;


        init();
        Log.d(TAG,"mAutoCLoseGroup = "+ mAutoCLoseGroup);

    }


    private void init(){
        mExpandableListView = new ExpandableListView(mContext);
        addView(mExpandableListView);
    }

    public void setAdapter(CustomExpandableListAdapter adapter){
                if(mExpandableListView != null){
                    mAdapter = adapter;
                    mExpandableListView.setAdapter(mAdapter);
                }
    }

    @Override
    public void onGroupExpand(int groupPosition) {

    }

    public static class Builder {
       private Context mContext;
        private boolean mAutoCloseGroupe = true;


        public Builder setContext(Context mContext){
            this.mContext = mContext;
            return this;
        }

       public Builder setAutoCloseGroupe(boolean autoCloseGroupe){
           this.mAutoCloseGroupe = autoCloseGroupe;
           return this;
       }

        public ExpandableListViewStickyHeader build(){


            return new ExpandableListViewStickyHeader(mContext,mAutoCloseGroupe);
        }

   }


}
