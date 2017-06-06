package com.example.mond.accelerometer.view.fragments;

import android.app.TimePickerDialog;
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
import com.example.mond.accelerometer.service.AccelerometerService;
import com.example.mond.accelerometer.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: 06/06/17 from this dialog you can only start the session, "Stop" button should be on the same level as button to show this dialog
public class AccelerometerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public static final String UID = "email";

    private int mDayTimeExecuting;

    @BindView(R.id.activity_list_start_btn) Button mStartButton;
    @BindView(R.id.activity_list_stop_btn) Button mStopButton;
    @BindView(R.id.activity_list_interval_value) EditText mIntervalValue;
    @BindView(R.id.activity_list_time_value) EditText mActionTimeValue;
    @BindView(R.id.activity_list_time_execution_btn) Button mTimeExecutionSetterBtn;
    @BindView(R.id.activity_list_time_execution_value) TextView mTimeExecutionValue;
    @BindView(R.id.activity_list_is_time_execution) CheckBox mIsExecutingOnTime;

    private String mUID;
    private TimePickerDialog mTimePickerDialog;

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

        initStartStopServiceButtons();

        return v;
    }

    public void initStartStopServiceButtons(){
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int interval = 0;
                int sessionTime = 0;

                // TODO: 06/06/17 DRY
                String sessionInterval = mIntervalValue.getText().toString();
                if(!TextUtils.isEmpty(sessionInterval)){
                    interval = Integer.parseInt(sessionInterval);
                }

                if(!mActionTimeValue.getText().toString().equals("")){
                    sessionTime = Integer.parseInt(mActionTimeValue.getText().toString());
                }else {
//                    0 marked as infinite
                    sessionTime = 0;
                }

                Intent serviceIntent = new Intent(getActivity(), AccelerometerService.class);
                Bundle bundle = new Bundle();
                bundle.putInt(AccelerometerService.INTERVAL, interval);
                bundle.putInt(AccelerometerService.SESSION_TIME, sessionTime);
                bundle.putInt(AccelerometerService.TIME_OF_START, mDayTimeExecuting);
                bundle.putBoolean(AccelerometerService.IS_DELAY_STARTING, mIsExecutingOnTime.isChecked());
                bundle.putString(AccelerometerService.UID, mUID);
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
}
