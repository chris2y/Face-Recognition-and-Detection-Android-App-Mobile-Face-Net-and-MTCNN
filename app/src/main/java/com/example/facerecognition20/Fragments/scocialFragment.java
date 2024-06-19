package com.example.facerecognition20.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.facerecognition20.Adapters.ShareAdapter;
import com.example.facerecognition20.LoginAndSignupActivity;
import com.example.facerecognition20.Model.ShareModel;
import com.example.facerecognition20.R;
import com.example.facerecognition20.Util.FirebaseUtil;
import com.example.facerecognition20.Util.ShareDiffCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class scocialFragment extends Fragment {

    private SharedPreferences sharedPreferences1;
    ImageView profilePicture;

    private List<ShareModel> mShareItems;
    private RecyclerView recyclerView;
    private ShareAdapter adapter;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar,progressBarRow;
    TextView noData;
    View view;
    TextView logOut;

    long lastTimestamp = 0;
    Parcelable recyclerViewState;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_scocial, container, false);



        logOut = view.findViewById(R.id.logoutButton);
        profilePicture = view.findViewById(R.id.profile_pic_layout);

        mShareItems = new ArrayList<>();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        noData = view.findViewById(R.id.noHomeItemsTextView);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressbarHome);
        progressBarRow = view.findViewById(R.id.progressBarLoadMore);
        //

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        adapter = new ShareAdapter(getContext(), mShareItems);
        recyclerView.setAdapter(adapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        recyclerView.addOnScrollListener(onScrollListener);
        swipeDownToRefresh();

        loadDataFromDatabase();

        fetchUserProfileData();

        sharedPreferences1 = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);


        logOut.setOnClickListener(view1 -> {

            SharedPreferences.Editor editor1 = sharedPreferences1.edit();

            editor1.clear();
            editor1.apply();
            editor1.putBoolean("isLoggedIn", false);

            startActivity(new Intent(getContext(), LoginAndSignupActivity.class));
            FirebaseUtil.logout();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void fetchUserProfileData() {
        DocumentReference userDocument = FirebaseUtil.getCurrentUserDocument();

        userDocument.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String profileImageUrl = document.getString("dataProfileImage");

                // Load the profile image using Glide
                if (profileImageUrl != null) {
                    Glide.with(this).load(profileImageUrl).into(profilePicture);
                }
            } else {
                // Document doesn't exist or is empty
            }
        }).addOnFailureListener(e -> {
            // Handle task failure
            // Log the error or show a message to the user
        });
    }

    private void swipeDownToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE);
            progressBarRow.setVisibility(View.GONE);
            loadDataFromDatabase();
        });
    }

    private void loadDataFromDatabase() {
        databaseReference.orderByChild("timestamp")
                .limitToLast(11).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ShareModel> updatedList = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                ShareModel homeModel = itemSnapshot.getValue(ShareModel.class);
                                homeModel.setKey(itemSnapshot.getKey());
                                updatedList.add(homeModel);
                            }
                            lastTimestamp =  updatedList.get(0).getTimestamp();
                            Collections.reverse(updatedList);
                            updatedList.remove(updatedList.size() - 1);
                            refreshTheAdapterAndView(updatedList);
                            isLoading = false;
                            isLastPage = false;
                        } else {
                            handleNoData();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }


    private void loadMoreData() {
        if (isLastPage || isLoading) {
            return;
        }

        isLoading = true;
        progressBarRow.setVisibility(View.VISIBLE);

        databaseReference.orderByChild("timestamp")
                .endAt(lastTimestamp)
                .limitToLast(11).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ShareModel> updatedList = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                ShareModel homeModel = itemSnapshot.getValue(ShareModel.class);
                                homeModel.setKey(itemSnapshot.getKey());
                                updatedList.add(homeModel);
                            }

                            if (updatedList.size() < 11 ) {
                                Toast.makeText(getContext(), "You reached last item", Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                                isLoading = false;
                                Collections.reverse(updatedList);
                                progressBarRow.setVisibility(View.GONE);
                                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                                refreshLoadMore(updatedList);
                                return;
                            }

                            lastTimestamp =  updatedList.get(0).getTimestamp();
                            Collections.reverse(updatedList);
                            updatedList.remove(updatedList.size() - 1);
                            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                            refreshLoadMore(updatedList);
                            isLoading = false;
                            progressBarRow.setVisibility(View.GONE);
                        } else {
                            handleNoData();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isLoading = false;
                        progressBarRow.setVisibility(View.GONE);
                    }
                });
    }


    private void refreshLoadMore(List<ShareModel> updatedList) {
        ShareDiffCallback diffCallback = new ShareDiffCallback(mShareItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // Append new data to the existing list
        mShareItems.addAll(updatedList);

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);


        diffResult.dispatchUpdatesTo(adapter);
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    private void refreshTheAdapterAndView(List<ShareModel> updatedList) {
        ShareDiffCallback diffCallback = new ShareDiffCallback(mShareItems, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        mShareItems.clear();
        mShareItems.addAll(updatedList);

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (mShareItems.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }

        diffResult.dispatchUpdatesTo(adapter);
    }

    private void handleNoData() {
        mShareItems.clear();
        noData.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

            // Check if we've reached the end of the list
            if (lastVisibleItem == totalItemCount - 1 && dy > 0) {
                // Load more data
                loadMoreData();
            }
        }
    };
}