package com.cardiogenerator.outputs;
/**
 * An interface used to set the standard for any outputs for future ease of use
 */
public interface OutputStrategy {
    void output(int patientId, long timestamp, String label, String data);
}
