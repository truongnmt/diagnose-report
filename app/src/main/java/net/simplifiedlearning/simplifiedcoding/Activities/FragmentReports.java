package net.simplifiedlearning.simplifiedcoding.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.simplifiedlearning.simplifiedcoding.Adapters.ReportsAdapter;
import net.simplifiedlearning.simplifiedcoding.Models.Report;
import net.simplifiedlearning.simplifiedcoding.Models.User;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;

/**
 * Created by truongnm on 3/31/18.
 */

public class FragmentReports extends Fragment {
    private List<Report> reports = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReportsAdapter reportsAdapter;
    private SwipeRefreshLayout swipeContainer;
    private User user;

    public FragmentReports() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final FragmentActivity fragmentActivity = getActivity();
        swipeContainer = getView().findViewById(R.id.swipe_refresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setRefreshing(false);

        recyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);

        recyclerView.setLayoutManager(linearLayoutManager);
        reportsAdapter = new ReportsAdapter(fragmentActivity, reports);
        recyclerView.setAdapter(reportsAdapter);

//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(reportsAdapter);
//        mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(recyclerView);

        user = SharedPrefManager.getInstance(fragmentActivity).getUser();

        getData();

//        final ImageButton editButton = getView().findViewById(R.id.editButton);
//        editButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.editButton) {
//                    isEditClicked = !isEditClicked;
//                    editButton.setBackgroundResource(isEditClicked ? R.color.mat100 : R.color.white);
//                    editButton.setColorFilter(isEditClicked ?
//                            Color.argb(255, 117, 117, 117) : R.color.mat900);
//                    reportsAdapter.editMode(isEditClicked);
//                }
//            }
//        });

        final ImageButton refreshButton = getView().findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.refreshButton) {
                    swipeContainer.setEnabled(true);
                    swipeContainer.setRefreshing(true);
                    getData();
                }
            }
        });
    }

    private void getData(){
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Report>> call = apiInterface.getReports(user.getId());
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, retrofit2.Response<List<Report>> response) {
                if(response.isSuccessful()){
                    reports.clear();
                    reports.addAll(response.body());
                    Log.e(TAG, reports.toString());
                    reportsAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, response.message());
                }
                swipeContainer.setEnabled(false);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                swipeContainer.setEnabled(false);
                swipeContainer.setRefreshing(false);
            }
        });
    }

//    @Override
//    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
//        mItemTouchHelper.startDrag(viewHolder);
//
//        Map<Integer,Integer> itemPosition = new HashMap<>();
//        List<Image> list = reportsAdapter.getList();
//        for(int i=0; i<list.size(); i++) {
//            itemPosition.put(i, list.get(i).getId());
//        }
//    }


}