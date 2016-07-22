package cn.opensrc.elasticlayout.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.opensrc.elasticlayout.R;

/**
 * Author:       sharp
 * Created on:   7/20/16 11:35 AM
 * Description:
 * Revisions:
 */
public class YourElasticLayout extends ElasticLayout{

    private ImageView ivHeader,ivFooter;
    private TextView tvFooter;
    private AnimationDrawable mAnimationDrawable;
    public YourElasticLayout(Context context) {
        super(context);
        YourInit();
    }

    public YourElasticLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        YourInit();
    }

    private void YourInit(){
        setRefreshMode(true);
        setLoadMoreMode(true);
        onUIRefreshListener = new YourOnUIRefreshListener();
        onUILoadMoreListener = new YourOnUILoadmoreListener();
        setOnUIRefreshListener(onUIRefreshListener);
        setOnUILoadMoreListener(onUILoadMoreListener);
    }

    /**
     * set your headerview
     */
    @Override
    public void setHeaderView(View headerView) {
        mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.layout_cusheader,null,false);
        ivHeader = (ImageView) mHeaderView.findViewById(R.id.ivHeader);
        ivHeader.setBackgroundResource(R.drawable.dgl);
    }

    /**
     * set your footerview
     */
    @Override
    public void setFooterView(View footerView) {
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.layout_cusfooter,null,false);
        ivFooter = (ImageView) mFooterView.findViewById(R.id.ivFooter);
        tvFooter = (TextView) mFooterView.findViewById(R.id.tvFooter);
        ivFooter.setBackgroundResource(R.drawable.dgl);
        tvFooter.setText("上拉加载更多");
    }

    /**
     * define your own UI RefreshListener
     */
    private class YourOnUIRefreshListener implements OnUIRefreshListener{

        @Override
        public void onUIBeforeRefresh() {
        }

        @Override
        public void onUIRefresh() {
            ivHeader.setBackgroundResource(R.drawable.list_loading);
            mAnimationDrawable = (AnimationDrawable) ivHeader.getBackground();
            mAnimationDrawable.start();
        }

        @Override
        public void onUIAfterRefresh() {

        }

        @Override
        public void onUIRefreshFinish() {
            mAnimationDrawable.stop();
        }

        @Override
        public void onUIWholeRefresh(int deltaY) {
            System.out.println("YourDeltaY onUIWholeRefresh" +deltaY);
            int absY = Math.abs(deltaY);
            switchImg(absY,ivHeader);

        }

    }

    /**
     * define your own UI LoadMoreListener
     */
    private class YourOnUILoadmoreListener implements OnUILoadMoreListener{

        @Override
        public void onUIBeforeLoadMore() {

        }

        @Override
        public void onUILoadMore() {
            ivFooter.setVisibility(View.VISIBLE);
            tvFooter.setVisibility(View.GONE);
            ivFooter.setBackgroundResource(R.drawable.list_loading);
            mAnimationDrawable = (AnimationDrawable) ivFooter.getBackground();
            mAnimationDrawable.start();
        }

        @Override
        public void onUILoadMoreFinish() {
            mAnimationDrawable.stop();
        }

        @Override
        public void onUIWholeLoadMore(int deltaY) {
            System.out.println("YourDeltaY onUIWholeLoadMore" +deltaY);
            ivFooter.setVisibility(View.GONE);
            tvFooter.setVisibility(View.VISIBLE);
        }
    }

    /**
     * switch img
     */
    private void switchImg(int absY,ImageView iv){

        if (absY > 10 && absY < 30){
            iv.setBackgroundResource(R.drawable.dgl);
        }else if (absY > 30 && absY < 50){
            iv.setBackgroundResource(R.drawable.dgm);
        }else if (absY > 50 && absY < 70){
            iv.setBackgroundResource(R.drawable.dgn);
        }else if (absY > 70 && absY < 90){
            iv.setBackgroundResource(R.drawable.dgo);
        }else if (absY > 90 && absY < 110){
            iv.setBackgroundResource(R.drawable.dgp);
        }else if (absY > 110 && absY < 130){
            iv.setBackgroundResource(R.drawable.dgq);
        }else if (absY > 130 && absY < 150){
            iv.setBackgroundResource(R.drawable.dgr);
        }else if (absY > 150 && absY < 170){
            iv.setBackgroundResource(R.drawable.dgs);
        }else if (absY > 170 && absY < 190){
            iv.setBackgroundResource(R.drawable.dgt);
        }else if (absY > 190 && absY < 210){
            iv.setBackgroundResource(R.drawable.dgu);
        }else if (absY > 210 && absY < 230){
            iv.setBackgroundResource(R.drawable.dgv);
        }else if (absY > 230 && absY < 250){
            iv.setBackgroundResource(R.drawable.dgw);
        }

    }

}
