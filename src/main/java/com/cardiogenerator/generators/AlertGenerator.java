//again changed package name

package com.cardiogenerator.generators;

import java.util.Random;

import com.cardiogenerator.outputs.OutputStrategy;
/**
 * A class which generates randomized alerts( ranzomized as in fi it will trigger or not)
 */

public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    //variable changed oto lowerCamelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        /**
         * A constructor used to initialize the alertStates array
         * We create an array bigger by 1 since we start our patientIds from 1, not 0
         * @param patientCount amount of patients
         * 
         */
        alertStates = new boolean[patientCount + 1];
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        /**
         * Has a chance ot generate or resolve an alert if one is already taking placefor a specific patient
         * then outputs it with our prefferred method
         * @param patientId Patient for which we want to try generate/resolve an alert
         * @param outputStrategy Prefered way of outputting our data
         */
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-Lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
