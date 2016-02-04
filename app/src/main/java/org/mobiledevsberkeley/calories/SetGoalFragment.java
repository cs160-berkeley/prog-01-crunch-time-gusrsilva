package org.mobiledevsberkeley.calories;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.Locale;


public class SetGoalFragment extends Fragment {
    private DecoView decoView;
    private int backIndex, series1Index;
    private SharedPreferences sharedPref;
    private String GOAL_KEY = "goal", AMOUNT_KEY = "amnt";
    private int currentGoal = 100, currentAmount = 0;
    private MaterialDialog md;
    private Button bSetGoal, bResetCount;
    private TextView textPercentage, textRemaining, textCurrentGoal;
    private SharedPreferences.Editor prefEditor;
    private boolean needToRedrawDeco = true, knowsWon = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_set_goal, container, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefEditor = sharedPref.edit();
        decoView = (DecoView) view.findViewById(R.id.decoView);
        textPercentage = (TextView)view.findViewById(R.id.textPercentage);
        textRemaining = (TextView)view.findViewById(R.id.cals_remaining);
        textCurrentGoal = (TextView)view.findViewById(R.id.current_goal);


        //Initialize Buttons
        bSetGoal = (Button)view.findViewById(R.id.new_goal);
        bSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGoalPressed();
            }
        });
        bResetCount = (Button)view.findViewById(R.id.reset_count);
        bResetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCountPressed();
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(needToRedrawDeco || currentAmount != sharedPref.getInt(AMOUNT_KEY, currentAmount))
            createDecoView();
            updateDecoView();
            needToRedrawDeco = false;
        }
        else {  }
    }

    private void displayMetGoal()
    {

    }

    private void createDecoView()
    {
        if(sharedPref.contains(GOAL_KEY))
        {
            currentGoal = sharedPref.getInt(GOAL_KEY, currentGoal);
        }
        textCurrentGoal.setText(String.format(Locale.ENGLISH, "Current Goal: %d Calories", currentGoal));
        decoView.deleteAll();
        //Initialize DecoView
        final SeriesItem backgroundSeries = new SeriesItem.Builder(Color.parseColor("#22E2E2E2"))
                .setRange(0, currentGoal, 0)
                .setLineWidth(20f)
                .build();
        backIndex = decoView.addSeries(backgroundSeries);

        final SeriesItem workoutSeries = new SeriesItem.Builder(Color.parseColor("#22DDDDDD"))
                .setRange(0, currentGoal, 0)
                .setLineWidth(20f)
                .build();

        series1Index = decoView.addSeries(workoutSeries);
        workoutSeries.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - backgroundSeries.getMinValue()) / (backgroundSeries.getMaxValue() - backgroundSeries.getMinValue()));
                percentFilled *= 100f;
                textPercentage.setText(String.format(Locale.ENGLISH, "%.0f%%", (percentFilled > 100? 100 : percentFilled)));
                int remaining = currentGoal - (int)currentPosition;
                textRemaining.setText(String.format(Locale.ENGLISH, "%d Calories Remaining", (remaining < 0? 0:remaining)));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        decoView.addEvent(new DecoEvent.Builder(currentGoal)
                .setIndex(backIndex)
                .setDuration(1000)
                .build());
    }

    private void updateDecoView()
    {
        if(sharedPref.contains(AMOUNT_KEY))
        {
            currentAmount = sharedPref.getInt(AMOUNT_KEY, currentAmount);
        }
        if(currentAmount >= currentGoal && currentGoal != 0)
            textCurrentGoal.setText("Goal complete. Nice Work!");

        decoView.addEvent(new DecoEvent.Builder(currentAmount)
                .setIndex(series1Index)
                .setDelay(1000)
                .setDuration(1500)
                .setColor(getResources().getColor(R.color.colorAccent))
                .build());
    }

    private void updateCurrentAmount(int n)
    {
        currentAmount = n;
        prefEditor.putInt(AMOUNT_KEY, n);
        prefEditor.apply();
        createDecoView();
        updateDecoView();
    }

    private void updateCurrentGoal(int n)
    {
        currentGoal = n;
        prefEditor.putInt(GOAL_KEY, n);
        prefEditor.apply();
        createDecoView();
        updateDecoView();
    }

    private void resetCountPressed()
    {
        md = new MaterialDialog
                .Builder(getContext())
                .title("Reset Count")
                .titleColor(Color.WHITE)
                .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .autoDismiss(false)
                .positiveText("Reset")
                .negativeText("Cancel")
                .negativeColor(Color.WHITE)
                .content("Are you sure you want to reset the progress you have made toward? This " +
                        "action cannot be undone.")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateCurrentAmount(0);
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .positiveColor(getResources().getColor(R.color.red))
                .show();
    }

    private void editGoalPressed()
    {
        View enterNameView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.dialog_edit_target, null);

        final EditText editText = (EditText) enterNameView.findViewById(R.id.enter_playlist_name);
        //Drawable editTextBg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit_text_bg);
        //editTextBg.setColorFilter(MainActivity.accentColor, PorterDuff.Mode.SRC_ATOP);
        //editText.setBackground(editTextBg);

        String currTitle = currentGoal + "";
        editText.setText(currTitle);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        editText.selectAll();

        md = new MaterialDialog
                .Builder(getContext())
                .title("Set Goal")
                .titleColor(getResources().getColor(R.color.colorAccent))
                .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                .autoDismiss(false)
                .positiveText("Save")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View cv = dialog.getCustomView();
                        if (cv != null) {
                            String str = ((EditText) cv.findViewById(R.id.enter_playlist_name)).getText().toString();
                            if (str.isEmpty())
                            {
                                Toast.makeText(getContext()
                                        , "Must enter an amount!"
                                        , Toast.LENGTH_SHORT)
                                        .show();
                            }

                            else
                            {
                                updateCurrentGoal(Integer.parseInt(str));
                                imm.toggleSoftInput(0,0);
                                md.dismiss();
                                createDecoView();
                                updateDecoView();
                            }
                        }
                    }
                })
                .positiveColor(getResources().getColor(R.color.colorAccent))
                .customView(enterNameView, false)
                .show();
    }
}
