package com.example.videocallapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationFragment extends Fragment {

    private RecyclerView notification_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification,container,false);

        notification_list = view.findViewById(R.id.notification_list);
        notification_list.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        TextView userNameTxt, userBioTxt;
        ImageView userProfile;
        Button acceptBtn, declineBtn;
        RelativeLayout cardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_notification);
            userBioTxt = itemView.findViewById(R.id.bio_notification);
            userProfile = itemView.findViewById(R.id.image_notification);
            acceptBtn = itemView.findViewById(R.id.accept_btn);
            declineBtn = itemView.findViewById(R.id.decline_btn);
            cardView = itemView.findViewById(R.id.rl_design);
        }
    }
}

