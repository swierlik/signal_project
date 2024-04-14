package com.cardiogenerator.generators;

import com.cardiogenerator.outputs.OutputStrategy;
/**
 * An interface for all the different data generators, created to set the standard for them.
 */
public interface PatientDataGenerator {
    void generate(int patientId, OutputStrategy outputStrategy);
}
