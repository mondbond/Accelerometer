package com.example.mond.accelerometer.view.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import com.example.mond.accelerometer.Constants;
import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.DataUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class AccelerometerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String UID = "uid";

    private int mDayTimeExecuting;

    @BindView(R.id.btn_start)
    Button mStartButton;
    @BindView(R.id.et_interval)
    EditText mIntervalValue;
    @BindView(R.id.et_time)
    EditText mActionTimeValue;
    @BindView(R.id.btn_pick_time)
    Button mTimeExecutionSetterBtn;
    @BindView(R.id.tv_time_execution)
    TextView mTimeExecutionValue;
    @BindView(R.id.cb_time_execution)
    CheckBox mIsExecutingOnTime;

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

        return v;
    }

    @OnCheckedChanged(R.id.cb_time_execution)
    public void validateOnEmptyFields(CompoundButton buttonView, boolean isChecked) {
        if (mTimeExecutionValue.getText().toString().equals("")) {
            buttonView.setChecked(false);
            Toast.makeText(getActivity(), getResources().getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
        }
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

    @OnClick(R.id.btn_pick_time)
    public void showTimePicker() {
        if (mTimePickerDialog == null) {
            mTimePickerDialog = new TimePickerDialog(getActivity(),
                    AccelerometerDialogFragment.this, 0, 0, true);
        }
        mTimePickerDialog.show();
    }

    @OnClick(R.id.btn_start)
    public void initParametersAndStart() {
        saveConfiguration();
        mListener.onAccelerometerStart();

        dismiss();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (DataUtil.isOutOfTime(hourOfDay, minute)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.error_out_of_time), Toast.LENGTH_SHORT).show();
        } else {
            mDayTimeExecuting = DataUtil.getTimeOfDayInMl(hourOfDay, minute);
            mTimeExecutionValue.setText(String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
        }
    }

    private void saveConfiguration() {

        int interval = 0;
        int sessionTime = 0;

        if (!TextUtils.isEmpty(mIntervalValue.getText().toString())) {
            interval = Integer.parseInt(mIntervalValue.getText().toString());
        }

        if (!mActionTimeValue.getText().toString().equals("")) {
            sessionTime = Integer.parseInt(mActionTimeValue.getText().toString());
        } else {
//                    0 marked as infinite
            sessionTime = 0;
        }

        // TODO: 19.06.17 Should have a separated manager to handle shared preference interactions
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.ACCELEROMETER_PARAMETERS_SHARED_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.ACCELEROMETER_INTERVAL, interval);
        editor.putInt(Constants.ACCELEROMETER_SERVICE_WORK_TIME, sessionTime);
        editor.putBoolean(Constants.ACCELEROMETER_IS_START_ON_TIME, mIsExecutingOnTime.isChecked());
        editor.putInt(Constants.ACCELEROMETER_TIME_OF_START_IN_ML, mDayTimeExecuting);
        editor.putString(Constants.UID, mUID);
        editor.apply();
    }


    public interface AccelerometerDialogInteractionListener {

        void onAccelerometerStart();
    }
}
