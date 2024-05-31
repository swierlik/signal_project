package com.alerts;

import org.junit.Before;
import org.junit.Test;

import com.data_management.Patient;

import java.util.List;
import static org.junit.Assert.*;

public class AlertGenRefacTest {

    private AlertGenRefac evaluator;

    @Before
    public void setUp() {
        evaluator = new AlertGenRefac();
    }

    @Test
    public void testEvaluateDataTriggersSystolicTrendAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(120, "SystolicPressure", 17128316L);
        patient.addRecord(135, "SystolicPressure", 17128316L + 1);
        patient.addRecord(145, "SystolicPressure", 17128316L + 2);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Systolic Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    public void testEvaluateDataTriggersDiastolicTrendAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(90, "DiastolicPressure", 17128316L);
        patient.addRecord(105, "DiastolicPressure", 17128316L + 1);
        patient.addRecord(115, "DiastolicPressure", 17128316L + 2);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Diastolic Trend Alert", alerts.get(0).getCondition());
    }

    @Test
    public void testEvaluateDataTriggersLowSaturationAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(85, "Saturation", 17128316L);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Low Saturation Alert", alerts.get(0).getCondition());
    }

    @Test
    public void testEvaluateDataTriggersRapidSaturationDropAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(97, "Saturation", 17128316L);
        patient.addRecord(92, "Saturation", 17128316L + 500000);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Rapid Saturation Drop Alert", alerts.get(0).getCondition());
    }

    @Test
    public void testEvaluateDataTriggersAbnormalHeartRateAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(120, "ECG", 17128316L);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Abnormal Heart Rate Alert", alerts.get(0).getCondition());
    }

    @Test
    public void testEvaluateDataTriggersCriticalThresholdAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(190, "SystolicPressure", 17128316L);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Critical Threshold Alert", alerts.get(0).getCondition());
    }

    @Test
    public void testEvaluateDataTriggersHypotensiveHypoxemiaAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(89, "SystolicPressure", 17128316L);
        patient.addRecord(91, "Saturation", 17128316L + 1);

        evaluator.evaluateData(patient);

        List<Alert> alerts = evaluator.getTriggeredAlerts();
        assertEquals(3, alerts.size());//low syst, saturation and hypotensive hypoxemia
        assertEquals("Hypotensive Hypoxemia Alert", alerts.get(2).getCondition());
    }

    // Additional test cases to cover other scenarios

}
