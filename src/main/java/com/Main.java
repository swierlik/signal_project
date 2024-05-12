package com;

import java.io.IOException;

import com.cardiogenerator.HealthDataSimulator;
import com.data_management.DataStorage;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("DataStorage")) {
        DataStorage.main(new String[]{});
        } else if(args[0].equals("HealthDataSimulator")){
            try {
                HealthDataSimulator.main(new String[]{});
            } catch (IOException e) {
                System.err.println("Error generating health data: " + e.getMessage());
            }
        }else if(args[0].equals("AlertGenerator")){
            com.alerts.AlertGenerator.main(new String[]{});
        }else{
            System.out.println("Invalid argument");
        }
    }
}
    
