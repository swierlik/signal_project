package com.alerts;

import java.util.List;

import com.data_management.PatientRecord;

public interface AlertStrategy {
    void checkAlert(PatientRecord record, List<Alert> alerts);
}

