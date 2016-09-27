package cn.opensrc.elasticlayout.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.opensrc.elasticlayout.R;
import cn.opensrc.elasticlayout.YourElasticLayout;
import cn.opensrc.ellib.ElasticLayout;

/**
 * Author:       sharp
 * Created on:   7/20/16 1:35 PM
 * Description:
 * Revisions:
 */
public class CustomElasticLayoutAty extends AppCompatActivity{
    private String mTxt = "有哪些考试作弊的奇葩经历？\n 开考十五分钟左右，一个考生才晃晃悠悠的走进来，酒气浓重，看那个状态就已经喝多了然后他领了卷子，坐下开始考试，写了没多一会，他直接吐了，吐了一桌子，那个味道喝酒的兄弟们估计应该都知道，当时那销魂的气味简直了……还是北方的冬天，室内空气本身就不怎么流通……\n" +
            "监考老师们直接弃考场而去，我们大摇大摆的掏出小抄，顺利完成考试，当时我交卷的时候，那个喝醉的兄弟还在打扫自己的桌子……";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_cusel);
        init();
    }

    private void init() {
        final YourElasticLayout elCus = (YourElasticLayout) findViewById(R.id.elCus);
        final TextView tv = (TextView) findViewById(R.id.tv);
        assert elCus != null;
        assert tv != null;

        elCus.setOnRefreshListener(new ElasticLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("CustomElasticLayoutAty onRefresh");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                elCus.stopRefresh();
                            }
                        });

                    }
                },3000);
            }

            @Override
            public void onRefreshFinish() {
                Toast.makeText(CustomElasticLayoutAty.this,"Refresh Completed",Toast.LENGTH_SHORT).show();
                tv.setText(mTxt);
            }
        });

        elCus.setOnLoadMoreListener(new ElasticLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                System.out.println("CustomElasticLayoutAty onLoadMore");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                elCus.stopLoadMore();
                            }
                        });

                    }
                },3000);
            }

            @Override
            public void onLoadMoreFinish() {
                Toast.makeText(CustomElasticLayoutAty.this,"Loadmore Completed",Toast.LENGTH_SHORT).show();
                String currText = tv.getText() + "\n" + mTxt;
                tv.setText(currText);
            }
        });

    }

}
