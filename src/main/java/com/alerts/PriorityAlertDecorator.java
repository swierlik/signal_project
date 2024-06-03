package com.alerts;

public class PriorityAlertDecorator extends AlertDecorator {
    private int priorityLevel;

    public PriorityAlertDecorator(AlertInterface decoratedAlert, int priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    @Override
    public String toString() {
        return "[Priority " + priorityLevel + "] " + decoratedAlert.toString();
    }
}

