package net.simplifiedlearning.simplifiedcoding.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.simplifiedlearning.simplifiedcoding.Models.Image;
import net.simplifiedlearning.simplifiedcoding.Models.Report;
import net.simplifiedlearning.simplifiedcoding.R;

import java.util.List;

/**
 * Created by truongnm on 3/24/18.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private List<Image> images;
    private Context context;

    public ImagesAdapter(Context context, List<Image> images){
        this.images = images;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView report_image_item, report_image_result;
        public ViewHolder(View itemView) {
            super(itemView);
            report_image_item = itemView.findViewById(R.id.report_image_item);
            report_image_result = itemView.findViewById(R.id.report_image_result);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_report_images, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = images.get(position);
        Picasso.get().load(image.getUrl()).into(holder.report_image_item);
        int result = image.getResult();
        if (result == -1) {
            // khong co du lieu
            holder.report_image_result.setImageResource(R.drawable.ic_help_black_24dp);
            holder.report_image_result.setColorFilter(context.getResources().getColor(R.color.warning)); // yellow
        }
        else {
            if (result == 1) {
                // khong co benh
                holder.report_image_result.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                holder.report_image_result.setColorFilter(context.getResources().getColor(R.color.safe)); // green
            } else {
                holder.report_image_result.setImageResource(R.drawable.ic_warning_black_24dp);
                holder.report_image_result.setColorFilter(context.getResources().getColor(R.color.danger)); // red
            }

//            TextView generalResult = findViewById(R.id.show_report_general_result);
//            if (Float.compare(general_result, 70) == 1)
//                generalResult.setTextColor(ContextCompat.getColor(this, R.color.safe));
//            else
//                generalResult.setTextColor(ContextCompat.getColor(this, R.color.danger));
//            generalResult.setText(String.valueOf(general_result + "%"));
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}