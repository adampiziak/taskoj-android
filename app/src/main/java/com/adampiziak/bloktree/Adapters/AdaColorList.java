package com.adampiziak.bloktree.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.adampiziak.bloktree.Activities.ActProjectCreator;
import com.adampiziak.bloktree.Activities.ActProjectEditor;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdaColorList extends RecyclerView.Adapter<AdaColorList.ColorHolder> {
    List<String> colors = new ArrayList<>();
    List<String> colorNames = new ArrayList<>();
    int selectedColor = 0;
    int projectCase = 0;
    Window window;
    Context context;


    public AdaColorList(Context context) {
        projectCase = ((Taskoj) context.getApplicationContext()).getProjectCase();
        colors = Arrays.asList(context.getResources()
                .getStringArray(R.array.projectColors));
        colorNames = Arrays.asList(context.getResources()
                .getStringArray(R.array.projectColorNames));
        this.context = context;
        window = (projectCase == 0) ? ((ActProjectCreator) context).getWindow() : ((ActProjectEditor) context).getWindow();


    }

    class ColorHolder extends RecyclerView.ViewHolder {
        TextView colorText;

        public ColorHolder(View v) {
            super(v);
            colorText = (TextView) v.findViewById(R.id.item_color_text);
        }
    }

    @Override
    public ColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View colorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        final ColorHolder colorHolder = new ColorHolder(colorView);
        colorHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = colorHolder.getAdapterPosition();
                if (((Taskoj) context.getApplicationContext()).getProjectCase() == 0)
                    ((ActProjectCreator) context).setColor(colors.get(selectedColor));
                else
                    ((ActProjectEditor) context).setColor(colors.get(selectedColor));
                //window.setStatusBarColor(Color.parseColor(colors.get(selectedColor)));
                notifyDataSetChanged();
            }
        });
        return colorHolder;
    }

    @Override
    public void onBindViewHolder(ColorHolder holder, int position) {
        int color = Color.parseColor(colors.get(position));
        holder.colorText.setTextColor((selectedColor == position) ? Color.WHITE : color);
        holder.colorText.setText(colorNames.get(position));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(20);
        drawable.setColor((selectedColor == position) ? color : Color.WHITE);
        drawable.setStroke(5, Color.parseColor(colors.get(position)));
        holder.colorText.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public void setColorPosition(String color) {
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).equals(color)) {
                selectedColor = i;
                notifyDataSetChanged();
            }

        }
    }


}











