package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;


public class ExpandableListViewStickyHeader extends RelativeLayout{
    public static final String TAG = "myLogs";
    private Context mContext;
    private RelativeLayout mHeader;
    private ExpandableListView mExpandableListView;
    private OnGroupExpandListenet mGroupExpandListener;
    private OnGroupCollapseListener mGroupCollapseListener;
    private OnStickyHeaderExpandableListScrollListener mListScrollListener;
    private CustomExpandableListAdapter mAdapter; //Адаптер
    private boolean mAutoCLoseGroup = true; //При открытии новой группы, предыдущая открытая закрывается
    private boolean mOpenGroups[]; //хранение списка групп и их состояния(expanded = true/collapse = false)


    private ExpandableListViewStickyHeader(Context context,
                                           boolean autoCLoseGroup,
                                           OnGroupExpandListenet onGroupExpand,
                                           OnGroupCollapseListener onGroupCollapse,
                                           OnStickyHeaderExpandableListScrollListener listScrollListener) {
        super(context);
        this.mContext = context;
        this.mAutoCLoseGroup = autoCLoseGroup;
        this.mGroupExpandListener = onGroupExpand;
        this.mGroupCollapseListener = onGroupCollapse;
        this.mListScrollListener = listScrollListener;

        init();
    }




    private void init(){
        //Добавление списка
        mExpandableListView = new ExpandableListView(mContext);

        if(mGroupExpandListener == null){
            mGroupExpandListener = new OnGroupExpandListenet();
        }

        if(mGroupCollapseListener == null){
            mGroupCollapseListener = new OnGroupCollapseListener();
        }

        if(mListScrollListener == null){
            mListScrollListener = new OnStickyHeaderExpandableListScrollListener();
        }
        mExpandableListView.setOnGroupExpandListener(mGroupExpandListener);
        mExpandableListView.setOnGroupCollapseListener(mGroupCollapseListener);
        mExpandableListView.setOnScrollListener(mListScrollListener);
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


    public OnGroupExpandListenet getGroupExpandListener() {
        return mGroupExpandListener;
    }

    public class OnGroupExpandListenet implements ExpandableListView.OnGroupExpandListener {



        @Override
        public  void onGroupExpand(int groupPosition) {
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
    }









    public  class OnGroupCollapseListener implements ExpandableListView.OnGroupCollapseListener{
        @Override
        public void onGroupCollapse(int groupPosition) {
            Log.d(TAG,"onGroupCollapse.position = " + groupPosition);
            mOpenGroups[groupPosition] = false;
        }
    }










    public static class Builder {
        private OnGroupExpandListenet mOnGroupExpandListener;
        private OnGroupCollapseListener mOnGroupCollapseListener;
        private OnStickyHeaderExpandableListScrollListener mListScrollListener;
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

        public Builder setOnStickyHeaderExpandableListScrollListener(OnStickyHeaderExpandableListScrollListener listScrollListener) {
            this.mListScrollListener = listScrollListener;
            return this;
        }

        public Builder setOnGroupCollapseListener(OnGroupCollapseListener mOnGroupCollapseListener) {
            this.mOnGroupCollapseListener = mOnGroupCollapseListener;
            return this;
        }

        public Builder setOnGroupExpandListener(OnGroupExpandListenet mOnGroupExpandListener) {
            this.mOnGroupExpandListener = mOnGroupExpandListener;
            return this;
        }

        public ExpandableListViewStickyHeader build(){
            return new ExpandableListViewStickyHeader(mContext,
                                                      mAutoCloseGroupe,
                                                      mOnGroupExpandListener,
                                                      mOnGroupCollapseListener,
                                                      mListScrollListener);
        }

   }


}
