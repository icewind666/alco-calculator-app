package simo.com.alco;

/**
 * Validates values in activities
 * Created by icewind on 12.08.2017.
 */

public class Validator {
    public static final int MAX_POSSIBLE_PROOF = 96;
    public static final int ZERO_PROOF = 0;
    static final int MAX_PROOF = 100;

    static final int MAX_POSSIBLE_TEMP = 78;
    static final int MIN_POSSIBLE_TEMP = 0;

    /**
     * Валидирует начальную крепость спирта
     */
    public static ValidateResult validateInitialProof(double initialProof) {
        if (initialProof > MAX_POSSIBLE_PROOF && initialProof <= MAX_PROOF) {
            return ValidateResult.UNREAL_INITIAL_PROOF;
        }
        else if (initialProof > MAX_PROOF) {
            return ValidateResult.INVALID_INITIAL_PROOF;

        }
        else if (initialProof == ZERO_PROOF) {
            return ValidateResult.ZERO_PROOF;
        }
        return ValidateResult.OK;
    }

    /**
     * Валидирует температуру спирта
     * @param temperature
     * @return
     */
    public static ValidateResult validateTemperature(double temperature) {
        if (temperature >= MAX_POSSIBLE_TEMP) {
            return ValidateResult.TOO_HIGH_TEMPERATURE;
        }
        if(temperature <= MIN_POSSIBLE_TEMP) {
                return ValidateResult.TOO_LOW_TEMPERATURE;
        }

        return ValidateResult.OK;
    }

}
