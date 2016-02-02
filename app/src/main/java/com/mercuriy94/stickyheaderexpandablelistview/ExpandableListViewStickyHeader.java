package com.mercuriy94.stickyheaderexpandablelistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ExpandableListViewStickyHeader extends RelativeLayout implements AbsListView.OnScrollListener,
                                                                                ExpandableListView.OnGroupExpandListener,
                                                                                ExpandableListView.OnGroupCollapseListener {
    public static final String TAG = "myLogs";
    private Context mContext;
    private RelativeLayout mHeader;
    private View mHeaderView;
    private CustomExpandableListView mExpandableListView;
    private AbsListView.OnScrollListener mOnScrollListener;
    private ExpandableListView.OnGroupExpandListener mGroupExpandListener;
    private ExpandableListView.OnGroupCollapseListener mGroupCollapseListener;

    private CustomExpandableListAdapter mAdapter; //Адаптер
    private boolean mAutoCLoseGroup = true; //При открытии новой группы, предыдущая открытая закрывается
    protected boolean mOpenGroups[]; //хранение списка групп и их состояния(expanded = true/collapse = false)
    protected int mGroupInFocus = -1;
    protected int mLastExpandedPosition = -1;
    private FrameLayout mScrollView;
    private static final int FADE_DELAY = 1000;
    private static final int FADE_DURATION = 2000;
    private AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);

    private ExpandableListViewStickyHeader(Context context,
                                           boolean autoCLoseGroup,
                                           AbsListView.OnScrollListener mOnScrollListener,
                                           ExpandableListView.OnGroupExpandListener mGroupExpandListener,
                                           ExpandableListView.OnGroupCollapseListener mGroupCollapseListener) {
        super(context);
        this.mContext = context;
        this.mAutoCLoseGroup = autoCLoseGroup;
        this.mOnScrollListener = mOnScrollListener;
        this.mGroupExpandListener = mGroupExpandListener;
        this.mGroupCollapseListener = mGroupCollapseListener;

        init();
    }


    private void init() {
        //Добавление списка
        mExpandableListView = new CustomExpandableListView(mContext);
        mExpandableListView.setOnGroupExpandListener(this);
        mExpandableListView.setOnGroupCollapseListener(this);
        mExpandableListView.setOnScrollListener(this);
        LayoutParams listParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mExpandableListView.setLayoutParams(listParams);
        mExpandableListView.setSmoothScrollbarEnabled(true);
        mExpandableListView.setSelected(true);
        mExpandableListView.setVerticalScrollBarEnabled(false);
        addView(mExpandableListView);

        //Добавление липкого заголовка
        mHeader = new RelativeLayout(mContext);
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerParams.addRule(ALIGN_PARENT_TOP);
        mHeader.setLayoutParams(headerParams);
        mHeader.setGravity(Gravity.BOTTOM);
        addView(mHeader);

        mScrollView = new FrameLayout(mContext);
        Drawable scrollBarDrawable = getResources().getDrawable(R.drawable.scrollbar_handle_holo_light);
        mScrollView = new FrameLayout(getContext());
        LayoutParams scrollParams = new LayoutParams(scrollBarDrawable.getIntrinsicWidth(), LayoutParams.MATCH_PARENT);
        scrollParams.addRule(ALIGN_PARENT_RIGHT);
        scrollParams.rightMargin = (int) dpToPx(2);
        mScrollView.setLayoutParams(scrollParams);

        ImageView scrollIndicator = new ImageView(mContext);
        scrollIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollIndicator.setImageDrawable(scrollBarDrawable);
        scrollIndicator.setScaleType(ImageView.ScaleType.FIT_XY);
        mScrollView.addView(scrollIndicator);
        mScrollView.setVisibility(INVISIBLE);

        addView(mScrollView);
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

    public CustomExpandableListAdapter getAdapter() {
        return mAdapter;
    }

    public ExpandableListView.OnGroupCollapseListener getGroupCollapseListener() {
        return mGroupCollapseListener;
    }

    public void setGroupCollapseListener(ExpandableListView.OnGroupCollapseListener mGroupCollapseListener) {
        this.mGroupCollapseListener = mGroupCollapseListener;
    }

    public AbsListView.OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }

    public ExpandableListView.OnGroupExpandListener getGroupExpandListener() {
        return mGroupExpandListener;
    }

    public void setGroupExpandListener(ExpandableListView.OnGroupExpandListener mGroupExpandListener) {
        this.mGroupExpandListener = mGroupExpandListener;
    }

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

    @Override
    public void onGroupCollapse(int groupPosition) {
        // Log.d(TAG,"onGroupCollapse.position = " + groupPosition);
        mOpenGroups[groupPosition] = false;

        if(mGroupCollapseListener != null){
            mGroupCollapseListener.onGroupCollapse(groupPosition);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(mOnScrollListener != null){
            mOnScrollListener.onScrollStateChanged(view,scrollState);
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

            //       Log.d(TAG, "Сумма детей раскрытых групп = " + sumChildsExpandedGroup);
            //       Log.d(TAG, "Количество раскрытых групп = " + expandedGroupsCount);
            //     Log.d(TAG, "Первая раскрытая группа попавшая на экран = " + mFirstVisibleExpandedGroup);


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
                        //       Log.d(TAG,"точка 2");
                        mHeader.scrollTo(0, 0);
                    } else {
                        //       Log.d(TAG,"точка 3");
                        mHeader.scrollTo(0, mHeader.getHeight() - sumFull);
                    }

                }


                //  Log.d(TAG, "полная высота = " + sumFull);
//
                //   Log.d(TAG, " mExpandableListView.getSelectedPosition = "+  mExpandableListView.getSelectedPosition());

            } else {
                if (visibleItemCount >= 2) {
                //    Log.d(TAG, "точка 4");

                    if (mExpandableListView.getChildAt(1).getTop() < 0) {

                        if (mLastExpandedPosition != -1 && sumChildsExpandedGroup != 0 && expandedGroupsCount != 0) {
                     //       Log.d(TAG, "точка 5");
                            buildHead(mLastExpandedPosition);
                        } else {
                            destroyHead();
                        }
                    } else {
                     //   Log.d(TAG, "точка 4.1");
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

            }
        });
        mHeader.getLayoutParams().height = mHeaderView.getMeasuredHeight();
        mHeader.addView(mHeaderView);
        mHeader.bringToFront();
        mScrollView.bringToFront();

        // Log.d(TAG, "Head show");
    }

    public void destroyHead() {
        // Log.d(TAG, "Head destroy");
        mHeader.removeAllViews();
    }

    public static class Builder {


        private Context mContext;
        private boolean mAutoCloseGroupe = true;
        private AbsListView.OnScrollListener mOnScrollListener;
        private ExpandableListView.OnGroupExpandListener mGroupExpandListener;
        private ExpandableListView.OnGroupCollapseListener mGroupCollapseListener;


        public Builder setContext(Context mContext) {
            this.mContext = mContext;
            return this;
        }

        public Builder setAutoCloseGroupe(boolean autoCloseGroupe) {
            this.mAutoCloseGroupe = autoCloseGroupe;
            return this;
        }


        public Builder setOnScrollListener(AbsListView.OnScrollListener mOnScrollListener) {
            this.mOnScrollListener = mOnScrollListener;
            return this;
        }

        public Builder setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener mGroupExpandListener) {
            this.mGroupExpandListener = mGroupExpandListener;
            return this;
        }

        public Builder setOnGroupCollapseListener(ExpandableListView.OnGroupCollapseListener mGroupCollapseListener) {
            this.mGroupCollapseListener = mGroupCollapseListener;
            return this;
        }

        public ExpandableListViewStickyHeader build() {
            return new ExpandableListViewStickyHeader(mContext,
                    mAutoCloseGroupe,
                    mOnScrollListener,
                    mGroupExpandListener,
                    mGroupCollapseListener);
        }

    }

}
