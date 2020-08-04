package com.example.smarttrashapp.userui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttrashapp.Holder.HistoryHolder;
import com.example.smarttrashapp.Model.UserHome;
import com.example.smarttrashapp.Prevalent.Prevalent;
import com.example.smarttrashapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseReference userRef, checkHistory;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    TextView nohistory,totalbouns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        nohistory = findViewById(R.id.history_showNohistory);
        totalbouns=findViewById(R.id.history_Counter_tv);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineusers.getPhone()).child("History");
        checkHistory = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineusers.getPhone());
        checkHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalBouns = 0;
                if (!dataSnapshot.hasChild("History")) {
                    nohistory.setVisibility(View.VISIBLE);
                }
                else {

                    for (DataSnapshot snapScore : dataSnapshot.child("History").getChildren()) {
                        totalBouns += Integer.parseInt(String.valueOf(snapScore.child("totalbouns").getValue()));
                    }
                    totalbouns.setText(""+totalBouns);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView = findViewById(R.id.history_rececler_menu);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<UserHome> options =
                new FirebaseRecyclerOptions.Builder<UserHome>()
                        .setQuery(userRef.orderByChild("pid"), UserHome.class).build();
        FirebaseRecyclerAdapter<UserHome, HistoryHolder> adapter = new FirebaseRecyclerAdapter<UserHome, HistoryHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryHolder holder, int i, @NonNull final UserHome data) {

                holder.cansNumber.setText(data.getTotalbouns());
                holder.dateofoperation.setText("Date " + data.getDate() + " Time" + data.getTime());

            }

            @NonNull
            @Override
            public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holder, parent, false);
                HistoryHolder holder = new HistoryHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

}