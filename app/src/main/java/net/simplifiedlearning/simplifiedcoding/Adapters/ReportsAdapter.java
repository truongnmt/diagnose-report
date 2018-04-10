package net.simplifiedlearning.simplifiedcoding.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.simplifiedlearning.simplifiedcoding.Activities.ReportDetail;
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
        Report report = reports.get(position);
        holder.report_name.setText(report.getName());
        holder.report_created_at.setText(report.getCreated_at());
        holder.report_description.setText(report.getDescription());

        final View view = holder.itemView;
        final Report report_tmp = report;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext() , ReportDetail.class);
                intent.putExtra("id", report_tmp.getId());
                intent.putExtra("name", report_tmp.getName());
                intent.putExtra("description", report_tmp.getDescription());
                intent.putExtra("patient_name", report_tmp.getPatient_name());
                intent.putExtra("patient_age", report_tmp.getPatient_age());
                intent.putExtra("patient_height", report_tmp.getPatient_height());
                intent.putExtra("patient_weight", report_tmp.getPatient_weight());
                intent.putExtra("created_at", report_tmp.getCreated_at());
                intent.putExtra("general_result", report_tmp.getGeneral_result());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}