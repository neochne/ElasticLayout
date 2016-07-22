package cn.opensrc.elasticlayout.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.opensrc.elasticlayout.R;
import cn.opensrc.elasticlayout.view.ElasticLayout;

/**
 * Author:       sharp
 * Created on:   7/18/16 2:26 PM
 * Description:
 * Revisions:
 */
public class ListViewAty extends AppCompatActivity{
    private List<String> mRefreshData = new ArrayList<>();
    private List<String> mCurrData = new ArrayList<>();
    private ArrayAdapter<String> mAdp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_listview);
        init();
    }

    private void init(){
        ListView lv = (ListView) findViewById(R.id.lv);
        assert lv != null;

        for (int i = 0; i < 3;i++){
            mRefreshData.add("item"+i);
        }

        mAdp = new ArrayAdapter<>(this,R.layout.lvitem_fake,R.id.tvItem,mCurrData);
        lv.setAdapter(mAdp);
        mCurrData.clear();
        mCurrData.addAll(mRefreshData);
        mAdp.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListViewAty.this,""+position,Toast.LENGTH_SHORT).show();
            }
        });
        listen();
    }

    private void listen(){
        final ElasticLayout el = (ElasticLayout) findViewById(R.id.el);
        assert el != null;
        el.setRefreshMode(true);
//        el.setLoadMoreMode(true);

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
                Toast.makeText(ListViewAty.this,"Refresh Completed",Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(ListViewAty.this,"Loadmore Completed",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
