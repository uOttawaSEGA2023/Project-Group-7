package com.quantumSamurais.hams.doctor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;

import java.util.EnumSet;
import java.util.Iterator;

public class CheckableItemAdapter<E extends Enum<E>> extends RecyclerView.Adapter<CheckableItemAdapter<E>.checkBoxViewHolder> {

    private final EnumSet<E> choices;
    private Iterator<E> choicesIter;
    private final Context currentContext;

    private final boolean[]  isChecked;

    public CheckableItemAdapter(Context context, EnumSet<E> choices) {
        this.choices = choices;
        this.currentContext = context;
        this.choicesIter = choices.iterator();
        isChecked = new boolean[choices.size()];
    }
    @NonNull
    @Override
    public checkBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(currentContext).inflate(R.layout.checkable_list_item,parent,false);
       return new checkBoxViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull checkBoxViewHolder holder, int position) {
        choicesIter = choices.iterator();
        for (int i = 0; i < position; i++) {
            if(choicesIter.hasNext()) {
                 choicesIter.next();
            }
        }
        String nexItem = choicesIter.next().toString();
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

    public EnumSet<E> getCheckedOptions(Class<E> enumClass) {
        EnumSet<E> toReturn = EnumSet.noneOf(enumClass);

        choicesIter = choices.iterator();
        for (int i = 0; i < choices.size(); i++) {
            E choice = choicesIter.next();
            if(isChecked[i]) {
                toReturn.add(choice);
            }
        }

        return toReturn;
    }


    @Override
    public int getItemCount() {
        return choices.size();
    }

    /** @noinspection InnerClassMayBeStatic*/
    public class checkBoxViewHolder extends RecyclerView.ViewHolder{
        checkBoxViewHolder(View itemView) {
            super(itemView);
        }
        public void setData(String text, int position) {
            CheckBox c = itemView.findViewById(R.id.checkableItem);
            c.setText(text);
            c.setOnCheckedChangeListener((view,checked) -> isChecked[position] = checked);
        }
    }
}
