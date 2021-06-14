package com.example.financial_management;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

public class SubmitFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    TextView monybox, accountbox, datebox, detailbox;
    Button submitbtn;
    RadioButton costrbtn;
    RadioButton incomerbtn;
    long time = 0;
    FirebaseUser currentFirebaseUser;
    DatabaseReference ref;

    public SubmitFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit, container, false);

        monybox = view.findViewById(R.id.moneyup);
        accountbox = view.findViewById(R.id.account);
        datebox = view.findViewById(R.id.dateup);
        detailbox = view.findViewById(R.id.detailup);
        costrbtn = view.findViewById(R.id.costrbtn);
        incomerbtn = view.findViewById(R.id.incomerbtn);
        submitbtn = view.findViewById(R.id.submitbtn);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detail = detailbox.getText().toString();
                boolean category = !costrbtn.isChecked();
                int money = Integer.parseInt(monybox.getText().toString());
                long time = System.currentTimeMillis();
                Transaction transaction = new Transaction(money, detail, category, time, time);
                FirebaseDatabase.getInstance().getReference().child("Transactions").child(currentFirebaseUser.getUid())
                        .child(Long.toString(time)).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "اطلاعات با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        EditText date = view.findViewById(R.id.dateup);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        SubmitFragment.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getActivity().getFragmentManager(), "Date");
            }
        });
        return view;
    }



    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        datebox.setText("" + year + "/" + month + "/" + day);
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setPersianDate(year, month, day);
        time = persianCalendar.getTimeInMillis();
    }

    public void setItems(DatabaseReference ref) {
        monybox.setText("ref.getDatabase().getReference().get()");
        accountbox.setText("ref.getDatabase().getReference().get()");
        datebox.setText("ref.getDatabase().getReference().get()");
        detailbox.setText("ref.getDatabase().getReference().get()");
        costrbtn.setText("ref.getDatabase().getReference().get()");
        incomerbtn.setText("ref.getDatabase().getReference().get()");
    }
}