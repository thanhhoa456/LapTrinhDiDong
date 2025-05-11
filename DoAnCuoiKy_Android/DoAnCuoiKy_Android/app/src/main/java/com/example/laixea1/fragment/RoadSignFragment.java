package com.example.laixea1.fragment;

import android.os.Bundle;
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
import com.example.laixea1.entity.RoadSign;
import com.example.laixea1.api.RoadSignApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoadSignFragment extends Fragment {
    private static final String ARG_GROUP_ID = "group_id";
    private RecyclerView recyclerView;
    private RoadSignAdapter adapter;

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
        adapter = new RoadSignAdapter(new java.util.ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadRoadSigns();
        return view;
    }

    private void loadRoadSigns() {
        int groupId = getArguments().getInt(ARG_GROUP_ID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.100.90:8080/api/") // Thay bằng URL API thực tế
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RoadSignApi roadSignApi = retrofit.create(RoadSignApi.class);
        Call<List<RoadSign>> call = roadSignApi.getRoadSignsByGroupId(groupId);
        call.enqueue(new Callback<List<RoadSign>>() {
            @Override
            public void onResponse(Call<List<RoadSign>> call, Response<List<RoadSign>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new RoadSignAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RoadSign>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}