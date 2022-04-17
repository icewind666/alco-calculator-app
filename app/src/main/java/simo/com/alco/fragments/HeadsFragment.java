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

import static simo.com.alco.R.id.textView;

public class HeadsFragment extends FragmentWithInit {
    String oldValueToEdit = "";
    TextView displayText; // main calculator field

    double initialProofValue = 0;
    double true_initialProofValue = 0;
    double desiredProofValue = 0;
    double initialVolume = 0;
    double temperatureCorrection = 0;

    boolean isTyping = false;
    boolean done = false;
    boolean useTempCorrection = false;
    boolean totalReset = true;
    int step = 0;
    ObjectAnimator objAnim;
    View mView;

    /**
     * Need this constructor to conform with android fragment api
     * to create empty fragment
     */
    public HeadsFragment() {
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
        mView = inflater.inflate(R.layout.fragment_heads, container, false);
        displayText = mView.findViewById(R.id.textView_heads);
        displayText.setTextSize(16);
        int number_ids[] = {
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

        int action_ids[] = { R.id.buttonClear_heads, R.id.buttonNext_heads };
        
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
        initialProofValue = 0;
        isTyping = false;
        step = 0;
        desiredProofValue = 0;
        true_initialProofValue = 0;
        initialVolume = 0;
        done = false;

        if (displayText != null) {
            displayText.setText(getText(R.string.heads_inputInitialProof));
            displayText.setTextSize(16);
        }

        ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);

        if (deleteBtn != null) {
            deleteBtn.setImageResource(R.drawable.delete);
        }

        totalReset = true;
        useTempCorrection = false;
        temperatureCorrection = 0;
        oldValueToEdit = "";
        if (objAnim != null) {
            objAnim.cancel();
        }


    }

    /**
     *
     * @param initialProof
     * @param displayText
     * @return
     */
    private boolean proofValidate(double initialProof, TextView displayText) {
        double valueToCheck = initialProof;

        if (useTempCorrection) {
            valueToCheck = true_initialProofValue;
        }

        ValidateResult vResult = Validator.validateInitialProof(valueToCheck);

        switch (vResult) {
            case INVALID_INITIAL_PROOF:
                displayText.setText(getString(R.string.unrealInitialProof, String.valueOf(true_initialProofValue)));
                initialProofValue = 0;
                true_initialProofValue = 0;
                return false;
            case UNREAL_INITIAL_PROOF:
                displayText.setText(getText(R.string.heads_noway));
                initialProofValue = 0;
                true_initialProofValue = 0;
                ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                deleteBtn.setImageResource(R.drawable.backspace);
                return false;
            case ZERO_PROOF:
                displayText.setText(getText(R.string.heads_water));
                initialProofValue = 0;
                true_initialProofValue = 0;
                return false;
            default:
                initialProofValue = initialProof;
                oldValueToEdit = ""; // this indicated that there was no error
                return true;
        }
    }


    /**
     * Validating initial proof value
     *
     * @param initialProof - value for validating
     */
    private void validateInitialProof(double initialProof) {
        TextView displayText = mView.findViewById(R.id.textView_heads);

        // storing value before validating
        // to be able to edit it after error
        oldValueToEdit = String.valueOf(initialProof);
        proofValidate(initialProof, displayText);
    }


    /**
     * Validating desired body proof value
     *
     * @param desiredProof
     */
    private void validateDesiredBodyProof(double desiredProof) {
        TextView displayText = mView.findViewById(R.id.textView_heads);
        oldValueToEdit = String.valueOf(desiredProof);

        if (desiredProof > Validator.MAX_POSSIBLE_PROOF) {
                String errorMsg = getString(R.string.heads_more_than_max,
                                            String.valueOf(Math.round(desiredProof)));
                displayText.setText(errorMsg);
        } else if (desiredProof == Validator.ZERO_PROOF) {
            displayText.setText(getString(R.string.heads_zero_proof,
                                String.valueOf(Math.round(desiredProof))));
        } else {
            desiredProofValue = desiredProof;
            oldValueToEdit = ""; // this indicated that there was no error
        }
    }

    //TODO: review this.
    boolean validateAbsoluteSpirit() {
        oldValueToEdit = String.valueOf(desiredProofValue);

        if ((desiredProofValue < initialProofValue) ||
            (desiredProofValue < true_initialProofValue)) {
            if (useTempCorrection) {
                displayText.setText(getString(R.string.heads_you_do_it_wrong_temp,
                        String.valueOf(Math.round(desiredProofValue)),
                        String.valueOf(Math.round(true_initialProofValue)),
                        String.valueOf(Math.round(temperatureCorrection))));
            }
            else {
                displayText.setText(getString(R.string.heads_you_do_it_wrong,
                        String.valueOf(Math.round(desiredProofValue)),
                        String.valueOf(Math.round(initialProofValue))));
            }
            //change picture to backspace
            ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
            deleteBtn.setImageResource(R.drawable.backspace);
            return false;

        }

        //oldValueToEdit = "";
        return true;
    }

    /**
     * Validates temperature correction
     *
     * @param temperature
     */
    private void validateTemp(double temperature) {
        displayText = mView.findViewById(R.id.textView_heads);
        oldValueToEdit = String.valueOf(temperature);

        ValidateResult validateResult = Validator.validateTemperature(temperature);

        switch (validateResult) {
            case TOO_HIGH_TEMPERATURE:
                displayText.setText(getText(R.string.tempTooHigh));
                break;

            case TOO_LOW_TEMPERATURE:
                displayText.setText(getText(R.string.tempTooLow));
                break;

            default:
                temperatureCorrection = temperature;
                // считаем крепость с поправкой
                true_initialProofValue = Math.round(1.0*initialProofValue + (20-temperature)*0.3);
                useTempCorrection = true;
                true_initialProofValue = (true_initialProofValue > 100) ? 100 : true_initialProofValue;
                oldValueToEdit = "";
        }
    }

    /**
     *
     */
    private void validateCurrentStep() {
        try {
            switch (step) {
                case 0:
                    validateInitialProof(Double.parseDouble(displayText.getText().toString()));
                    break;
                case 1:
                    // проверяем значение температуры
                    validateTemp(Double.parseDouble(displayText.getText().toString()));
                    // проверяем крепость с поправкой на температуру
                    ValidateResult validateInitialProofResult = Validator.validateInitialProof(true_initialProofValue);
                    switch (validateInitialProofResult) {
                        case INVALID_INITIAL_PROOF:
                            displayText.setText(getString(R.string.unrealInitialProof, String.valueOf(true_initialProofValue)));
                            initialProofValue = 0;
                            return;
                        default:
                            break;
                    }

                    break;
                case 2:
                    initialVolume = Double.parseDouble(displayText.getText().toString());
                    if (initialVolume <= 0) {
                        oldValueToEdit = String.valueOf(initialVolume);
                        displayText.setText(getString(R.string.heads_novolume));
                        return;
                    }
                    break;
                case 3:
                    desiredProofValue = Double.parseDouble(displayText.getText().toString());
                    validateDesiredBodyProof(desiredProofValue);
                    break;
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Ошибка! Нажмите сброс и попробуйте еще раз", Toast.LENGTH_SHORT).show();
            clearAll();
        }
    }

    public String s(int key) {
        return getString(key);
    }


    /**
     * Process taps on number buttons in Formula Fragment
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

                    if (totalReset) { // something is wrong) no "next" if need to reset flag is on
                        return;
                    }

                    switch (step) {
                        case 0:
                            deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                            deleteBtn.setImageResource(R.drawable.delete);
                            displayText.setText(getText(R.string.heads_inputTemp));
                            break;

                        case 1:
                            totalReset = true;
                            if(proofValidate(initialProofValue, displayText)) {
                                displayText.setText(getText(R.string.heads_inputInitialVolume));
                            }
                            deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                            deleteBtn.setImageResource(R.drawable.delete);
                            break;

                        case 2:
                            // Second screen showing
                            totalReset = true;
                            deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                            deleteBtn.setImageResource(R.drawable.delete);
                            displayText.setText(getText(R.string.heads_inputDesiredProof));
                            break;

                        case 3:
                            if(!validateAbsoluteSpirit()) {
                                totalReset = false;
                                step--;
                                isTyping=true;
                                break;
                            }
                            // Last screen showing.
                            // Should calculate the results
                            if(desiredProofValue == 0) {
                                // log error
                                totalReset = true;
                            }
                            else {
                                try {
                                    totalReset = true;
                                    double n = initialProofValue;
                                    double p = initialVolume ;
                                    double m = desiredProofValue;
                                    double t = true_initialProofValue;
                                    double absolutSpirit;
                                    double heads;
                                    double absolutSpiritBody;
                                    double body;
                                    double tails;

                                    if (!useTempCorrection) {
                                        t = initialProofValue;
                                    }

                                    if (t > 0) {
                                        n = t;
                                    }

                                    absolutSpirit = p * (n / 100);
                                    double headParts = ((MainActivity) getActivity()).getHeadsPercent();
                                    heads = absolutSpirit * headParts / 100;
                                    absolutSpiritBody = absolutSpirit * 0.7;
                                    body = absolutSpiritBody / (m / 100);
                                    tails = p - body - heads;
                                    String lastResult;

                                    if (useTempCorrection) {
                                        lastResult = getString(R.string.heads_resultTextWithCorrection,
                                                String.valueOf(Math.round(temperatureCorrection)),
                                                String.valueOf((int)Math.round(t)),
                                                String.valueOf((int)Math.round(m)),
                                                String.valueOf(Math.round(heads)),
                                                String.valueOf(Math.round(headParts)),
                                                String.valueOf(Math.round(body)),
                                                String.valueOf((int)Math.round(m)),
                                                String.valueOf(Math.round(tails)));
                                    } else {
                                        lastResult = getString(R.string.heads_resultText,
                                                String.valueOf((int)Math.round(t)),
                                                String.valueOf((int)Math.round(m)),
                                                String.valueOf(Math.round(heads)),
                                                String.valueOf(Math.round(headParts)),
                                                String.valueOf(Math.round(body)),
                                                String.valueOf((int)Math.round(m)),
                                                String.valueOf(Math.round(tails)));
                                    }

                                    displayText.setTextSize(14);
                                    displayText.setText(lastResult);
                                    totalReset = true;
                                    deleteBtn = mView.findViewById(R.id.buttonClear_heads);
                                    deleteBtn.setImageResource(R.drawable.delete);
                                    done = true;
                                    shareButton_heads();
                                } catch (Exception ex) {
                                    Toast.makeText(getActivity(), "Ошибка! Нажмите сброс и попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                    clearAll();
                                }
                            }
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
            if (currentText.equals(s(R.string.heads_inputInitialProof)) ||
                    currentText.equals(s(R.string.heads_inputDesiredProof)) ||
                    currentText.equals(s(R.string.heads_inputInitialVolume)) ||
                    currentText.equals(s(R.string.heads_inputTemp))) {
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
                        displayText.setText(R.string.heads_inputInitialProof);
                        break;
                    case 1:
                        displayText.setText(R.string.heads_inputTemp);
                        useTempCorrection = false;
                        break;
                    case 2:
                        displayText.setText(R.string.heads_inputInitialVolume);
                        break;
                    case 3:
                        displayText.setText(R.string.heads_inputDesiredProof);
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
            initialProofValue = 0;
            isTyping = false;
            step = 0;
            desiredProofValue = 0;
            true_initialProofValue = 0;
            initialVolume = 0;
            done = false;
            displayText.setText(getText(R.string.heads_inputInitialProof));
            displayText.setTextSize(16);
            ImageButton deleteBtn = mView.findViewById(R.id.buttonClear_heads);
            deleteBtn.setImageResource(R.drawable.delete);
            totalReset = true;
            useTempCorrection = false;
            temperatureCorrection = 0;
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