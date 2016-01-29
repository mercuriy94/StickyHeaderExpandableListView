package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;


public class ExpandableListViewStickyHeader extends RelativeLayout implements ExpandableListView.OnGroupExpandListener,
                                                                                ExpandableListView.OnGroupCollapseListener{
    public static final String TAG = "myLogs";
    private Context mContext;
    private RelativeLayout mHeader;
    private ExpandableListView mExpandableListView;
    private CustomExpandableListAdapter mAdapter; //Адаптер
    private boolean mAutoCLoseGroup = true; //При открытии новой группы, предыдущая открытая закрывается
    private boolean mOpenGroups[]; //хранение списка групп и их состояния(expanded = true/collapse = false)


    private ExpandableListViewStickyHeader(Context mContext,
                                           boolean mAutoCLoseGroup) {
        super(mContext);
        this.mContext = mContext;
        this.mAutoCLoseGroup = mAutoCLoseGroup;


        init();
        Log.d(TAG,"mAutoCLoseGroup = "+ mAutoCLoseGroup);

    }


    private void init(){
        //Добавление списка
        mExpandableListView = new ExpandableListView(mContext);
        mExpandableListView.setOnGroupExpandListener(this);
        mExpandableListView.setOnGroupCollapseListener(this);
        addView(mExpandableListView);

        //Добавление липкого заголовка
        mHeader = new RelativeLayout(mContext);
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerParams.addRule(ALIGN_PARENT_TOP);
        mHeader.setLayoutParams(headerParams);
        addView(mHeader);

    }

    public void setAdapter(CustomExpandableListAdapter adapter){
                if(mExpandableListView != null){
                    mAdapter = adapter;
                    mExpandableListView.setAdapter(mAdapter);

                    mOpenGroups = new boolean[mAdapter.getGroupCount()]; //получаем список групп
                }
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        Log.d(TAG,"onGroupExpand.position = "+groupPosition);
        mOpenGroups[groupPosition] = true;
        if(mAutoCLoseGroup){
            for(int position = 0; position < mOpenGroups.length; position++){
                if(mOpenGroups[position] && position != groupPosition){
                    mExpandableListView.collapseGroup(position);
                }
            }
        }
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        Log.d(TAG,"onGroupCollapse.position = " + groupPosition);
        mOpenGroups[groupPosition] = false;
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
