package net.simplifiedlearning.simplifiedcoding.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import net.simplifiedlearning.simplifiedcoding.Models.ImagePreviewItem;
import net.simplifiedlearning.simplifiedcoding.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truongnm on 3/11/18.
 */

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ImagePreviewItem> data = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, List<ImagePreviewItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = row.findViewById(R.id.image_preview_item);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImagePreviewItem item = data.get(position);
        holder.image.setImageBitmap(item.getImage());
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}