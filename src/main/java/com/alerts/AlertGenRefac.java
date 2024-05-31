package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class AlertGenRefac {
    List<Alert> triggeredAlerts= new ArrayList<>();
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(17128316L, 171283163844400L);
        
        AlertFactory bpAlertFactory = new BloodPressureAlertFactory();
        AlertFactory boAlertFactory = new BloodOxygenAlertFactory();
        AlertFactory ecgAlertFactory = new ECGAlertFactory();
    
        int bpSystCounter = 0;
        PatientRecord bpSystLastRecord = null;
        int bpDiastCounter = 0;
        PatientRecord bpDiastLastRecord = null;
    
        double lastSaturation = 0.0;
        long lastSaturationTimestamp = 0;
        boolean lowSystolic = false;
        boolean lowSaturation = false;
    
        double lastHeartRate = 0.0;
        boolean irregularBeatDetected = false;
    
        for (PatientRecord record : records) {
            String recordType = record.getRecordType();
            double measurementValue = record.getMeasurementValue();
            long timestamp = record.getTimestamp();
            String patientId = String.valueOf(record.getPatientId());
    
            switch (recordType) {
                case "SystolicPressure":
                    if (bpSystLastRecord != null) {
                        double difference = measurementValue - bpSystLastRecord.getMeasurementValue();
                        if (Math.abs(difference) >= 10) {
                            bpSystCounter++;
                        } else {
                            bpSystCounter = 0;
                        }
                        if (bpSystCounter >= 2) {
                            triggerAlert(bpAlertFactory.createAlert(patientId, "Systolic Trend Alert", timestamp));
                        }
                    }
                    lowSystolic = measurementValue < 90;
                    bpSystLastRecord = record;
                    break;
    
                case "DiastolicPressure":
                    if (bpDiastLastRecord != null) {
                        double difference = measurementValue - bpDiastLastRecord.getMeasurementValue();
                        if (Math.abs(difference) >= 10) {
                            bpDiastCounter++;
                        } else {
                            bpDiastCounter = 0;
                        }
                        if (bpDiastCounter >= 2) {
                            triggerAlert(bpAlertFactory.createAlert(patientId, "Diastolic Trend Alert", timestamp));
                        }
                    }
                    bpDiastLastRecord = record;
                    break;
    
                case "Saturation":
                    double saturation = measurementValue;
                    if (saturation < 92) {
                        triggerAlert(boAlertFactory.createAlert(patientId, "Low Saturation Alert", timestamp));
                    }
                    if (lastSaturationTimestamp != 0 && (timestamp - lastSaturationTimestamp) <= 600000) {
                        double saturationDrop = lastSaturation - saturation;
                        if (saturationDrop >= 5) {
                            triggerAlert(boAlertFactory.createAlert(patientId, "Rapid Saturation Drop Alert", timestamp));
                        }
                    }
                    lastSaturation = saturation;
                    lastSaturationTimestamp = timestamp;
                    lowSaturation = saturation < 92;
                    break;
    
                case "ECG":
                    double heartRate = measurementValue;
                    if (heartRate < 50 || heartRate > 100) {
                        triggerAlert(ecgAlertFactory.createAlert(patientId, "Abnormal Heart Rate Alert", timestamp));
                    }
                    if (lastHeartRate != 0.0) {
                        double difference = Math.abs(heartRate - lastHeartRate);
                        if (difference > 0.2 * lastHeartRate) {
                            irregularBeatDetected = true;
                        } else {
                            irregularBeatDetected = false;
                        }
                    }
                    if (irregularBeatDetected) {
                        triggerAlert(ecgAlertFactory.createAlert(patientId, "Irregular Beat Alert", timestamp));
                    }
                    lastHeartRate = heartRate;
                    break;
    
                case "Alert":
                    if (Math.abs(1 - measurementValue) < 0.01) {
                        triggerAlert(bpAlertFactory.createAlert(patientId, "Alert Triggered (Manual)", timestamp));
                    }
                    break;
    
                default:
                    break;
            }
    
            if (recordType.equals("SystolicPressure") || recordType.equals("DiastolicPressure")) {
                if (measurementValue > 180 || measurementValue < 90) {
                    triggerAlert(bpAlertFactory.createAlert(patientId, "Critical Threshold Alert", timestamp));
                }
            }
    
            if (lowSystolic && lowSaturation) {
                triggerAlert(bpAlertFactory.createAlert(patientId, "Hypotensive Hypoxemia Alert", timestamp));
            }
        }
    }
    
    private void triggerAlert(Alert alert) {
        System.out.println("Alert triggered: " + alert);
        triggeredAlerts.add(alert);
    }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }
    
}
