package cn.mrlong.audiorecord;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.mrlong.audiorecord.recorder.Recorder;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioHolder> {
    private Context context;
    private List<Recorder> datas = new ArrayList<>();
    private ItemOnClickListener itemOnClickListener;

    public AudioAdapter(Context context, ItemOnClickListener itemOnClickListener) {
        this.context = context;
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public AudioHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, viewGroup, false);
        AudioHolder audioHolder = new AudioHolder(v);
        return audioHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioHolder audioHolder, int i) {
        audioHolder.setData(i);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addData(Recorder recorder) {
        this.datas.add(recorder);
        notifyDataSetChanged();
    }

    public List<Recorder> getDatas() {
        return datas;
    }

    public void addDatas(List<Recorder> d) {
        this.datas.clear();
        this.datas.addAll(d);
        notifyDataSetChanged();
    }

    public Recorder getData(int i) {
        return datas.get(i);
    }

    class AudioHolder extends RecyclerView.ViewHolder {
        TextView val;

        public AudioHolder(@NonNull View itemView) {
            super(itemView);
            val = itemView.findViewById(R.id.val);

        }

        public void setData(final int i) {
            Recorder data = datas.get(i);
            val.setText("地址 =  " + data.getFilePathString());
            val.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemOnClickListener)
                        itemOnClickListener.itemOnClickListener(i);
                }
            });
        }
    }
}