package com.alerts;

import java.util.List;

import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private double lastSaturation = 0.0;
    private long lastSaturationTimestamp = 0;
    private boolean lowSaturation = false;

    @Override
    public void checkAlert(PatientRecord record, List<Alert> alerts) {
        if (record.getRecordType().equals("Saturation")) {
            double saturation = record.getMeasurementValue();

            if (saturation < 92) {
                lowSaturation = true;
                alerts.add(new Alert(String.valueOf(record.getPatientId()), "Low Saturation Alert", record.getTimestamp()));
            } else {
                lowSaturation = false;
            }

            if (lastSaturationTimestamp != 0 && (record.getTimestamp() - lastSaturationTimestamp) <= 600000) {
                double saturationDrop = lastSaturation - saturation;
                if (saturationDrop >= 5) {
                    alerts.add(new Alert(String.valueOf(record.getPatientId()), "Rapid Saturation Drop Alert", record.getTimestamp()));
                }
            }

            lastSaturation = saturation;
            lastSaturationTimestamp = record.getTimestamp();
        }
    }
}

