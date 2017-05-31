package com.example.mond.accelerometer.view.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.service.AccelerometerService;
import com.example.mond.accelerometer.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccelerometerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public static final String EMAIL = "email";

    private OnFragmentInteractionListener mListener;
    private int mDayTimeExecuting;

    @BindView(R.id.activity_list_start_btn) Button mStartButton;
    @BindView(R.id.activity_list_stop_btn) Button mStopButton;

    @BindView(R.id.activity_list_interval_value) EditText mIntervalValue;
    @BindView(R.id.activity_list_time_value) EditText mActionTimeValue;
    @BindView(R.id.activity_list_time_execution_btn) Button mTimeExecutionSetterBtn;
    @BindView(R.id.activity_list_time_execution_value) TextView mTimeExecutionValue;
    @BindView(R.id.activity_list_is_time_execution) CheckBox mIsExecutingOnTime;

    private String mEmail;
    private TimePickerDialog mTimePickerDialog;

    public static AccelerometerDialogFragment newInstance(String email) {
        AccelerometerDialogFragment fragment = new AccelerometerDialogFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEmail = getArguments().getString(EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accelerometer_dialog, container, false);
        ButterKnife.bind(this, v);
        mIsExecutingOnTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(mTimeExecutionValue.getText().toString().equals("")){
                buttonView.setChecked(false);
                // TODO: 30/05/17 HARDCODE
                Toast.makeText(getActivity(), getResources().getString(R.string.empty_fields_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTimeExecutionSetterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mTimePickerDialog == null){
                mTimePickerDialog = new TimePickerDialog(getActivity(),
                        AccelerometerDialogFragment.this, 0, 0, true);
            }
             mTimePickerDialog.show();
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            int interval = 0;
            int sessionTime = 0;

            if(!mIntervalValue.getText().toString().equals("")){
                interval = Integer.parseInt(mIntervalValue.getText().toString());
            }

            if(!mActionTimeValue.getText().toString().equals("")){
                sessionTime = Integer.parseInt(mActionTimeValue.getText().toString());
            }

            Intent serviceIntent = new Intent(getActivity(), AccelerometerService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(AccelerometerService.INTERVAL, interval);
            bundle.putInt(AccelerometerService.SESSION_TIME, sessionTime);
            bundle.putInt(AccelerometerService.TIME_OF_START, mDayTimeExecuting);
            bundle.putBoolean(AccelerometerService.IS_DELAY_STARTING, mIsExecutingOnTime.isChecked());
            bundle.putString(AccelerometerService.UID, mEmail);
            serviceIntent.putExtras(bundle);
            serviceIntent.setAction(AccelerometerService.START_ACTION);
            getActivity().startService(serviceIntent);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccelerometerService.BROADCAST_ACTION);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(Util.isOutOfTime(hourOfDay, minute)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.out_of_time_error), Toast.LENGTH_SHORT).show();
        }else {
            mDayTimeExecuting = Util.getTimeOfDayInMl(hourOfDay, minute);
            mTimeExecutionValue.setText(String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
