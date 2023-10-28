package com.quantumSamurais.hams.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.Specialties;

import java.util.ArrayList;
import java.util.Iterator;

public class SpecialtyItemAdapter extends RecyclerView.Adapter<SpecialtyItemAdapter.SpecialtyViewHolder> {
    private final ArrayList<Specialties> specialties;
    private Iterator<Specialties> specialtiesIterator;
    private final Context currentContext;

    public SpecialtyItemAdapter(Context context, ArrayList<Specialties> specialties) {
        this.specialties = specialties;
        this.currentContext = context;
        this.specialtiesIterator = specialties.iterator();
    }
    @NonNull
    @Override
    public SpecialtyItemAdapter.SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.specialties_show_more_item,parent,false);
        return new SpecialtyItemAdapter.SpecialtyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyItemAdapter.SpecialtyViewHolder holder, int position) {
        String nexItem = specialties.get(position).toString();
        StringBuilder text = new StringBuilder();
        String[] splitString = nexItem.split("_");
        for (int i = 0; i < splitString.length; i++) {
            String s = splitString[i];
            text.append(s.substring(0, 1).toUpperCase());
            text.append(s.substring(1).toLowerCase());
            if(i != splitString.length -1)
                text.append(' ');
        }
        holder.setData(text.toString(), position);
    }


    @Override
    public int getItemCount() {
        return specialties.size();
    }

    /** @noinspection InnerClassMayBeStatic*/
    public class SpecialtyViewHolder extends RecyclerView.ViewHolder{
        TextView specialty;
        SpecialtyViewHolder(View itemView) {
            super(itemView);
        }
        public void setData(String text, int position) {
            specialty = itemView.findViewById(R.id.someSpecialty);
            specialty.setText(text);
        }
    }
}
