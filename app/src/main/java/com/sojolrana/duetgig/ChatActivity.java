package com.sojolrana.duetgig;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.adapters.MessageAdapter;
import com.sojolrana.duetgig.models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private TextInputEditText etMessage;
    private MaterialButton btnSend;
    private String chatId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chatId = getIntent().getStringExtra("chatId");
        String otherUserName = getIntent().getStringExtra("otherUserName");

        TextView toolbarTitle = findViewById(R.id.toolbarUserName);
        toolbarTitle.setText(otherUserName);

        recyclerView = findViewById(R.id.messagesRecyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        setupRecyclerView();
        listenForMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, mAuth.getCurrentUser().getUid());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void listenForMessages() {
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        messageList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Message message = document.toObject(Message.class);
                            messageList.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String content = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        if (content.isEmpty()) return;

        String messageId = UUID.randomUUID().toString();
        String senderId = mAuth.getCurrentUser().getUid();

        Message message = new Message(messageId, senderId, content, Timestamp.now());

        db.collection("chats").document(chatId).collection("messages")
                .document(messageId).set(message)
                .addOnSuccessListener(aVoid -> {
                    etMessage.setText("");
                    updateChatLastMessage(content);
                });
    }

    private void updateChatLastMessage(String content) {
        Map<String, Object> update = new HashMap<>();
        update.put("lastMessage", content);
        update.put("lastTimestamp", FieldValue.serverTimestamp());

        db.collection("chats").document(chatId).update(update);
    }
}