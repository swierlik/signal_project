package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.Patient;
import com.data_management.PatientRecord;

public class AlertGenerator {
    List<Alert> triggeredAlerts= new ArrayList<>();
    
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(17128316L, 171283163844400L);

        List<Alert> alerts = new ArrayList<>();
        boolean lowSystolic = false;
        boolean lowSaturation = false;


        AlertStrategy bloodPressureStrategy = new BloodPressureStrategy();
        AlertStrategy heartRateStrategy = new HeartRateStrategy();
        AlertStrategy oxygenSaturationStrategy = new OxygenSaturationStrategy();

        List<AlertStrategy> strategies = new ArrayList<>();
            strategies.add(bloodPressureStrategy);
            strategies.add(heartRateStrategy);
            strategies.add(oxygenSaturationStrategy);

        for (PatientRecord record : records) {
            for (AlertStrategy strategy : strategies) {
                strategy.checkAlert(record, alerts);
            }

            // Check for low systolic pressure
            if (record.getRecordType().equals("SystolicPressure") && record.getMeasurementValue() < 90) {
                lowSystolic = true;
            }

            // Check for low saturation
            if (record.getRecordType().equals("Saturation") && record.getMeasurementValue() < 92) {
                lowSaturation = true;
            }
        }
        
        // Check for Hypotensive Hypoxemia Alert
        if (lowSystolic && lowSaturation) {
            // Assuming the last record's timestamp can be used for the alert
            PatientRecord lastRecord = records.get(records.size() - 1);
            Alert hypotensiveHypoxemiaAlert = new Alert(String.valueOf(lastRecord.getPatientId()), "Hypotensive Hypoxemia Alert", lastRecord.getTimestamp());
            alerts.add(hypotensiveHypoxemiaAlert);
        }

        for (Alert alert : alerts) {
            triggerAlert(alert);
        }
    }

    private void triggerAlert(Alert alert) {
        System.out.println("Alert triggered: " + alert);
        triggeredAlerts.add(alert);
    }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    public static void main(String[] args) {
        AlertGenerator evaluator = new AlertGenerator();
        Patient patient = new Patient(1);
        patient.addRecord(120, "SystolicPressure", 17128316L);
        patient.addRecord(135, "SystolicPressure", 17128316L + 1);
        patient.addRecord(145, "SystolicPressure", 17128316L + 2);
        evaluator.evaluateData(patient);
    }
    
}
