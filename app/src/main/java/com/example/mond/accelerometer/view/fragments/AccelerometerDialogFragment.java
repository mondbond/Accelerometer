package com.example.mond.accelerometer.view.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.interfaces.AuthenticationInteractionListener;
import com.example.mond.accelerometer.service.AccelerometerService;
import com.example.mond.accelerometer.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccelerometerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public static final String UID = "uid";

    private int mDayTimeExecuting;

    @BindView(R.id.activity_list_start_btn) Button mStartButton;
    @BindView(R.id.activity_list_interval_value) EditText mIntervalValue;
    @BindView(R.id.activity_list_time_value) EditText mActionTimeValue;
    @BindView(R.id.activity_list_time_execution_btn) Button mTimeExecutionSetterBtn;
    @BindView(R.id.activity_list_time_execution_value) TextView mTimeExecutionValue;
    @BindView(R.id.activity_list_is_time_execution) CheckBox mIsExecutingOnTime;

    private String mUID;
    private TimePickerDialog mTimePickerDialog;

    private AccelerometerDialogInteractionListener mListener;

    public static AccelerometerDialogFragment newInstance(String uID) {
        AccelerometerDialogFragment fragment = new AccelerometerDialogFragment();
        Bundle args = new Bundle();
        args.putString(UID, uID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUID = getArguments().getString(UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accelerometer_dialog, container, false);
        ButterKnife.bind(this, v);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mIsExecutingOnTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(mTimeExecutionValue.getText().toString().equals("")){
                buttonView.setChecked(false);
                Toast.makeText(getActivity(), getResources().getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
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

        initStartStopServiceButtons();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AccelerometerDialogInteractionListener) {
            mListener = (AccelerometerDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AccelerometerDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void initStartStopServiceButtons(){
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            int interval = 0;
            int sessionTime = 0;

            if(!TextUtils.isEmpty(mIntervalValue.getText().toString())){
                interval = Integer.parseInt(mIntervalValue.getText().toString());
            }

            if(!mActionTimeValue.getText().toString().equals("")){
                sessionTime = Integer.parseInt(mActionTimeValue.getText().toString());
            }else {
//                    0 marked as infinite
                sessionTime = 0;
            }

            mListener.onAccelerometerStart();

            Intent serviceIntent = new Intent(getActivity(), AccelerometerService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(AccelerometerService.ARG_INTERVAL, interval);
            bundle.putInt(AccelerometerService.ARG_SESSION_TIME, sessionTime);
            bundle.putInt(AccelerometerService.ARG_TIME_OF_START, mDayTimeExecuting);
            bundle.putBoolean(AccelerometerService.ARG_IS_DELAY_STARTING, mIsExecutingOnTime.isChecked());
            bundle.putString(AccelerometerService.UID, mUID);
            serviceIntent.putExtras(bundle);
            serviceIntent.setAction(AccelerometerService.ACCELEROMETER_SERVICE_START_ACTION);
            getActivity().startService(serviceIntent);

            dismiss();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(Util.isOutOfTime(hourOfDay, minute)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.error_out_of_time), Toast.LENGTH_SHORT).show();
        }else {
            mDayTimeExecuting = Util.getTimeOfDayInMl(hourOfDay, minute);
            mTimeExecutionValue.setText(String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
        }
    }

    public interface AccelerometerDialogInteractionListener{

        void onAccelerometerStart();
    }
}
