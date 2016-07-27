package cn.opensrc.ellib;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Author:       sharp
 * Created on:   7/7/16 2:26 PM
 * Description:
 * Revisions:
 */
public class ElasticLayout extends LinearLayout {

    // ----------------------------------------------------
    // Member Variable Block
    // ----------------------------------------------------

    public Context mContext;
    private View mTarget;

    // Header
    public View mHeaderView;
    private ImageView ivDefaultOfHeader;
    private TextView tvDefaultOfHeader;
    private int mHeaderViewHeight;

    // Footer
    public View mFooterView;
    private ImageView ivDefaultOfFooter;
    private TextView tvDefaultOfFooter;

    // Scroll
    private int mDownY, mTouchSlop, mScrolledYDistance;
    private MarginLayoutParams mHeaderViewLayoutParams;
    private Scroller mScroller = null;
    private int refreshPosition = 200;
    private int loadMorePosition = 100;
    private boolean mIsPullDownBeforeRefresh = true;
    private boolean mIsPullUpBeforeLoadMore = true;
    private float scrollRate = .5f;
    private boolean isUIRefreshing, isUILoading;
    private boolean refreshMode;
    private boolean loadMoreMode;

    // User is Scrolled ElasticLayout
    private boolean isScrolled;

    // is this initialized
    private boolean isInited;

    // animation
    private Animation mCwRotateAnim, mCcwRotateAnim, mCwRotateAnimRefreshing;

    // CallBack
    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;
    public OnUIRefreshListener onUIRefreshListener;
    public OnUILoadMoreListener onUILoadMoreListener;

    // ----------------------------------------------------
    // Constructor Block
    // ----------------------------------------------------

    public ElasticLayout(Context context) {
        super(context);
        mContext = context;
        mScroller = null;
        mScroller = new Scroller(context);
        init();
    }

    public ElasticLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScroller = null;
        mScroller = new Scroller(context);
        init();
    }

    // ----------------------------------------------------
    // Override Method Block
    // ----------------------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        computeScroll();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (isInited) return;

        this.removeView(mFooterView);
        this.addView(mFooterView);
        mFooterView.setVisibility(View.INVISIBLE);
        mHeaderViewHeight = mHeaderView.getHeight();
        mTarget = getChildAt(1);

        // header scroll out of screen
        mHeaderViewLayoutParams.topMargin = -mHeaderViewHeight;
        isInited = true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mFooterView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(200, MeasureSpec.AT_MOST));
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            mHeaderViewLayoutParams.topMargin = -mHeaderViewHeight + mScrolledYDistance - mScroller.getCurrY();
            requestLayout();
            postInvalidate();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercepted = false;
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDownY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                // forbid usr scroll content
                if (isUIRefreshing || isUILoading)
                    intercepted = true;

                // refresh
                if ((ev.getY() - mDownY > 0) && isChildScrollToTop())
                    intercepted = true;

                // loadmore
                if ((ev.getY() - mDownY < 0) && isChildScrollToBottom())
                    intercepted = true;

                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return intercepted;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_UP:
                up(event);
                break;

            default:
                break;

        }

        return true;

    }

    // ----------------------------------------------------
    // Custom Method Block
    // ----------------------------------------------------

    private void smoothScrollTo(int deltaY) {

        mScroller.startScroll(0, 0, 0, deltaY, 500);
        postInvalidate();

    }

    private void move(MotionEvent event) {

        if (isUIRefreshing || isUILoading) return;

        mScrolledYDistance = Math.round((event.getY() - mDownY) * scrollRate);
        if (Math.abs(mScrolledYDistance) >= mTouchSlop) isScrolled = true;

        if (mScrolledYDistance > 0 && refreshMode) {

            onUIRefreshListener.onUIWholeRefresh(mScrolledYDistance);

            if (mScrolledYDistance >= refreshPosition && mIsPullDownBeforeRefresh) {
                mIsPullDownBeforeRefresh = false;
                onUIRefreshListener.onUIAfterRefresh();
            } else if (mScrolledYDistance < refreshPosition && !mIsPullDownBeforeRefresh) {
                mIsPullDownBeforeRefresh = true;
                onUIRefreshListener.onUIBeforeRefresh();
            }

        } else if (mScrolledYDistance < 0 && loadMoreMode && mFooterView.getTop() <= getHeight()) {

            onUILoadMoreListener.onUIWholeLoadMore(mScrolledYDistance);

            mFooterView.setVisibility(View.VISIBLE);
            int dis = Math.abs(mScrolledYDistance);

            if (dis < loadMorePosition && !mIsPullUpBeforeLoadMore) {
                mIsPullUpBeforeLoadMore = true;
                onUILoadMoreListener.onUIBeforeLoadMore();
            } else if (dis >= loadMorePosition) {
                isUILoading = true;
                mIsPullUpBeforeLoadMore = true;
                onUILoadMoreListener.onUILoadMore();
                if (onLoadMoreListener != null)
                    onLoadMoreListener.onLoadMore();
            }

        }

        mHeaderViewLayoutParams.topMargin = -mHeaderViewHeight + mScrolledYDistance;
        mHeaderView.requestLayout();

    }

    private void up(MotionEvent event) {

        if (!isScrolled) return;

        if (isUIRefreshing || isUILoading) return;

        if (mScrolledYDistance > 0 && mScrolledYDistance >= refreshPosition && refreshMode) {
            mIsPullDownBeforeRefresh = true;
            isUIRefreshing = true;
            int externalDeltaY = Math.abs(mScrolledYDistance) - refreshPosition;
            smoothScrollTo(externalDeltaY);
            onUIRefreshListener.onUIRefresh();
            if (onRefreshListener != null)
                onRefreshListener.onRefresh();
        } else {
            smoothScrollTo(mScrolledYDistance);
        }
        isScrolled = false;

    }

    /**
     * @return is child scroll to top
     */
    public boolean isChildScrollToTop() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return !(absListView.getChildCount() > 0 && (absListView
                        .getFirstVisiblePosition() > 0 || absListView
                        .getChildAt(0).getTop() < absListView.getPaddingTop()));
            } else {
                return !(mTarget.getScrollY() > 0);
            }
        } else {
            return !ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /**
     * @return is child scroll to bottom
     */
    public boolean isChildScrollToBottom() {

//        if (isChildScrollToTop()) {
//            return false;
//        }

        if (mTarget instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mTarget;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount();
            if (layoutManager instanceof LinearLayoutManager && count > 0) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastItems = new int[2];
                staggeredGridLayoutManager
                        .findLastCompletelyVisibleItemPositions(lastItems);
                int lastItem = Math.max(lastItems[0], lastItems[1]);
                if (lastItem == count - 1) {
                    return true;
                }
            }
            return false;
        } else if (mTarget instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mTarget;
            int count = absListView.getAdapter().getCount();
            int firstPos = absListView.getFirstVisiblePosition();

            int lastPos = absListView.getLastVisiblePosition();
            if (lastPos > 0 && count > 0 && lastPos == count - 1) {
                return true;
            }

            if (firstPos == 0 && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop()) {
                return false;
            }

            return false;
        } else if (mTarget instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mTarget;
            View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (view != null) {
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff == 0) {
                    return true;
                }else{
                    return false;
                }

            }
        }
        return true;
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        setHeaderView(null);
        setFooterView(null);
        setRefreshMode(refreshMode);
        setLoadMoreMode(loadMoreMode);
        this.addView(mHeaderView, 0);
        mHeaderViewLayoutParams = (MarginLayoutParams) mHeaderView.getLayoutParams();
        onUIRefreshListener = new DefaultUIRefreshListener();
        onUILoadMoreListener = new DefaultUILoadMoreListener();
        setOnUIRefreshListener(onUIRefreshListener);
        setOnUILoadMoreListener(onUILoadMoreListener);
        mCwRotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_180cw);
        mCcwRotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_180ccw);
        mCwRotateAnimRefreshing = AnimationUtils.loadAnimation(mContext, R.anim.rotate_360cw);
        mCwRotateAnimRefreshing.setInterpolator(new LinearInterpolator());
    }

    // API start

    public void setRefreshPosition(int refreshPosition) {
        this.refreshPosition = refreshPosition;
    }

    public void setLoadMorePosition(int loadMorePosition) {
        this.loadMorePosition = loadMorePosition;
    }

    public void setScrollRate(float scrollRate) {
        this.scrollRate = scrollRate;
    }

    public void setHeaderView(View headerView) {

        if (headerView != null) {
            ivDefaultOfHeader = null;
            tvDefaultOfHeader = null;
            this.removeView(mHeaderView);
            mHeaderView = headerView;
            this.addView(mHeaderView, 0);
            return;
        }

        if (mHeaderView != null) return;

        LinearLayout llHeader = new LinearLayout(mContext);
        llHeader.setOrientation(LinearLayout.HORIZONTAL);
        llHeader.setGravity(Gravity.CENTER);
        llHeader.setPadding(0, 20, 0, 20);

        ivDefaultOfHeader = new ImageView(mContext);
        ivDefaultOfHeader.setImageResource(R.drawable.ic_elarrow);
        llHeader.addView(ivDefaultOfHeader);

        tvDefaultOfHeader = new TextView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = 20;
        tvDefaultOfHeader.setLayoutParams(params);
        tvDefaultOfHeader.setText("下拉刷新");
        llHeader.addView(tvDefaultOfHeader);
        mHeaderView = llHeader;
    }

    public void setFooterView(View footerView) {

        if (footerView != null) {
            ivDefaultOfFooter = null;
            tvDefaultOfFooter = null;
            this.removeView(mFooterView);
            mFooterView = footerView;
            this.addView(mFooterView);
            return;
        }

        if (mFooterView != null) return;

        LinearLayout llFooter = new LinearLayout(mContext);
        llFooter.setOrientation(LinearLayout.HORIZONTAL);
        llFooter.setGravity(Gravity.CENTER);
        llFooter.setPadding(0, 20, 0, 20);

        ivDefaultOfFooter = new ImageView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.rightMargin = 20;
        ivDefaultOfFooter.setLayoutParams(params);
        ivDefaultOfFooter.setImageResource(R.drawable.ic_elrotate);
        llFooter.addView(ivDefaultOfFooter);
        ivDefaultOfFooter.setVisibility(View.GONE);

        tvDefaultOfFooter = new TextView(mContext);
        tvDefaultOfFooter.setText("上拉加载更多");
        llFooter.addView(tvDefaultOfFooter);

        mFooterView = llFooter;

    }

    public void setRefreshMode(boolean refresh) {
        this.refreshMode = refresh;
        if (mHeaderView != null) {
            if (refresh) mHeaderView.setVisibility(View.VISIBLE);
            else mHeaderView.setVisibility(View.INVISIBLE);
        }
    }

    public void setLoadMoreMode(boolean loadMore) {
        this.loadMoreMode = loadMore;
        if (mFooterView != null) {
            if (loadMore) mFooterView.setVisibility(View.VISIBLE);
            else mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnRefreshListener(@NonNull OnRefreshListener onRefreshListener) {
        this.onRefreshListener = null;
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = null;
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnUIRefreshListener(@NonNull OnUIRefreshListener onUIRefreshListener) {
        this.onUIRefreshListener = null;
        this.onUIRefreshListener = onUIRefreshListener;
    }

    public void setOnUILoadMoreListener(@NonNull OnUILoadMoreListener onUILoadMoreListener) {
        this.onUILoadMoreListener = null;
        this.onUILoadMoreListener = onUILoadMoreListener;
    }

    public void stopRefresh() {
        if (!isUIRefreshing) return;
        onUIRefreshListener.onUIRefreshFinish();
        mScrolledYDistance = refreshPosition;
        smoothScrollTo(mScrolledYDistance);
        isUIRefreshing = false;
        if (onRefreshListener != null)
            onRefreshListener.onRefreshFinish();
    }

    public void stopLoadMore() {
        if (!isUILoading) return;
        onUILoadMoreListener.onUILoadMoreFinish();
        mFooterView.setVisibility(View.GONE);
        smoothScrollTo(mScrolledYDistance);
        isUILoading = false;
        if (onLoadMoreListener != null)
            onLoadMoreListener.onLoadMoreFinish();
    }

    // API end

    // Interface
    public interface OnRefreshListener {
        void onRefresh();

        void onRefreshFinish();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();

        void onLoadMoreFinish();
    }

    public interface OnUIRefreshListener {
        void onUIBeforeRefresh();

        void onUIRefresh();

        void onUIAfterRefresh();

        void onUIRefreshFinish();

        void onUIWholeRefresh(int deltaY);
    }

    public  interface OnUILoadMoreListener {
        void onUIBeforeLoadMore();

        void onUILoadMore();

        void onUILoadMoreFinish();

        void onUIWholeLoadMore(int deltaY);
    }

    // default ui interface implements
    public class DefaultUIRefreshListener implements OnUIRefreshListener {


        @Override
        public void onUIBeforeRefresh() {

            if (ivDefaultOfHeader == null || tvDefaultOfHeader == null) return;

            mCwRotateAnim.setFillAfter(false);
            ivDefaultOfHeader.setImageResource(R.drawable.ic_elarrow);
            tvDefaultOfHeader.setText("下拉刷新");
        }

        @Override
        public void onUIRefresh() {

            if (ivDefaultOfHeader == null || tvDefaultOfHeader == null) return;

            mCwRotateAnim.cancel();
            ivDefaultOfHeader.setImageResource(R.drawable.ic_elrotate);
            ivDefaultOfHeader.startAnimation(mCwRotateAnimRefreshing);
            tvDefaultOfHeader.setText("正在刷新...");

        }

        @Override
        public void onUIAfterRefresh() {

            if (ivDefaultOfHeader == null || tvDefaultOfHeader == null) return;

            mCwRotateAnim.setFillAfter(true);
            ivDefaultOfHeader.startAnimation(mCwRotateAnim);
            tvDefaultOfHeader.setText("松手刷新");
        }

        @Override
        public void onUIRefreshFinish() {

            if (ivDefaultOfHeader == null || tvDefaultOfHeader == null) return;

            ivDefaultOfHeader.clearAnimation();
            ivDefaultOfHeader.setImageResource(R.drawable.ic_elarrow);
            tvDefaultOfHeader.setText("下拉刷新");
        }

        @Override
        public void onUIWholeRefresh(int deltaY) {

        }


    }

    public class DefaultUILoadMoreListener implements OnUILoadMoreListener {

        @Override
        public void onUIBeforeLoadMore() {

            if (ivDefaultOfFooter == null || tvDefaultOfFooter == null) return;

            tvDefaultOfFooter.setText("上拉加载更多");
            ivDefaultOfFooter.setVisibility(View.GONE);

        }

        @Override
        public void onUILoadMore() {

            if (ivDefaultOfFooter == null || tvDefaultOfFooter == null) return;

            tvDefaultOfFooter.setText("正在加载...");
            ivDefaultOfFooter.setVisibility(View.VISIBLE);
            ivDefaultOfFooter.startAnimation(mCwRotateAnimRefreshing);
        }

        @Override
        public void onUILoadMoreFinish() {

            if (ivDefaultOfFooter == null || tvDefaultOfFooter == null) return;

            tvDefaultOfFooter.setText("上拉加载更多");
            ivDefaultOfFooter.clearAnimation();
            ivDefaultOfFooter.setVisibility(View.GONE);

        }

        @Override
        public void onUIWholeLoadMore(int deltaY) {

        }

    }

}
