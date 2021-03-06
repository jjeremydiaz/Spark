package com.example.tony.spark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window w = this.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);

            View view = new View(this);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGreenDark));
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("M/dd HH:mm");
        final String dateTime = df.format(c.getTime());
        String[] dateTimeArr = dateTime.split(" ");
        final String date = dateTimeArr[0];
        final String currTime = dateTimeArr[1];
        final SparkDataBase sdb = new SparkDataBase(this);
        Button today = (Button) findViewById(R.id.today);
        Button yesterday = (Button) findViewById(R.id.yesterday);
        Button newEntry = (Button) findViewById(R.id.new_entry);

        DateFormat dateFormat = new SimpleDateFormat("M/dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        final String yesterdayDateTime = dateFormat.format(cal.getTime());
        final String[] yesterdayDateTimeArr = yesterdayDateTime.split(" ");
        final String yesterdayDate = yesterdayDateTimeArr[0];
        final String yesterdayTime = yesterdayDateTimeArr[1];

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ArrayList<String>> timeAndEntries = sdb.getDiarySummary(date);
                final ArrayList<String> times = timeAndEntries.get(0);
                final ArrayList<String> entries = timeAndEntries.get(1);
                Bundle bdl = new Bundle();
                bdl.putString("date", date);
                DiaryPopUp dpu = new DiaryPopUp();
                DialogFragment dialog = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(final Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        final String date = savedInstanceState.getString("date");
//                        final SparkDataBase sdb = new SparkDataBase(getActivity());
//                        List<ArrayList<String>> diarySummaries = sdb.getDiarySummary(date);
//                        final List<String> times = diarySummaries.get(0);
//                        List<String> entries = diarySummaries.get(1);
                        String[] entriesArr = new String[entries.size()];
                        entriesArr = entries.toArray(entriesArr);
                        builder.setTitle(date)
                                .setItems(entriesArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        Intent myIntent = new Intent(getActivity(), DiaryEntryEditActivity.class);
                                        String time = times.get(which);
                                        ArrayList<String> details = sdb.getSingleDiary(date, time);
                                        String[] detailsArr = new String[details.size()];
                                        detailsArr = details.toArray(detailsArr);
                                        myIntent.putExtra("details", detailsArr);
                                        myIntent.putExtra("date", date);
                                        myIntent.putExtra("time", time);
                                        getActivity().startActivity(myIntent);
                                    }
                                });
                        return builder.create();
                    }
                };
                dialog.setCancelable(true);
                dialog.show(getFragmentManager(), "dialog");
            }
        });

        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ArrayList<String>> timeAndEntries = sdb.getDiarySummary(yesterdayDate);
                final ArrayList<String> times = timeAndEntries.get(0);
                final ArrayList<String> entries = timeAndEntries.get(1);
                Bundle bdl = new Bundle();
                bdl.putString("date", yesterdayDate);
                DiaryPopUp dpu = new DiaryPopUp();
                DialogFragment dialog = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(final Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        String[] entriesArr = new String[entries.size()];
                        entriesArr = entries.toArray(entriesArr);
                        builder.setTitle(yesterdayDate)
                                .setItems(entriesArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        Intent myIntent = new Intent(getActivity(), DiaryEntryEditActivity.class);
                                        String time = times.get(which);
                                        ArrayList<String> details = sdb.getSingleDiary(yesterdayDate, time);
                                        String[] detailsArr = new String[details.size()];
                                        detailsArr = details.toArray(detailsArr);
                                        myIntent.putExtra("details", detailsArr);
                                        myIntent.putExtra("date", yesterdayDate);
                                        myIntent.putExtra("time", time);
                                        getActivity().startActivity(myIntent);
                                    }
                                });
                        return builder.create();
                    }
                };
                dialog.setCancelable(true);
                dialog.show(getFragmentManager(), "dialog");
            }
        });

        newEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DiaryEntryEditActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("time", currTime);
                v.getContext().startActivity(intent);
            }
        });
    }
}
