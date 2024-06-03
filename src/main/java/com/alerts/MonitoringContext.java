package com.alerts;

import java.util.ArrayList;
import java.util.List;

public class MonitoringContext {
    public boolean lowSystolic = false;
    public boolean lowSaturation = false;
    public List<Alert> alerts = new ArrayList<>();
}

