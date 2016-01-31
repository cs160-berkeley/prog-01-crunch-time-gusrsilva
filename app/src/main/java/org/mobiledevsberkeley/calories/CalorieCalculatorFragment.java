package org.mobiledevsberkeley.calories;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


public class CalorieCalculatorFragment extends Fragment {

    private String TAG = "cs160";
    private int currentWorkoutValue, currentWorkoutPos, equivWorkoutPos;
    private Button saveButton;
    private TextView vUnits, vEquivUnits, vCalsBurned, vEquivCalsBurned;
    private ImageView vFlame;
    private EditText vNumber;
    private Spinner equivalentSpinner;
    private String[] workoutNames, unitNames = new String[2];
    private int[] workoutVals, workoutUnits;

    public CalorieCalculatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_calorie_calculator, container, false);

        workoutNames = getResources().getStringArray(R.array.workout_name_array);
        unitNames[0] = getResources().getString(R.string.reps);unitNames[1]=getResources().getString(R.string.minutes);
        workoutVals = getResources().getIntArray(R.array.workout_value_array);
        workoutUnits = getResources().getIntArray(R.array.workout_unit_array);
        currentWorkoutPos = 0; equivWorkoutPos = 1;
        currentWorkoutValue = workoutVals[currentWorkoutPos];

        vUnits = (TextView)view.findViewById(R.id.units_text);
        vEquivUnits = (TextView)view.findViewById(R.id.equiv_units_text);
        vFlame = (ImageView)view.findViewById(R.id.flame_image);
        vCalsBurned = (TextView)view.findViewById(R.id.calories_burned_amount);
        vEquivCalsBurned = (TextView)view.findViewById(R.id.equivalent_amount);
        vNumber = (EditText) view.findViewById(R.id.num_text);
        saveButton = (Button)view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        vNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateResults();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Initialize Spinner to choose workout type
        Spinner workoutSpinner = (Spinner) view.findViewById(R.id.workout_spinner);
        ArrayAdapter<CharSequence> workoutSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.workout_name_array, R.layout.my_spinner_item);
        workoutSpinnerAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        workoutSpinner.setAdapter(workoutSpinnerAdapter);
        workoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vUnits.setText(String.valueOf(unitNames[workoutUnits[position]]));
                currentWorkoutPos = position;
                currentWorkoutValue = workoutVals[position];
                updateResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Initialize Spinner to choose equivalent workout to compare
        equivalentSpinner = (Spinner) view.findViewById(R.id.equivalent_workout_spinner);
        ArrayAdapter<CharSequence> equivalentAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.workout_name_array, R.layout.my_spinner_item);
        equivalentAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        equivalentSpinner.setAdapter(equivalentAdapter);
        equivalentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                equivWorkoutPos = position;
                int equivalentAmount = getEquivAmount();
                vEquivCalsBurned.setText(String.valueOf(equivalentAmount));
                String units = unitNames[workoutUnits[position]] + " of";
                vEquivUnits.setText(units);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }


    private void updateResults()
    {
        int calsBurned = getCalsBurned();
        vCalsBurned.setText(String.valueOf(calsBurned));
        int color = getTrafficlightColor((float) calsBurned / 100.0);
        vFlame.setColorFilter(color);

        //Resize text if it's too big
        if(calsBurned >= Math.pow(10, 7))
            vCalsBurned.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        else if(calsBurned >= Math.pow(10, 4))
            vCalsBurned.setTextSize(TypedValue.COMPLEX_UNIT_SP, 70);
        else if(calsBurned >= Math.pow(10, 2))
            vCalsBurned.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        else
            vCalsBurned.setTextSize(TypedValue.COMPLEX_UNIT_SP, 150);

        //Set equivalent to next workout amount
        if(currentWorkoutPos == equivWorkoutPos) {
            equivWorkoutPos = (currentWorkoutPos + 1) % workoutNames.length;
            equivalentSpinner.setSelection(equivWorkoutPos, true);
        }
        int equivalentAmount = getEquivAmount();
        vEquivCalsBurned.setText(String.valueOf(equivalentAmount));
        String units = unitNames[workoutUnits[equivWorkoutPos]] + " of";
        vEquivUnits.setText(units);
    }

    private int getCalsBurned()
    {
        String inputText = vNumber.getText().toString();
        if(inputText.isEmpty())
            return 0;
        double inputAmount = Integer.parseInt(inputText);
        double calsBurned = (100 * inputAmount) / currentWorkoutValue;
        return (int)Math.ceil(calsBurned);
    }

    private int getEquivAmount()
    {
        double calsBurned = getCalsBurned();
        double equivalentWorkoutValue = workoutVals[equivWorkoutPos];
        double equivalentAmount = (calsBurned * equivalentWorkoutValue) / 100;
        return (int)Math.ceil(equivalentAmount);
    }

    private int getTrafficlightColor(double value){
        value = 1 - value;
        return android.graphics.Color.HSVToColor(new float[]{(float)value*40f,1f,1f});
    }

}
