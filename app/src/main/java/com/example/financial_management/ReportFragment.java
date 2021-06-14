package com.example.financial_management;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.financial_management.databinding.UpdateDialogBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class ReportFragment extends Fragment {
    ViewPager viewPager;
    HomeActivity homeActivity;
    RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String currentUser;
    private ProgressBar progressBar;

    public ReportFragment() {
    }

    PersianCalendar persianCalendar;
    LinearLayoutManager lm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleview);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lm = new LinearLayoutManager(getContext());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);

        recyclerView.setLayoutManager(lm);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Transactions").child(currentUser);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        homeActivity = (HomeActivity) getActivity();
        persianCalendar = new PersianCalendar();
        return view;
    }

    public void onStart() {
        super.onStart();
        databaseReference.orderByChild("time").limitToFirst(1);

        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Transaction>()
                .setQuery(databaseReference, Transaction.class).build();
        Collections.reverse(firebaseRecyclerOptions.getSnapshots());

        FirebaseRecyclerAdapter<Transaction, SubmitViewHolder> adapter = new FirebaseRecyclerAdapter<Transaction, SubmitViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final SubmitViewHolder SubmitViewHolder, int i, @NonNull Transaction transaction) {
                String ID = getRef(i).getKey();
                databaseReference.child(ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(View.GONE);
                        if (snapshot.hasChild("detail")) {
                            String detail = snapshot.child("detail").getValue().toString();
                            String money = snapshot.child("money").getValue().toString();
                            long time = Long.parseLong(snapshot.child("time").getValue().toString());
                            PersianCalendar calendar = new PersianCalendar();
                            calendar.setTimeInMillis(time);
                            SubmitViewHolder.time.setText("تاریخ ثبت: ");
                            SubmitViewHolder.time.append(calendar.getPersianShortDateTime());

                            SubmitViewHolder.detailtv.setText(detail);
                            SubmitViewHolder.moneytv.setText(money);
                            SubmitViewHolder.delbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getRef(i).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                                            Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            SubmitViewHolder.editbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openEditDialog(getRef(i), true);
                                }
                            });
                        }
                        SubmitViewHolder.itemView.setOnClickListener(view -> {
                            openEditDialog(getRef(i), false);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public SubmitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
                SubmitViewHolder viewHolder = new SubmitViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void openEditDialog(DatabaseReference ref, Boolean enable) {
        if (getActivity() == null || ref == null) return;

        UpdateDialogBinding dBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.update_dialog, null, false);
        dBinding.setEnable(enable);
        Dialog dialog = new Dialog(getContext(), R.style.mytheme);
        dialog.setContentView(dBinding.getRoot());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        databaseReference.child(ref.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("detail")) {
                    String detail = snapshot.child("detail").getValue().toString();
                    String money = snapshot.child("money").getValue().toString();
                    if (snapshot.child("category").getValue().toString().equals("false")) {
                        dBinding.costrbtn.setChecked(true);
                    } else {
                        dBinding.incomerbtn.setChecked(true);
                    }

                    long date = snapshot.child("date").getValue() == null ? 0 : (long) snapshot.child("date").getValue();
                    persianCalendar.setTimeInMillis(date);
                    dBinding.dateup.setText(persianCalendar.getPersianYear() + "/" + (persianCalendar.getPersianMonth() + 1) + "/" + persianCalendar.getPersianDay());
                    dBinding.detailup.setText(detail);
                    dBinding.moneyup.setText(money);

                    long time = snapshot.child("time").getValue() == null ? 0 : (long) snapshot.child("time").getValue();
                    persianCalendar.setTimeInMillis(time);
                    dBinding.time.setText("تاریخ ثبت تراکنش: " + persianCalendar.getPersianShortDateTime());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dBinding.submitbtn.setOnClickListener(v -> {
            Transaction transaction = new Transaction(Integer.parseInt(dBinding.moneyup.getText().toString()), dBinding.detailup.getText().toString(),
                    !dBinding.costrbtn.isChecked(), System.currentTimeMillis(), persianCalendar.getTimeInMillis());
            DatabaseReference dR = databaseReference.child(ref.getKey());
            dR.setValue(transaction);
            new Handler().postDelayed(() -> {
                Toast.makeText(getActivity(), "ویرایش با موفقیت انجام شد.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }, 1000);

        });

        dBinding.dateup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        (view, year, monthOfYear, dayOfMonth) -> {
                            dBinding.dateup.setText("" + year + "/" + monthOfYear + "/" + dayOfMonth);
                            persianCalendar.setPersianDate(year, monthOfYear, dayOfMonth);
                        },
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getActivity().getFragmentManager(), "Date");
            }
        });
    }


    public static class SubmitViewHolder extends RecyclerView.ViewHolder {

        TextView detailtv, moneytv, time;
        ImageView editbtn, delbtn;

        public SubmitViewHolder(@NonNull View itemView) {
            super(itemView);
            detailtv = itemView.findViewById(R.id.detailtv);
            time = itemView.findViewById(R.id.time);
            moneytv = itemView.findViewById(R.id.moneytv);
            editbtn = itemView.findViewById(R.id.edtbtn);
            delbtn = itemView.findViewById(R.id.delbtn);
        }
    }

    public void onSum() {
    }
}