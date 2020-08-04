package com.example.smarttrashapp.userui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttrashapp.Holder.HistoryHolder;
import com.example.smarttrashapp.Model.UserHome;
import com.example.smarttrashapp.Prevalent.Prevalent;
import com.example.smarttrashapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference userRef,checkHistory;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    TextView nohistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nohistory=findViewById(R.id.showNohistory);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineusers.getPhone()).child("History");
        checkHistory= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineusers.getPhone());
        checkHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("History")){
                    Toast.makeText(HomeActivity.this, "No History", Toast.LENGTH_SHORT).show();
                    nohistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Get New Bouns", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, ScannerActivity.class);
                startActivity(intent);

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTV = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);
        userNameTV.setText(Prevalent.currentonlineusers.getName());
        Picasso.get().load(Prevalent.currentonlineusers.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        recyclerView = findViewById(R.id.rececler_menu);
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
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                        intent.putExtra("pid", data.getPid());
                        startActivity(intent);
                    }
                });

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_scan_history) {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}