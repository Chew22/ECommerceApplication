package com.example.ecommerceapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.Chew_SearchUserActivity;
import com.example.ecommerceapplication.adapters.Chew_RecentChatRecyclerAdapter;
import com.example.ecommerceapplication.models.ChatroomModel;
import com.example.ecommerceapplication.utils.Chew_FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Chew_ChatFragment extends Fragment {

    // Chat Page
    ImageButton searchButton;

    RecyclerView recycler_view_chat;
    Chew_RecentChatRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chew_fragment_chat, container, false);
        recycler_view_chat = view.findViewById(R.id.recycler_view_chat);
        setupRecyclerView();

        searchButton = view.findViewById(R.id.main_search_btn);
        searchButton.setOnClickListener(v -> {
            // Use getActivity() to get the activity context
            startActivity(new Intent(getActivity(), Chew_SearchUserActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {

        Query query = Chew_FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", Chew_FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new Chew_RecentChatRecyclerAdapter(options, getContext());
        recycler_view_chat.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view_chat.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }
}