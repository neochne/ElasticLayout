package cn.opensrc.elasticlayout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.opensrc.elasticlayout.R;

/**
 * Author:       sharp
 * Created on:   7/18/16 2:09 PM
 * Description:
 * Revisions:
 */
public class MainAty extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
    }

    public void toListViewAty(View v){
        startActivity(new Intent(this, ListViewAty.class));
    }

    public void toRecyclerViewAty(View v){
        startActivity(new Intent(this, RecyclerViewAty.class));
    }

    public void toCustomElasticLayout(View v){
        startActivity(new Intent(this,CustomElasticLayoutAty.class));
    }
}
