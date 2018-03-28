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
import net.simplifiedlearning.simplifiedcoding.R;

import java.util.List;

/**
 * Created by nhict on 3/24/18.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private List<Image> images;
    private Context context;

    public ImagesAdapter(Context context, List<Image> images){
        this.images = images;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView image_name, image_result;
        private ImageView image_url;
        public ViewHolder(View itemView) {
            super(itemView);
            image_url = itemView.findViewById(R.id.image_url);
            image_name = itemView.findViewById(R.id.image_name);
            image_result = itemView.findViewById(R.id.image_result);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = images.get(position);
        Picasso.get().load(image.getUrl()).into(holder.image_url);
        holder.image_name.setText(image.getName());
        holder.image_result.setText(String.format("Your Probable Risks: %s%%", String.valueOf(image.getResult())));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}