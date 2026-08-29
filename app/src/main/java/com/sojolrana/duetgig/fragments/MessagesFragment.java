package com.sojolrana.duetgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.ChatActivity;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.adapters.ChatListAdapter;
import com.sojolrana.duetgig.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private List<Chat> chatList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private View emptyStateLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        progressBar = view.findViewById(R.id.messagesProgressBar);
        emptyStateLayout = view.findViewById(R.id.messagesEmptyStateText);

        setupRecyclerView();
        loadChats();

        return view;
    }

    private void setupRecyclerView() {
        chatList = new ArrayList<>();
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        adapter = new ChatListAdapter(chatList, currentUserId, chat -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            intent.putExtra("otherUserName", chat.getOtherUserName(currentUserId));
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadChats() {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        db.collection("chats")
                .whereArrayContains("userIds", currentUserId)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (error != null || value == null) return;
                    
                    chatList.clear();
                    for (QueryDocumentSnapshot document : value) {
                        Chat chat = document.toObject(Chat.class);
                        chatList.add(chat);
                    }
                    
                    // Sort locally by lastTimestamp descending
                    chatList.sort((c1, c2) -> {
                        if (c1.getLastTimestamp() == null || c2.getLastTimestamp() == null) return 0;
                        return c2.getLastTimestamp().compareTo(c1.getLastTimestamp());
                    });

                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
    }

    private void updateEmptyState() {
        if (chatList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}