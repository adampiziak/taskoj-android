package com.adampiziak.bloktree.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adampiziak.bloktree.Activities.ActTaskCreator;
import com.adampiziak.bloktree.Activities.ActTaskEditor;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.R;

import java.util.Arrays;
import java.util.List;

public class AdaPriorityDialog extends RecyclerView.Adapter<AdaPriorityDialog.ViewHolder> {

    private final List<String> priorityTitles;
    private final List<String> priorityDescriptions;
    private final List<String> priorityColors;
    private Context mContext;
    private int target;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mDescription;
        LinearLayout mRoot;

        ViewHolder(View v) {
            super(v);
            mRoot = (LinearLayout) v.findViewById(R.id.item_dialog_priority_root);
            mTitle = (TextView) v.findViewById(R.id.item_dialog_priority_title);
            mDescription = (TextView) v.findViewById(R.id.item_dialog_priority_description);

        }
    }

    public AdaPriorityDialog(Context context) {
        priorityTitles = Arrays.asList(context.getResources()
                .getStringArray(R.array.dialog_priority_titles));
        priorityDescriptions = Arrays.asList(context.getResources()
                .getStringArray(R.array.dialog_priority_descriptions));
        priorityColors = Arrays.asList(context.getResources()
                .getStringArray(R.array.dialog_priority_colors));
        this.mContext = context;
        target = ((Taskoj) context.getApplicationContext()).getPriorityCase();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemDialogPriority = inflater.inflate(R.layout.item_dialog_priority, parent, false);
        return new ViewHolder(itemDialogPriority);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(priorityTitles.get(position));
        holder.mTitle.setWidth(400);
        holder.mTitle.setTextColor(Color.parseColor(priorityColors.get(position)));
        holder.mDescription.setText(priorityDescriptions.get(position));
        final int i = position;
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (target) {
                    case 0:
                        ((ActTaskCreator) mContext).updatePriority(i);
                        break;
                    case 1:
                        ((ActTaskEditor) mContext).updatePriority(i);
                        break;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return priorityTitles.size();
    }

}
