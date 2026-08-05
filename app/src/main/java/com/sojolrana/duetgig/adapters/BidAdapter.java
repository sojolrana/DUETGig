package com.sojolrana.duetgig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.models.Bid;

import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {

    private List<Bid> bidList;
    private OnBidActionListener listener;

    public interface OnBidActionListener {
        void onAccept(Bid bid);
        void onDecline(Bid bid);
    }

    public BidAdapter(List<Bid> bidList, OnBidActionListener listener) {
        this.bidList = bidList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bid, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        Bid bid = bidList.get(position);
        holder.bind(bid, listener);
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    static class BidViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, proposal;
        MaterialButton btnAccept, btnDecline;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bidderName);
            amount = itemView.findViewById(R.id.bidAmount);
            proposal = itemView.findViewById(R.id.bidProposal);
            btnAccept = itemView.findViewById(R.id.btnAcceptBid);
            btnDecline = itemView.findViewById(R.id.btnDeclineBid);
        }

        public void bind(Bid bid, OnBidActionListener listener) {
            name.setText(bid.getBidderName());
            amount.setText("$" + bid.getAmount());
            proposal.setText(bid.getProposal());

            btnAccept.setOnClickListener(v -> listener.onAccept(bid));
            btnDecline.setOnClickListener(v -> listener.onDecline(bid));
            
            // If bid is already accepted/rejected, hide buttons
            if (!"Pending".equals(bid.getStatus())) {
                btnAccept.setVisibility(View.GONE);
                btnDecline.setVisibility(View.GONE);
            } else {
                btnAccept.setVisibility(View.VISIBLE);
                btnDecline.setVisibility(View.VISIBLE);
            }
        }
    }
}