package com.alerts;

import java.util.List;

import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private double lastHeartRate = 0.0;
    private boolean irregularBeatDetected = false;

    @Override
    public void checkAlert(PatientRecord record, List<Alert> alerts) {
        if (record.getRecordType().equals("ECG")) {
            double heartRate = record.getMeasurementValue();
            if (heartRate < 50 || heartRate > 100) {
                alerts.add(new Alert(String.valueOf(record.getPatientId()), "Abnormal Heart Rate Alert", record.getTimestamp()));
            }

            if (lastHeartRate != 0.0) {
                double heartRateDifference = Math.abs(heartRate - lastHeartRate);
                if (heartRateDifference > 0.2 * lastHeartRate) {
                    irregularBeatDetected = true;
                } else {
                    irregularBeatDetected = false;
                }
            }

            if (irregularBeatDetected) {
                alerts.add(new Alert(String.valueOf(record.getPatientId()), "Irregular Beat Alert", record.getTimestamp()));
            }

            lastHeartRate = heartRate;
        }
    }
}

