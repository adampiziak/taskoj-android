package com.adampiziak.bloktree.Adapters;

import android.app.Dialog;
import android.content.Context;
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

public class AdaRepeatDialog extends RecyclerView.Adapter<AdaRepeatDialog.ViewHolder>{

    private final List<String> priorityTitles;

    private Context mContext;
    Taskoj taskoj;

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

    public AdaRepeatDialog(Context context, Dialog dialog) {
        priorityTitles = Arrays.asList(context.getResources()
                .getStringArray(R.array.dialog_repeat));
        this.mContext = context;
        taskoj = (Taskoj) context.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemDialogPriority = inflater.inflate(R.layout.item_dialog_priority, parent, false);
        final ViewHolder vh =  new ViewHolder(itemDialogPriority);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskoj.getRepeatCase() == Taskoj.CASE_TASK_CREATOR)
                    ((ActTaskCreator) mContext).updateRepeat(vh.getAdapterPosition());
                else if (taskoj.getRepeatCase() == Taskoj.CASE_TASK_EDITOR)
                    ((ActTaskEditor) mContext).updateRepeat(vh.getAdapterPosition());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(priorityTitles.get(position));
        holder.mTitle.setWidth(400);
    }

    @Override
    public int getItemCount() {
        return priorityTitles.size();
    }
}
