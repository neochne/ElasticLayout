package cn.opensrc.elasticlayout.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.opensrc.elasticlayout.R;
import cn.opensrc.elasticlayout.adapter.RecyclerViewAdp;
import cn.opensrc.ellib.ElasticLayout;

/**
 * Author:       sharp
 * Created on:   7/18/16 2:26 PM
 * Description:
 * Revisions:
 */
public class RecyclerViewAty extends AppCompatActivity{
    private List<String> mRefreshData = new ArrayList<>();
    private List<String> mCurrData = new ArrayList<>();
    private RecyclerViewAdp mAdp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_recyclerview);
        init();
    }
    private void init(){

        for (int i = 0; i < 3;i++){
            mRefreshData.add("item"+i);
        }

        mCurrData.addAll(mRefreshData);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        assert rv != null;
        mAdp = new RecyclerViewAdp(mCurrData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(mAdp);
        listen();
    }
    private void listen(){
        final ElasticLayout el = (ElasticLayout) findViewById(R.id.el);
        assert el != null;
        el.setRefreshMode(true);
        el.setLoadMoreMode(true);
        // frefresh
        el.setOnRefreshListener(new ElasticLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCurrData.clear();
                                mCurrData.addAll(mRefreshData);
                                mAdp.notifyDataSetChanged();
                                el.stopRefresh();
                            }
                        });
                    }
                },3000);
            }

            @Override
            public void onRefreshFinish() {
                Toast.makeText(RecyclerViewAty.this,"Refresh Completed",Toast.LENGTH_SHORT).show();
            }
        });

        // loadmore
        el.setOnLoadMoreListener(new ElasticLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCurrData.addAll(mRefreshData);
                                mAdp.notifyDataSetChanged();
                                el.stopLoadMore();
                            }
                        });
                    }
                },3000);
            }

            @Override
            public void onLoadMoreFinish() {
                Toast.makeText(RecyclerViewAty.this,"Loadmore Completed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
