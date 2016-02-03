package org.mobiledevsberkeley.calories;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;


public class SetTargetFragment extends Fragment {
    private DecoView decoView;
    private int backIndex, series1Index;
    private SharedPreferences sharedPref;
    private String GOAL_KEY = "goal";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_set_target, container, false);
        decoView = (DecoView) view.findViewById(R.id.decoView);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(!sharedPref.contains(GOAL_KEY))
        {
            Toast.makeText(getContext(), "uh-oh it looks like you haven't set a goal yet!", Toast.LENGTH_SHORT).show();
        }


        final SeriesItem backgroundSeries = new SeriesItem.Builder(Color.parseColor("#22E2E2E2"))
                .setRange(0, 100, 0)
                .setLineWidth(20f)
                .build();

        backIndex = decoView.addSeries(backgroundSeries);

        final SeriesItem workoutSeries = new SeriesItem.Builder(Color.parseColor("#22DDDDDD"))
                .setRange(0, 100, 0)
                .setLineWidth(20f)
                .build();

        series1Index = decoView.addSeries(workoutSeries);

        final TextView textPercentage = (TextView) view.findViewById(R.id.textPercentage);
        workoutSeries.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - workoutSeries.getMinValue()) / (workoutSeries.getMaxValue() - workoutSeries.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            decoView.addEvent(new DecoEvent.Builder(100)
                    .setIndex(backIndex)
                    .setDuration(1500)
                    .build());
            updateDecoView(50);
        }
        else {  }
    }

    public void updateDecoView(int n)
    {
        decoView.addEvent(new DecoEvent.Builder(n)
                .setIndex(series1Index)
                .setDelay(1500)
                .setColor(getResources().getColor(R.color.colorAccent))
                .build());
    }
}
