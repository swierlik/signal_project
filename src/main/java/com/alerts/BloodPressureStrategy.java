package com.alerts;

import java.util.List;

import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    private int systolicCounter = 0;
    private PatientRecord lastSystolicRecord = null;
    private int diastolicCounter = 0;
    private PatientRecord lastDiastolicRecord = null;
    private boolean lowSystolic = false;

    @Override
    public void checkAlert(PatientRecord record, List<Alert> alerts) {
        if (record.getRecordType().equals("SystolicPressure")) {
            handleSystolicPressure(record, alerts);
        } else if (record.getRecordType().equals("DiastolicPressure")) {
            handleDiastolicPressure(record, alerts);
        }
    }

    private void handleSystolicPressure(PatientRecord record, List<Alert> alerts) {
        if (lastSystolicRecord != null) {
            double systolicDifference = record.getMeasurementValue() - lastSystolicRecord.getMeasurementValue();
            if (Math.abs(systolicDifference) >= 10) {
                systolicCounter++;
            } else {
                systolicCounter = 0;
            }
            if (systolicCounter >= 2) {
                alerts.add(new Alert(String.valueOf(record.getPatientId()), "Systolic Trend Alert", record.getTimestamp()));
            }
        }

        if (record.getMeasurementValue() < 90) {
            lowSystolic = true;
        } else {
            lowSystolic = false;
        }

        lastSystolicRecord = record;

        if (record.getMeasurementValue() > 180 || record.getMeasurementValue() < 90) {
            alerts.add(new Alert(String.valueOf(record.getPatientId()), "Critical Threshold Alert", record.getTimestamp()));
        }
    }

    private void handleDiastolicPressure(PatientRecord record, List<Alert> alerts) {
        if (lastDiastolicRecord != null) {
            double diastolicDifference = record.getMeasurementValue() - lastDiastolicRecord.getMeasurementValue();
            if (Math.abs(diastolicDifference) >= 10) {
                diastolicCounter++;
            } else {
                diastolicCounter = 0;
            }
            if (diastolicCounter >= 2) {
                alerts.add(new Alert(String.valueOf(record.getPatientId()), "Diastolic Trend Alert", record.getTimestamp()));
            }
        }

        lastDiastolicRecord = record;

        if (record.getMeasurementValue() > 180 || record.getMeasurementValue() < 90) {
            alerts.add(new Alert(String.valueOf(record.getPatientId()), "Critical Threshold Alert", record.getTimestamp()));
        }
    }
}

