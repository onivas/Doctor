package com.savinoordine.doctor;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savinoordine.doctor.log.LogAdapter;
import com.savinoordine.doctor.log.LogHandler;

import java.util.ArrayList;
import java.util.List;

public class Doctor {

    private Activity mActivity;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private LogHandler mHandler;
    private LogHandler.PriorityType mLogPriority = LogHandler.PriorityType.ALL;

    private List<String> mDataset = new ArrayList<>();
    private boolean mScrollToEnd = true;

    public Doctor(Activity activity) {
        mActivity = activity;
        createView();
    }

    private void createView() {

        ViewGroup view = (ViewGroup) mActivity.findViewById(android.R.id.content);
        int childCount = view.getChildCount();

        mRecyclerView = new RecyclerView(mActivity);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LogAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayout layout = new LinearLayout(mActivity.getApplicationContext());
        layout.addView(mRecyclerView);
        layout.setGravity(Gravity.BOTTOM);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        view.addView(layout, childCount - 1, params);
    }

    /**
     * Set the priority that you need to filter the lines in the log
     * @param priorityType
     * @return
     */
    public Doctor setLogPriority(LogHandler.PriorityType priorityType) {
        mLogPriority = priorityType;
        return this;
    }

    /**
     * No log autoscrolling if set to false, default is true
     * @param value
     * @return
     */
    public Doctor setAutoScrolling(boolean value) {
        mScrollToEnd = value;
        return this;
    }

    public Doctor start() {

        mHandler = new LogHandler(mLogPriority) {

            public void onLineAdd(String line) {
                final String l = line;

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDataset.add(l);
                        mAdapter.notifyDataSetChanged();
                        if (mScrollToEnd) {
                            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                        }
                    }
                });
            }
        };

        return this;
    }

    public void stop() {
        if (mHandler != null) {
            mHandler.stop();
        }
    }
}
