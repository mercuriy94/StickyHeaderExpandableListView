package com.mercuriy94.stickyheaderexpandablelistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;


public class ExpandableListViewStickyHeader extends RelativeLayout{
    public static final String TAG = "myLogs";
    private Context mContext;
    private RelativeLayout mHeader;
    private View mHeaderView;
    private CustomExpandableListView mExpandableListView;
    private AbsListView.OnScrollListener mOnScrollListener;
    private ExpandableListView.OnGroupExpandListener mGroupExpandListener;
    private ExpandableListView.OnGroupCollapseListener mGroupCollapseListener;
    private OnStickyHeaderClickListener mOnStickyHeaderClickListener;
    private CustomExpandableListAdapter mAdapter; //Адаптер
    private boolean mAutoCLoseGroup = false; //При открытии новой группы, предыдущая открытая закрывается
    protected boolean mOpenGroups[]; //хранение списка групп и их состояния(expanded = true/collapse = false)
    protected int mGroupInFocus = -1;
    protected int mLastExpandedPosition = -1;
    private FrameLayout mScrollView;
    private static final int FADE_DELAY = 1000;
    private static final int FADE_DURATION = 2000;
    private AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);



    public ExpandableListViewStickyHeader(Context context) {
        super(context);

    }

    public ExpandableListViewStickyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = getContext();
        init();
    }

    public ExpandableListViewStickyHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnStickyHeaderClickListener(OnStickyHeaderClickListener onStickyHeaderClickListener){
        this.mOnStickyHeaderClickListener = onStickyHeaderClickListener;
    }


    private class OnStickyHeaderGroupExpandListener implements ExpandableListView.OnGroupExpandListener{
        private OnStickyHeaderGroupExpandListener(){}


        @Override
        public void onGroupExpand(int groupPosition) {
            Log.d(TAG, "onGroupExpand.position = " + groupPosition);

            mOpenGroups[groupPosition] = true;

            mLastExpandedPosition = groupPosition;
            if (mAutoCLoseGroup) {

                // mExpandableListView.smoothScrollBy(30,500);
                for (int position = 0; position < mOpenGroups.length; position++) {
                    if (mOpenGroups[position] && position != groupPosition) {
                        mExpandableListView.collapseGroup(position);
                    }
                }
            }

            if(mGroupExpandListener != null){
                mGroupExpandListener.onGroupExpand(groupPosition);
            }
        }
    }

    private class OnStickyHeaderGroupCollapseListener implements ExpandableListView.OnGroupCollapseListener{
        @Override
        public void onGroupCollapse(int groupPosition) {
            // Log.d(TAG,"onGroupCollapse.position = " + groupPosition);
            mOpenGroups[groupPosition] = false;

            if(mGroupCollapseListener != null){
                mGroupCollapseListener.onGroupCollapse(groupPosition);
            }
        }
    }

    private class OnStickyHeaderScrollListener implements AbsListView.OnScrollListener{
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(mOnScrollListener != null){
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            Log.d(TAG, "onScroll.firstVisibleItem =" + firstVisibleItem);
            Log.d(TAG, "onScroll.visibleItemCount =" + visibleItemCount);
            updateScrollBar();
            if (mAdapter != null) {

                int sumChildsExpandedGroup = 0; //Сумма всех детей раскрытых групп
                int expandedGroupsCount = 0; //Сумма всех раскрытых групп
                int mFirstVisibleExpandedGroup = -1; //Первая раскрытая группа попавшая на экран (первая сверху)
                int sum = 0;
                int sumHeightVisibleChildrens = 0;
                int sumFull = 0;
                int sumHeightDivider = 0;


                for (int i = 0; i < mOpenGroups.length; i++) {
                    if (mOpenGroups[i]) {
                        sumChildsExpandedGroup += mAdapter.getChildrenCount(i);
                        expandedGroupsCount++;
                        if ((i + sumChildsExpandedGroup) >= firstVisibleItem && i <= firstVisibleItem) {
                            mFirstVisibleExpandedGroup = i;
                            break;
                        }
                    }
                }

                if (mFirstVisibleExpandedGroup != -1 && expandedGroupsCount > 1) {
                    if (((mFirstVisibleExpandedGroup + sumChildsExpandedGroup) - mAdapter.getChildrenCount(mFirstVisibleExpandedGroup)) > firstVisibleItem) {
                        mFirstVisibleExpandedGroup = -1;
                    }
                }

                //     Log.d(TAG, "Сумма детей раскрытых групп = " + sumChildsExpandedGroup);
                //     Log.d(TAG, "Количество раскрытых групп = " + expandedGroupsCount);
                //   Log.d(TAG, "Первая раскрытая группа попавшая на экран = " + mFirstVisibleExpandedGroup);


             /*   Log.d(TAG,"Высота 0 элемента = "+ mExpandableListView.getChildAt(0).getTop());
                for(int i =1; i< visibleItemCount; i++){
                    Log.d(TAG,"Высота "+i+" элемента = "+ mExpandableListView.getChildAt(i).getTop());
                }*/


                if (mFirstVisibleExpandedGroup != -1) {
                    mGroupInFocus = mFirstVisibleExpandedGroup;
                    int mVisibleChildsCount = 0;


                    mVisibleChildsCount = (mFirstVisibleExpandedGroup + sumChildsExpandedGroup) - firstVisibleItem + 1;
                    if (mVisibleChildsCount > visibleItemCount) {
                        mVisibleChildsCount = visibleItemCount;
                    }

                    //       Log.d(TAG,"Кол-во видимых детей = "+ mVisibleChildsCount);
                    //  buildHead(mFirstVisibleExpandedGroup);
                    if (mVisibleChildsCount > 0) {
                        sumHeightVisibleChildrens += mExpandableListView.getChildAt(0).getHeight() + mExpandableListView.getChildAt(0).getTop();
                        for (int i = 1; i < mVisibleChildsCount; i++) {
                            sumHeightVisibleChildrens += mExpandableListView.getChildAt(i).getHeight();
                        }
                    }

                    //     Log.d(TAG,"Суммарная высота видимых детей = "+ sumHeightVisibleChildrens);
                    boolean statusDivider = false;
                    if (mExpandableListView.getChildAt(0).getTop() > 0 && (firstVisibleItem == (mFirstVisibleExpandedGroup + sumChildsExpandedGroup - mAdapter.getChildrenCount(mFirstVisibleExpandedGroup)))) {
                        //Log.d(TAG, "высотка делителя = " + mExpandableListView.getChildAt(0).getTop());
                        destroyHead();
                    } else {

                        if (mExpandableListView.getChildAt(0).getTop() > 0) {
                            statusDivider = true;
                            //        Log.d(TAG, "высотка делителя = " + mExpandableListView.getChildAt(0).getTop());

                            sumHeightVisibleChildrens -= mExpandableListView.getChildAt(0).getTop();
                            for (int i = 1; i < mVisibleChildsCount; i++) {
                                sumHeightDivider += mExpandableListView.getDividerHeight();
                            }
                            sumFull += (sumHeightVisibleChildrens + sumHeightDivider + mExpandableListView.getChildAt(0).getTop());
                        } else {

                            for (int i = 1; i < mVisibleChildsCount; i++) {
                                sumHeightDivider += mExpandableListView.getDividerHeight();
                            }
                            sumFull += (sumHeightVisibleChildrens + sumHeightDivider);
                        }

                        buildHead(mFirstVisibleExpandedGroup);
                        if ((sumFull >= mHeader.getHeight()) && ((firstVisibleItem > mFirstVisibleExpandedGroup) || ((firstVisibleItem == mFirstVisibleExpandedGroup) && (!statusDivider)))) {
                            //           Log.d(TAG, "точка 2");

                            mHeader.scrollTo(0, 0);

                        } else {
                            //             Log.d(TAG,"точка 3");
                            mHeader.scrollTo(0, mHeader.getHeight() - sumFull);
                        }

                    }


                    //  Log.d(TAG, "полная высота = " + sumFull);
//
                    //   Log.d(TAG, " mExpandableListView.getSelectedPosition = "+  mExpandableListView.getSelectedPosition());

                } else {
                    if (visibleItemCount >= 2) {
                        //   Log.d(TAG, "точка 4");

                        if (mExpandableListView.getChildAt(1).getTop() < 0) {

                            if (mLastExpandedPosition != -1 && sumChildsExpandedGroup != 0 && expandedGroupsCount != 0) {
                                //         Log.d(TAG, "точка 5");
                                buildHead(mLastExpandedPosition);
                            } else {
                                destroyHead();
                            }
                        } else {
                            //    Log.d(TAG, "точка 4.1");
                            destroyHead();
                        }
                    } else {
                        destroyHead();
                    }

                }
            }

            if(mOnScrollListener != null){
                mOnScrollListener.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
            }
        }
    }

    private void init() {
        Log.d(TAG,"init");
        //Добавление списка
        mExpandableListView = new CustomExpandableListView(getContext());
        mExpandableListView.setOnGroupExpandListener(new OnStickyHeaderGroupExpandListener());
        mExpandableListView.setOnGroupCollapseListener(new OnStickyHeaderGroupCollapseListener());
        mExpandableListView.setOnScrollListener(new OnStickyHeaderScrollListener());
        LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mExpandableListView.setLayoutParams(listParams);
        mExpandableListView.setSmoothScrollbarEnabled(true);
        mExpandableListView.setSelected(true);
        mExpandableListView.setVerticalScrollBarEnabled(false);
        addView(mExpandableListView);

        //Добавление липкого заголовка
        mHeader = new RelativeLayout(getContext());
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerParams.addRule(ALIGN_PARENT_TOP);
        mHeader.setLayoutParams(headerParams);
        mHeader.setGravity(Gravity.BOTTOM);
        addView(mHeader);

        mScrollView = new FrameLayout(getContext());
        Drawable scrollBarDrawable = getResources().getDrawable(R.drawable.scrollbar_handle_holo_light);
        mScrollView = new FrameLayout(getContext());
        LayoutParams scrollParams = new LayoutParams(scrollBarDrawable.getIntrinsicWidth(), LayoutParams.MATCH_PARENT);
        scrollParams.addRule(ALIGN_PARENT_RIGHT);
        scrollParams.rightMargin = (int) dpToPx(2);
        mScrollView.setLayoutParams(scrollParams);

        ImageView scrollIndicator = new ImageView(getContext());
        scrollIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollIndicator.setImageDrawable(scrollBarDrawable);
        scrollIndicator.setScaleType(ImageView.ScaleType.FIT_XY);
        mScrollView.addView(scrollIndicator);
        mScrollView.setVisibility(INVISIBLE);

        addView(mScrollView);
    }

    public boolean isAutoCLoseGroup() {
        return mAutoCLoseGroup;
    }

    public void setAutoCLoseGroup(boolean mAutoCLoseGroup) {
        this.mAutoCLoseGroup = mAutoCLoseGroup;
    }

    public CustomExpandableListView getExpandableListView() {
        return mExpandableListView;
    }

    public void setExpandableListView(CustomExpandableListView mExpandableListView) {
        this.mExpandableListView = mExpandableListView;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public void setAdapter(CustomExpandableListAdapter adapter) {
        if (mExpandableListView != null) {
            mAdapter = adapter;
            mExpandableListView.setAdapter(mAdapter);
            mOpenGroups = new boolean[mAdapter.getGroupCount()]; //получаем список групп
        }
    }

    public CustomExpandableListAdapter getExpandableListAdapter() {

        return mAdapter;
    }


    public void setOnGroupCollapseListener(ExpandableListView.OnGroupCollapseListener mGroupCollapseListener) {
        this.mGroupCollapseListener = mGroupCollapseListener;
    }



    public void setOnScrollListener(AbsListView.OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }



    public void setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener mGroupExpandListener) {
        this.mGroupExpandListener = mGroupExpandListener;
    }





    private void updateScrollBar() {
        if (mHeader != null && mExpandableListView != null && mScrollView != null) {
            int offset = mExpandableListView.computeVerticalScrollOffset();
            int range = mExpandableListView.computeVerticalScrollRange();
            int extent = mExpandableListView.computeVerticalScrollExtent();
            mScrollView.setVisibility(extent >= range ? View.INVISIBLE : View.VISIBLE);
            if (extent >= range) {
                return;
            }
            int top = range == 0 ? mExpandableListView.getHeight() : mExpandableListView.getHeight() * offset / range;
            int bottom = range == 0 ? 0 : mExpandableListView.getHeight() - mExpandableListView.getHeight() * (offset + extent) / range;
            mScrollView.setPadding(0, top, 0, bottom);
            fadeOut.reset();
            fadeOut.setFillBefore(true);
            fadeOut.setFillAfter(true);
            fadeOut.setStartOffset(FADE_DELAY);
            fadeOut.setDuration(FADE_DURATION);
            mScrollView.clearAnimation();
            mScrollView.startAnimation(fadeOut);
        }
    }

    private void buildHead(int groupPosition) {


        View previousHeader = mHeader.getChildAt(0);
        if (previousHeader != null) {
            mHeader.removeViewAt(0);
        }

        mHeaderView = mAdapter.getGroupHeaderView(groupPosition, mHeaderView, mHeader);
        mHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mHeaderView.measure(MeasureSpec.makeMeasureSpec(mHeader.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mHeaderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpandableListView.collapseGroup(mGroupInFocus);
                mExpandableListView.setSelectedGroup(mGroupInFocus);
                mExpandableListView.smoothScrollBy(-1, 0); //fix bag
                updateScrollBar();

            }
        });
        mHeader.getLayoutParams().height = mHeaderView.getMeasuredHeight();
        mHeader.addView(mHeaderView);
        mHeader.bringToFront();
        mScrollView.bringToFront();
        requestLayout();
         Log.d(TAG, "Head show");
    }

    public void destroyHead() {
         Log.d(TAG, "Head destroy");
        mHeader.removeAllViews();
    }


    //Методы ExpandableListView

    public boolean collapseGroup(int groupPos) {
        return mExpandableListView.collapseGroup(groupPos);
    }


    public boolean expandGroup(int groupPos) {
        return mExpandableListView.expandGroup(groupPos);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean expandGroup(int groupPos, boolean animate) {
        return mExpandableListView.expandGroup(groupPos, animate);
    }




    public long getExpandableListPosition(int flatListPosition) {
        return mExpandableListView.getExpandableListPosition(flatListPosition);
    }


    public int getFlatListPosition(long packedPosition) {
        return mExpandableListView.getFlatListPosition(packedPosition);
    }


    public long getSelectedId() {
        return mExpandableListView.getSelectedId();
    }


    public long getSelectedPosition() {
        return mExpandableListView.getSelectedPosition();
    }


    public boolean isGroupExpanded(int groupPosition) {
        return mExpandableListView.isGroupExpanded(groupPosition);
    }




    public boolean performItemClick(View v, int position, long id) {
        return mExpandableListView.performItemClick(v, position, id);
    }




    public void setAdapter(ListAdapter adapter) {
        mExpandableListView.setAdapter(adapter);
    }
    public ListAdapter getAdapter() {
        return mExpandableListView.getAdapter();
    }

    public void setChildDivider(Drawable childDivider) {
        mExpandableListView.setChildDivider(childDivider);
    }


    public void setChildIndicator(Drawable childIndicator) {
        mExpandableListView.setChildIndicator(childIndicator);
    }


    public void setChildIndicatorBounds(int left, int right) {
        mExpandableListView.setChildIndicatorBounds(left, right);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setChildIndicatorBoundsRelative(int start, int end) {
        mExpandableListView.setChildIndicatorBoundsRelative(start, end);
    }


    public void setGroupIndicator(Drawable groupIndicator) {
        mExpandableListView.setGroupIndicator(groupIndicator);
    }


    public void setIndicatorBounds(int left, int right) {
        mExpandableListView.setIndicatorBounds(left, right);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setIndicatorBoundsRelative(int start, int end) {
        mExpandableListView.setIndicatorBoundsRelative(start, end);
    }


    public void setOnChildClickListener(ExpandableListView.OnChildClickListener onChildClickListener) {
        mExpandableListView.setOnChildClickListener(onChildClickListener);
    }


    public void setOnGroupClickListener(ExpandableListView.OnGroupClickListener onGroupClickListener) {
        mExpandableListView.setOnGroupClickListener(onGroupClickListener);
    }


    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        mExpandableListView.setOnItemClickListener(l);
    }


    public boolean setSelectedChild(int groupPosition, int childPosition, boolean shouldExpandGroup) {
        return mExpandableListView.setSelectedChild(groupPosition, childPosition, shouldExpandGroup);
    }


    public void setSelectedGroup(int groupPosition) {
        mExpandableListView.setSelectedGroup(groupPosition);
    }


    public void setDivider(Drawable divider) {
        mExpandableListView.setDivider(divider);
    }


    public void setDividerHeight(int height) {
        mExpandableListView.setDividerHeight(height);
    }


    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        mExpandableListView.setFooterDividersEnabled(footerDividersEnabled);
    }


    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        mExpandableListView.setHeaderDividersEnabled(headerDividersEnabled);
    }
}
