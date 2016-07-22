package cn.opensrc.elasticlayout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.opensrc.elasticlayout.R;

/**
 * Author:       sharp
 * Created on:   7/18/16 4:35 PM
 * Description:
 * Revisions:
 */
public class RecyclerViewAdp extends RecyclerView.Adapter<RecyclerViewAdp.MyRecyclerViewHolder>{

    private List<String> mData;
    private Context mCtx;
    public RecyclerViewAdp(List<String> mData) {
        this.mData = mData;
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mCtx = parent.getContext();
        return new MyRecyclerViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.rvitem_fake,parent,false));
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, final int position) {
        holder.tv.setText(mData.get(position));
        holder.llRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx,""+position,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyRecyclerViewHolder extends RecyclerView.ViewHolder{
        LinearLayout llRv;
        TextView tv;
        public MyRecyclerViewHolder(View itemView) {
            super(itemView);
            llRv = (LinearLayout) itemView.findViewById(R.id.llRv);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
