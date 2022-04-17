package simo.com.alco.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.ValidateResult;
import simo.com.alco.Validator;

public class SugarLitreFragment extends FragmentWithInit {
    String oldValueToEdit = "";
    TextView displayText; // main calculator field

    final int MAX_POSSIBLE_TOLERANCE = 25;
    final int MIN_POSSIBLE_TOLERANCE = 1;

    double toleranceValue = 0;
    boolean isTyping = false;
    boolean done = false;
    boolean totalReset = true;
    int step = 0;

    ObjectAnimator objAnim;
    View mView;

    /**
     * Need this constructor to conform with android fragment api
     * to create empty fragment
     */
    public SugarLitreFragment() {
    }

    /**
     * Factory method.
     * Useful when we will have arguments for creating fragment
     * @return fragment instance
     */
    public void initFragment(Object... args) {
        // pass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate content block with layout of current fragment
        mView = inflater.inflate(R.layout.fragment_sugar_litre, container, false);
        displayText = mView.findViewById(R.id.textView_heads);
        displayText.setTextSize(20);
        displayText.setText(R.string.inputInitialTolerance);

        int[] number_ids = {
                R.id.button0_heads,
                R.id.button1_heads,
                R.id.button2_heads,
                R.id.button3_heads,
                R.id.button4_heads,
                R.id.button5_heads,
                R.id.button6_heads,
                R.id.button7_heads,
                R.id.button8_heads,
                R.id.button9_heads
        };

        // set event listeners to number buttons
        for (int id: number_ids) {
            View btnView = mView.findViewById(id);
            btnView.setOnClickListener(this);
        }

        int[] action_ids = {R.id.buttonClear_heads, R.id.buttonNext_heads};

        for (int id: action_ids) {
            View btnView = mView.findViewById(id);
            btnView.setOnClickListener(this);
        }

        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        // we got click on button event -> transfer it to main activity to handle
        if (v.getId() == R.id.buttonClear_heads || v.getId() == R.id.buttonNext_heads) {
            ActionTapListener actionTapListener = new ActionTapListener((TextView) mView.findViewById(R.id.textView_heads));
            actionTapListener.onClick(v);
        } else {
            NumberTapListener numberTapListener = new NumberTapListener((TextView) mView.findViewById(R.id.textView_heads));
            numberTapListener.onClick(v);
        }

    }

    /**
     * Clear all states and progress
     */
    protected void clearAll() {
        toleranceValue = 0;
        isTyping = false;
        step = 0;
        done = false;
        totalReset = true;

        if (displayText != null) {
            displayText.setText(getText(R.string.inputInitialTolerance));
            displayText.setTextSize(20);
        }

        ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);

        if (deleteBtn != null) {
            deleteBtn.setImageResource(R.drawable.delete);
        }

        oldValueToEdit = "";
        if (objAnim != null) {
            objAnim.cancel();
        }
    }


    /**
     * Validating initial tolerance value
     *
     * @param toleranceValue - value for validating
     */
    private void validateTolerance(double toleranceValue) {
        TextView displayText = mView.findViewById(R.id.textView_heads);

        // storing value before validating
        // to be able to edit it after error
        oldValueToEdit = String.valueOf(toleranceValue);

        if (toleranceValue > MAX_POSSIBLE_TOLERANCE) {
            displayText.setText(R.string.tooHighTolerance);
        }
        else if (toleranceValue < MIN_POSSIBLE_TOLERANCE) {
            displayText.setText(R.string.tooLowTolerance);
        }
        else {
            this.toleranceValue = toleranceValue;
            oldValueToEdit = "";
        }
    }


    /**
     * Validating current step of calculator flow
     */
    private void validateCurrentStep() {
        try {
            if (step == 0) {
                validateTolerance(Double.parseDouble(displayText.getText().toString()));
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Ошибка! Нажмите сброс и попробуйте еще раз", Toast.LENGTH_SHORT).show();
            clearAll();
        }
    }

    /**
     * Returns string resource by id
     * @param key
     * @return
     */
    public String s(int key) {
        return getString(key);
    }


    /**
     * Process taps on number buttons in Fragment
     */
    private class NumberTapListener implements View.OnClickListener {
        TextView displayText;

        NumberTapListener(TextView displayArg) {
            super();
            if (displayArg == null) {
                throw new IllegalArgumentException("displayArg cannot be null");
            }
            displayText = displayArg;
        }

        @Override
        public void onClick(View v) {
            if (done) {
                // we are done. Skip all clicks on number buttons
                totalReset = true;
                oldValueToEdit = "";
                return;
            }

            switch (v.getId()) {
                case R.id.button0_heads:
                    userTappedNumber(0);
                    break;
                case R.id.button1_heads:
                    userTappedNumber(1);
                    break;
                case R.id.button2_heads:
                    userTappedNumber(2);
                    break;
                case R.id.button3_heads:
                    userTappedNumber(3);
                    break;
                case R.id.button4_heads:
                    userTappedNumber(4);
                    break;
                case R.id.button5_heads:
                    userTappedNumber(5);
                    break;
                case R.id.button6_heads:
                    userTappedNumber(6);
                    break;
                case R.id.button7_heads:
                    userTappedNumber(7);
                    break;
                case R.id.button8_heads:
                    userTappedNumber(8);
                    break;
                case R.id.button9_heads:
                    userTappedNumber(9);
                    break;
            }
        }


        /**
         * Adds a digit to the textview (concatenates actually)
         *
         * @param number - int number arg
         */
        @SuppressLint("SetTextI18n")
        private void typeOrAdd(int number) {
            if (isTyping) {
                if (!oldValueToEdit.isEmpty()) {
                    displayText.setText(String.valueOf(number));
                    oldValueToEdit = "";//used. clearing
                } else {
                    ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                    deleteBtn.setImageResource(R.drawable.backspace);
                    displayText.setText(displayText.getText() + String.valueOf(number));
                }
            } else {
                if (!oldValueToEdit.isEmpty()) {
                    displayText.setText(String.valueOf(number));
                    oldValueToEdit = "";//used. clearing
                } else {

                    displayText.setText(String.valueOf(number));
                    isTyping = true;

                    //change picture to backspace
                    ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                    deleteBtn.setImageResource(R.drawable.backspace);
                    totalReset = false;
                }
            }
        }

        /**
         * Handling user tap on number button.
         * Clears text field in case of error
         *
         * @param number - tapped number
         */
        private void userTappedNumber(int number) {
            // this is weird exception handling.
            try {
                Integer.parseInt(displayText.getText().toString());
            } catch (NumberFormatException ex) {
                // this happens when we show error to user and he start typing further
                displayText.setText("");
            }

            // process typed number
            typeOrAdd(number);
            // validate all after adding taped number
            validateCurrentStep();
        }


    }

    /**
     * Processes taps on action buttons
     */
    private class ActionTapListener implements View.OnClickListener {
        TextView displayText;

        ActionTapListener(TextView displayArg) {
            super();
            displayText = displayArg;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonClear_heads:
                    if (totalReset) {
                        clearAll();
                    } else {
                        clearLastTypedNumber();
                    }
                    break;

                case R.id.buttonNext_heads:
                    isTyping = false;
                    ImageButton deleteBtn;

                    if (totalReset) {
                        // something is wrong) no "next" if need to reset flag is on
                        return;
                    }

                    if (toleranceValue <= 0) {
                        Toast.makeText(getActivity(), "Ошибка! Нажмите сброс и попробуйте еще раз",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    switch (step) {
                        case 0:
                            deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                            deleteBtn.setImageResource(R.drawable.delete);
                            totalReset = true;

                            double syrup = (toleranceValue * 10) / 0.6;

                            double water = 1000 - syrup;
                            long sugar = Math.round((syrup/water)*1000);

                            String resultStr = "Вам понадобится %1$s гр. сахара на 1 литр воды, чтобы Ваши дрожжи толерантностью %2$s%% полностью выбродили.";
                            done = true;
                            displayText.setText(String.format(resultStr, sugar, (long)toleranceValue));
                            shareButton_heads();

                            break;
                        case 1:
                            totalReset = true;
                            done = true;
                            break;
                    }
                    step++;
                    break;
            }
        }

        /**
         * Removes last typed symbol in field
         */
        private void clearLastTypedNumber() {
            String currentText;

            if (!oldValueToEdit.isEmpty()) {
                currentText = String.valueOf(Math.round(Float.parseFloat(oldValueToEdit)));
                oldValueToEdit = "";
            } else {
                currentText = displayText.getText().toString();
            }

            // if text in displayText is one of our predefined strings ->
            // then clear button will clear all .
            if (currentText.equals(s(R.string.inputInitialTolerance))) {
                clearAll();
                return;
            }

            if (currentText.length() == 1) {
                // all cleared.
                //change picture to backspace
                ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                deleteBtn.setImageResource(R.drawable.delete);
                oldValueToEdit = "";

                switch (step) {
                    case 0:
                        displayText.setText(R.string.inputInitialTolerance);
                        break;
                    case 1:
                        displayText.setText(R.string.inputInitialTolerance);
                        break;
                }

            } else {
                String cutString = currentText.substring(0, currentText.length() - 1);
                ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                deleteBtn.setImageResource(R.drawable.backspace);
                displayText.setText(cutString);
                validateCurrentStep();
            }
        }


        /**
         * Clears all variables
         */
        private void clearAll() {
            isTyping = false;
            step = 0;
            done = false;
            toleranceValue = 0;
            displayText.setText(getText(R.string.inputInitialTolerance));
            displayText.setTextSize(20);
            ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
            deleteBtn.setImageResource(R.drawable.delete);
            totalReset = true;
            oldValueToEdit = "";

            ImageButton nextBtn = mView.findViewById(R.id.buttonNext_heads);
            nextBtn.setImageResource(R.drawable.forward);
            nextBtn.setOnClickListener(this);
            if (objAnim != null) {
                objAnim.cancel();
            }


        }

    }

    /**
     * Opens share dialog in formula.
     */
    public void shareButton_heads() {
        ImageButton nextBtn = mView.findViewById(R.id.buttonNext_heads);
        nextBtn.setImageResource(R.drawable.ic_share_black_24dp);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareLinkiOS = "https://apple.co/2Mu9gK1";
                String shareLinkAndroid = "http://bit.ly/2Oj4zTP";
                String shareMessageText = "Пользуюсь лучшим калькулятором самогонщика.\nЕсть и для iOS и для Android. Скачай, пригодится!\n\nДля Android: " + shareLinkAndroid + "\n\nДля iOS: " + shareLinkiOS;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessageText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                totalReset = true;
                done = true;
            }
        });
        pulseAnimation(nextBtn);
    }


    private void pulseAnimation(ImageButton btnObj) {
        objAnim = ObjectAnimator.ofPropertyValuesHolder(btnObj, PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        objAnim.setDuration(400);
        objAnim.setRepeatCount(ObjectAnimator.INFINITE);
        objAnim.setRepeatMode(ObjectAnimator.REVERSE);
        objAnim.setStartDelay(1000);
        objAnim.start();
    }

}