package com.sojolrana.duetgig.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message, currentUserId);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        MaterialCardView card;
        TextView content;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.messageContainer);
            card = itemView.findViewById(R.id.messageCard);
            content = itemView.findViewById(R.id.messageContent);
        }

        public void bind(Message message, String currentUserId) {
            content.setText(message.getContent());

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
            if (message.getSenderId().equals(currentUserId)) {
                // Sent by current user (Right side)
                container.setGravity(Gravity.END);
                card.setCardBackgroundColor(itemView.getContext().getColor(R.color.primary));
                content.setTextColor(itemView.getContext().getColor(R.color.white));
                params.setMargins(100, 0, 0, 0); // Indent from left
            } else {
                // Received (Left side)
                container.setGravity(Gravity.START);
                card.setCardBackgroundColor(itemView.getContext().getColor(R.color.light_grey));
                content.setTextColor(itemView.getContext().getColor(R.color.black));
                params.setMargins(0, 0, 100, 0); // Indent from right
            }
            card.setLayoutParams(params);
        }
    }
}