package net.simplifiedlearning.simplifiedcoding.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.simplifiedlearning.simplifiedcoding.Models.Report;
import net.simplifiedlearning.simplifiedcoding.R;
import java.util.List;

/**
 * Created by truongnm on 3/24/18.
 */

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private List<Report> reports;
    private Context context;

    public ReportsAdapter(Context context, List<Report> reports){
        this.reports = reports;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView report_name, report_description, report_created_at;
        public ViewHolder(View itemView) {
            super(itemView);
            report_name = itemView.findViewById(R.id.report_name);
            report_description = itemView.findViewById(R.id.report_description);
            report_created_at = itemView.findViewById(R.id.report_created_at);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        Image image = images.get(position);
//        Picasso.get().load(image.getUrl()).into(holder.image_url);
        holder.image_name.setText(image.getName());
        holder.image_result.setText(String.format("Your Probable Risks: %s%%", String.valueOf(image.getResult())));
//        if(activate) {
//            holder.handleView.setVisibility(View.VISIBLE);
//            holder.handleView.animate()
//                    .translationX(0)
//                    .alpha(1.0f)
//                    .setDuration(100)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                        }
//                    });
//        } else {
//            holder.handleView.animate()
//                    .translationX(holder.handleView.getWidth())
//                    .alpha(0.0f)
//                    .setDuration(100)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            holder.handleView.setVisibility(View.INVISIBLE);
//                        }
//                    });
//        }
//
//        // Start a drag whenever the handle view is touched
//        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
//                    mDragStartListener.onStartDrag(holder);
//                }
//                return false;
//            }
//        });
    }

//    @Override
//    public void onItemDismiss(int position) {
//        images.remove(position);
//        notifyItemRemoved(position);
//    }
//
//    @Override
//    public boolean onItemMove(int fromPosition, int toPosition) {
//        Collections.swap(images, fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
//        return true;
//    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

//    public List<Image> getList() {
//        return this.images;
//    }


//    /**
//     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
//     * "handle" view that initiates a drag event when touched.
//     */
//    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
//            ItemTouchHelperViewHolder {
//
//        public final TextView image_name, image_result;
//        public final ImageView image_url;
//        public final ImageView handleView;
//
//        public ItemViewHolder(View itemView) {
//            super(itemView);
//            image_url = itemView.findViewById(R.id.image_url);
//            image_name = itemView.findViewById(R.id.image_name);
//            image_result = itemView.findViewById(R.id.image_result);
//            handleView = itemView.findViewById(R.id.handle);
//        }
//
//        @Override
//        public void onItemSelected() {
//            itemView.setBackgroundColor(Color.LTGRAY);
//        }
//
//        @Override
//        public void onItemClear() {
//            itemView.setBackgroundColor(0);
//        }
//    }

}