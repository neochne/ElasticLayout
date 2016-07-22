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
    private String mTxt = "习近平总书记19日上午来到宁夏回族自治区银川市新城清真寺了解宗教活动开展情况。在礼拜大殿，习近平与宁夏伊斯兰教界人士亲切交流，通过他们向广大穆斯林群众致以亲切问候和良好祝愿。习近平说，我国的各民族和宗教是在5000多年的文明史中孕育发展起来的，只有落地生根才能生生不息。习近平希望大家继续发扬爱国爱教传统，在脱贫致富奔小康的道路上发挥积极作用。在场的伊斯兰教界人士表示，将认真贯彻落实全国宗教工作会议精神，既念好古兰经，又念好致富经，为国家发展营造和谐的环境。\n" +
            "        坐在北京西站的地上，提起在肯德基门口拉横幅的人群，赵大龙难掩气愤。他的嘴角还肿着，左眼仍有青紫，右手手臂内侧有一块不大不小的乌青。";
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
                String currText = tv.getText() + mTxt;
                tv.setText(currText);
            }
        });

    }

}
