package com.example.laixea1.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.adapter.RoadSignAdapter;
import com.example.laixea1.api.RoadSignApi;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.entity.RoadSign;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoadSignFragment extends Fragment {
    private static final String ARG_GROUP_ID = "group_id";
    private RecyclerView recyclerView;
    private RoadSignAdapter adapter;
    private List<RoadSign> roadSignList = new ArrayList<>();
    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;

    public static RoadSignFragment newInstance(int groupId) {
        RoadSignFragment fragment = new RoadSignFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_road_sign, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoadSignAdapter(roadSignList);
        recyclerView.setAdapter(adapter);

        // Thêm scroll listener để tải thêm dữ liệu khi cuộn đến cuối
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null &&
                        layoutManager.findLastCompletelyVisibleItemPosition() >= roadSignList.size() - 2) {
                    loadMoreRoadSigns();
                }
            }
        });

        loadRoadSigns(currentPage);
        return view;
    }

    private void loadRoadSigns(int page) {
        isLoading = true;
        int groupId = getArguments().getInt(ARG_GROUP_ID);
        Log.d("RoadSignFragment", "Loading page " + page + " for groupId " + groupId);
        RoadSignApi roadSignApi = RetrofitClient.createService(RoadSignApi.class);
        Call<List<RoadSign>> call = roadSignApi.getRoadSignsByGroupId(groupId, page, PAGE_SIZE);
        call.enqueue(new Callback<List<RoadSign>>() {
            @Override
            public void onResponse(Call<List<RoadSign>> call, Response<List<RoadSign>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    roadSignList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    currentPage++;
                } else {
                    Toast.makeText(getContext(), "Không tải được dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("RoadSignFragment", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RoadSign>> call, Throwable t) {
                isLoading = false;
                String message = t instanceof java.net.SocketTimeoutException ?
                        "Hết thời gian kết nối. Vui lòng thử lại." : "Lỗi: " + t.getMessage();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                Log.e("RoadSignFragment", "API Error: ", t);
            }
        });
    }

    private void loadMoreRoadSigns() {
        loadRoadSigns(currentPage);
    }
}