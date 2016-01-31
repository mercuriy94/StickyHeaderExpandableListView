package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;


public class ExpandableListViewStickyHeader extends RelativeLayout{
    public static final String TAG = "myLogs";
    private Context mContext;
    private RelativeLayout mHeader;
    private View mHeaderView;
    private ExpandableListView mExpandableListView;
    private OnGroupExpandListenet mGroupExpandListener;
    private OnGroupCollapseListener mGroupCollapseListener;
    private OnStickyHeaderExpandableListScrollListener mListScrollListener;
    private CustomExpandableListAdapter mAdapter; //Адаптер
    private boolean mAutoCLoseGroup = true; //При открытии новой группы, предыдущая открытая закрывается
    private boolean mOpenGroups[]; //хранение списка групп и их состояния(expanded = true/collapse = false)
    private int mGroupInFocus = -1;
    private int mLastExpandedPosition = -1;
    private boolean mClick = false;


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
        LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mExpandableListView.setLayoutParams(listParams);
        mExpandableListView.setSmoothScrollbarEnabled(true);
        mExpandableListView.setSelected(true);
        addView(mExpandableListView);

        //Добавление липкого заголовка
        mHeader = new RelativeLayout(mContext);
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerParams.addRule(ALIGN_PARENT_TOP);
        mHeader.setLayoutParams(headerParams);
        mHeader.setGravity(Gravity.BOTTOM);

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
            mClick = true;
          // mExpandableListView.smoothScrollToPosition(groupPosition);

            mLastExpandedPosition = groupPosition;
            if(mAutoCLoseGroup){

               // mExpandableListView.smoothScrollBy(30,500);
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
           // Log.d(TAG,"onGroupCollapse.position = " + groupPosition);
            mOpenGroups[groupPosition] = false;

        }
    }

private  boolean scroll = false;

    public  class OnStickyHeaderExpandableListScrollListener implements AbsListView.OnScrollListener {
        public static final String TAG = "myLogs";


        public OnStickyHeaderExpandableListScrollListener() {
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {


        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.d(TAG, "onScroll.firstVisibleItem =" + firstVisibleItem);
            Log.d(TAG, "onScroll.visibleItemCount =" + visibleItemCount);
            if(mAdapter != null){

                int sumChildsExpandedGroup = 0; //Сумма всех детей раскрытых групп
                int expandedGroupsCount = 0; //Сумма всех раскрытых групп
                int mFirstVisibleExpandedGroup = -1; //Первая раскрытая группа попавшая на экран (первая сверху)
                int sum = 0;
                int sumHeightVisibleChildrens = 0;
                int sumFull = 0;
                int sumHeightDivider = 0;



                for(int i =0;i< mOpenGroups.length;i++){
                    if(mOpenGroups[i]){
                        sumChildsExpandedGroup += mAdapter.getChildrenCount(i);
                        expandedGroupsCount++;
                        if((i+sumChildsExpandedGroup)>=firstVisibleItem && i <= firstVisibleItem){
                            mFirstVisibleExpandedGroup = i;
                            break;
                        }
                    }
                }

                if(mFirstVisibleExpandedGroup != -1 && expandedGroupsCount > 1){
                  if(((mFirstVisibleExpandedGroup + sumChildsExpandedGroup)- mAdapter.getChildrenCount(mFirstVisibleExpandedGroup))> firstVisibleItem){
                      mFirstVisibleExpandedGroup = -1;
                  }
                }

         //       Log.d(TAG, "Сумма детей раскрытых групп = " + sumChildsExpandedGroup);
         //       Log.d(TAG, "Количество раскрытых групп = " + expandedGroupsCount);
           //     Log.d(TAG, "Первая раскрытая группа попавшая на экран = " + mFirstVisibleExpandedGroup);


             /*   Log.d(TAG,"Высота 0 элемента = "+ mExpandableListView.getChildAt(0).getTop());
                for(int i =1; i< visibleItemCount; i++){
                    Log.d(TAG,"Высота "+i+" элемента = "+ mExpandableListView.getChildAt(i).getTop());
                }*/


                if(mFirstVisibleExpandedGroup != -1){
                    mGroupInFocus = mFirstVisibleExpandedGroup;
                    int mVisibleChildsCount = 0;


                    mVisibleChildsCount = (mFirstVisibleExpandedGroup + sumChildsExpandedGroup) -firstVisibleItem +1;
                    if(mVisibleChildsCount > visibleItemCount){
                        mVisibleChildsCount = visibleItemCount;
                    }

         //       Log.d(TAG,"Кол-во видимых детей = "+ mVisibleChildsCount);
                    //  buildHead(mFirstVisibleExpandedGroup);
                    if(mVisibleChildsCount > 0){
                        sumHeightVisibleChildrens+=mExpandableListView.getChildAt(0).getHeight() + mExpandableListView.getChildAt(0).getTop();
                        for(int i = 1; i < mVisibleChildsCount; i++){
                            sumHeightVisibleChildrens+= mExpandableListView.getChildAt(i).getHeight();
                        }
                    }

           //     Log.d(TAG,"Суммарная высота видимых детей = "+ sumHeightVisibleChildrens);
                    boolean statusDivider = false;
                    if( mExpandableListView.getChildAt(0).getTop() > 0 && (firstVisibleItem == (mFirstVisibleExpandedGroup + sumChildsExpandedGroup - mAdapter.getChildrenCount(mFirstVisibleExpandedGroup)))) {
                        //Log.d(TAG, "высотка делителя = " + mExpandableListView.getChildAt(0).getTop());
                        destroyHead();
                    }else {

                        if( mExpandableListView.getChildAt(0).getTop() > 0){
                            statusDivider = true;
                    //        Log.d(TAG, "высотка делителя = " + mExpandableListView.getChildAt(0).getTop());

                            sumHeightVisibleChildrens -= mExpandableListView.getChildAt(0).getTop();
                            for(int i = 1; i< mVisibleChildsCount ;i++){
                                sumHeightDivider += mExpandableListView.getDividerHeight();
                            }
                            sumFull += (sumHeightVisibleChildrens + sumHeightDivider + mExpandableListView.getChildAt(0).getTop());
                        }else {

                            for(int i = 1; i< mVisibleChildsCount ;i++){
                                sumHeightDivider += mExpandableListView.getDividerHeight();
                            }
                            sumFull += (sumHeightVisibleChildrens + sumHeightDivider);
                        }

                        buildHead(mFirstVisibleExpandedGroup);
                        if((sumFull >= mHeader.getHeight())&&((firstVisibleItem > mFirstVisibleExpandedGroup) || ((firstVisibleItem == mFirstVisibleExpandedGroup) &&(!statusDivider) ))){
                     //       Log.d(TAG,"точка 2");
                            mHeader.scrollTo(0,0);
                        } else {
                     //       Log.d(TAG,"точка 3");
                            mHeader.scrollTo(0, mHeader.getHeight() - sumFull);
                        }

                    }


                  //  Log.d(TAG, "полная высота = " + sumFull);
//
                 //   Log.d(TAG, " mExpandableListView.getSelectedPosition = "+  mExpandableListView.getSelectedPosition());

                }else {
                    if(visibleItemCount >= 2){
                        Log.d(TAG, "точка 4");

                     if(mExpandableListView.getChildAt(1).getTop() < 0){

                         if(mLastExpandedPosition != -1 && sumChildsExpandedGroup != 0 && expandedGroupsCount != 0){
                             Log.d(TAG, "точка 5");
                             if(firstVisibleItem >= (mGroupInFocus +sumChildsExpandedGroup)){
                                 destroyHead();
                             }else {
                                Log.d(TAG, "mLastExpandedPosition = "+ mLastExpandedPosition);

                                 buildHead(mLastExpandedPosition);


                             }
                         }else {destroyHead();}
                     }else {
                         Log.d(TAG, "точка 4.1");
                         destroyHead();
                     }
                    }else {
                        destroyHead();
                    }

                }
            }


        }
    }



    private void buildHead(int groupPosition){


        View previousHeader = mHeader.getChildAt(0);
        if (previousHeader != null) {
            mHeader.removeViewAt(0);
        }

        mHeaderView = mAdapter.getGroupHeaderView(groupPosition,mHeaderView,mHeader);
        mHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mHeaderView.measure(MeasureSpec.makeMeasureSpec(mHeader.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mHeaderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpandableListView.collapseGroup(mGroupInFocus);
                mExpandableListView.setSelectedGroup(mGroupInFocus);

            }
        });
        mHeader.getLayoutParams().height = mHeaderView.getMeasuredHeight();
        mHeader.addView(mHeaderView);
        mHeader.bringToFront();

       // Log.d(TAG, "Head show");
    }

    public void destroyHead(){
       // Log.d(TAG, "Head destroy");
            mHeader.removeAllViews();
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
