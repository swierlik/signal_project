package com.alerts;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatInterval;
    private int repeatCount;

    public RepeatedAlertDecorator(AlertInterface decoratedAlert, int repeatInterval, int repeatCount) {
        super(decoratedAlert);
        this.repeatInterval = repeatInterval;
        this.repeatCount = repeatCount;
    }

    public void checkAlert() {
        for (int i = 0; i < repeatCount; i++) {
            System.out.println("Checking alert: " + decoratedAlert.toString());
            try {
                Thread.sleep(repeatInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return decoratedAlert.toString() + " [Repeated " + repeatCount + " times every " + repeatInterval + " ms]";
    }
}
