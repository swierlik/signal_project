package com.alerts;

import java.util.List;

import com.data_management.DataReaderReal;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    public static void main(String[] args) {
        DataReaderReal reader = new DataReaderReal();
        DataStorage dataStorage = new DataStorage();
        reader.readData(dataStorage);
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        Patient patient = new Patient(53);
        alertGenerator.evaluateData(patient);
    }

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */

    public void evaluateData(Patient patient) {
        // Get all records for the patient
        List<PatientRecord> records = patient.getRecords(17128316L,171283163844400L);

        // Implementation goes here
        int bpSystCounter = 0;
        PatientRecord bpSystLastRecord = null;
        int bpDiastCounter = 0;
        PatientRecord bpDiastLastRecord = null;

        int saturationCounter = 0;
        double lastSaturation = 0.0;
        long lastSaturationTimestamp = 0;

        boolean lowSystolic = false;
        boolean lowSaturation = false;

        int heartRateCounter = 0;
        double lastHeartRate = 0.0;
        boolean irregularBeatDetected = false;
        
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("SystolicPressure")) {
                if (bpSystLastRecord != null) {
                    double systolicDifference = record.getMeasurementValue() - bpSystLastRecord.getMeasurementValue();
                    if (Math.abs(systolicDifference) > 10) {
                        bpSystCounter++;
                    } else {
                        bpSystCounter = 0; // Reset counter if difference is not significant
                    }
                    if (bpSystCounter >= 2) {
                        Alert trendAlert = new Alert(String.valueOf(record.getPatientId()), "Systolic Trend Alert", record.getTimestamp());
                        triggerAlert(trendAlert);
                    }
                }
                //Check for low systolic pressure
                if (record.getMeasurementValue() < 90) {
                    lowSystolic = true;
                } else {
                    lowSystolic = false;
                }

                bpSystLastRecord = record;
            } else if (record.getRecordType().equals("DiastolicPressure")) {
                if (bpDiastLastRecord != null) {
                    double diastolicDifference = record.getMeasurementValue() - bpDiastLastRecord.getMeasurementValue();
                    if (Math.abs(diastolicDifference) > 10) {
                        bpDiastCounter++;
                    } else {
                        bpDiastCounter = 0; // Reset counter if difference is not significant
                    }
                    if (bpDiastCounter >= 2) {
                        Alert trendAlert = new Alert(String.valueOf(record.getPatientId()), "Diastolic Trend Alert", record.getTimestamp());
                        triggerAlert(trendAlert);
                    }
                }

                bpDiastLastRecord = record;
            } else if (record.getRecordType().equals("Saturation")) {
                double saturation = record.getMeasurementValue();
        
                // Check for low saturation
                if (saturation < 92) {
                    lowSaturation = true;
                    Alert lowSaturationAlert = new Alert(String.valueOf(record.getPatientId()), "Low Saturation Alert", record.getTimestamp());
                    triggerAlert(lowSaturationAlert);
                }
                else {
                    lowSaturation = false;
                }
        
                // Check for rapid drop
                if (lastSaturationTimestamp != 0 && (record.getTimestamp() - lastSaturationTimestamp) <= 600000) {
                    double saturationDrop = lastSaturation - saturation;
                    if (saturationDrop >= 5) {
                        Alert rapidDropAlert = new Alert(String.valueOf(record.getPatientId()), "Rapid Saturation Drop Alert", record.getTimestamp());
                        triggerAlert(rapidDropAlert);
                    }
                }
        
                lastSaturation = saturation;
                lastSaturationTimestamp = record.getTimestamp();
            } else if (record.getRecordType().equals("ECG")) {
                double heartRate = record.getMeasurementValue();
        
                // Check for abnormal heart rate
                if (heartRate < 50 || heartRate > 100) {
                    Alert abnormalHeartRateAlert = new Alert(String.valueOf(record.getPatientId()), "Abnormal Heart Rate Alert", record.getTimestamp());
                    triggerAlert(abnormalHeartRateAlert);
                }
        
                // Check for irregular beat patterns
                if (lastHeartRate != 0.0) {
                    double heartRateDifference = Math.abs(heartRate - lastHeartRate);
                    if (heartRateDifference > 0.2 * lastHeartRate) {
                        irregularBeatDetected = true;
                    } else {
                        irregularBeatDetected = false;
                    }
                }
        
                if (irregularBeatDetected) {
                    Alert irregularBeatAlert = new Alert(String.valueOf(record.getPatientId()), "Irregular Beat Alert", record.getTimestamp());
                    triggerAlert(irregularBeatAlert);
                }
        
                lastHeartRate = heartRate;
            } else if (record.getRecordType().equals("Alert")&&Math.abs(1-record.getMeasurementValue())<0.01)
            {
                Alert alert = new Alert(String.valueOf(record.getPatientId()), "Alert Triggered (Manual)", record.getTimestamp());
                triggerAlert(alert);
            }
            
            // Check for critical thresholds
            if (record.getRecordType().equals("SystolicPressure") || record.getRecordType().equals("DiastolicPressure")) {
                if (record.getMeasurementValue() > 180 || record.getMeasurementValue() < 90) {
                    Alert thresholdAlert = new Alert(String.valueOf(record.getPatientId()), "Critical Threshold Alert", record.getTimestamp());
                    triggerAlert(thresholdAlert);
                }
            }

            // Check for Hypotensive Hypoxemia Alert
            if (lowSystolic && lowSaturation) {
                Alert hypotensiveHypoxemiaAlert = new Alert(String.valueOf(record.getPatientId()), "Hypotensive Hypoxemia Alert", record.getTimestamp());
                triggerAlert(hypotensiveHypoxemiaAlert);
            }
        }
        

    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        System.out.println("Alert triggered: " + alert.getPatientId() + " - " + alert.getCondition() + " - " + alert.getTimestamp());
    }
}
