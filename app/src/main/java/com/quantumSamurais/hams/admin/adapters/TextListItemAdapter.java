package com.quantumSamurais.hams.admin.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;

import java.util.ArrayList;

public class TextListItemAdapter extends RecyclerView.Adapter<TextListItemAdapter.listItemViewHolder> {

    private final Context currentContext;
    private ArrayList<String> data;


    public TextListItemAdapter(Context context, ArrayList<String> list) {
        this.currentContext = context;
        this.data = list;
    }
    @NonNull
    @Override
    public listItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.text_list_item,parent,false);
        return new listItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull listItemViewHolder holder, int position) {
        String positionData =  data.get(position);
        holder.setData(positionData, position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    /** @noinspection InnerClassMayBeStatic*/
    public class listItemViewHolder extends RecyclerView.ViewHolder{
        listItemViewHolder(View itemView) {
            super(itemView);
        }
        public void setData(String text, int position) {
            TextView t = itemView.findViewById(R.id.listItemText);
            t.setText(text);
        }
    }
}
