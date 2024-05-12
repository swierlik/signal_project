package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataReaderReal implements DataReader{
    @Override
    public void readData(DataStorage dataStorage) {

        File directory = new File("C:\\Users\\igors\\OneDrive\\Dokumenty\\##Studia\\Rok1\\Programowanie\\Period5\\signal_project\\output");

        // Get all files in the directory
        File[] files = directory.listFiles();
        

        for(File file : files){
            String data;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                // Read each line from the file and append it to the StringBuilder
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                data = content.toString();
            }
            catch (IOException e) {
                System.err.println("Error reading file: " + file.getName());
                continue;
            }

            Pattern pattern = Pattern.compile("Patient ID: (\\d+), Timestamp: (\\d+), Label: (.*), Data: (\\d+\\.\\d+)");

            
            Matcher matcher = pattern.matcher(data);

            // Iterate over matches and extract variables
            while (matcher.find()) {
                int patientID = Integer.parseInt(matcher.group(1));
                long timestamp = Long.parseLong(matcher.group(2));
                String label = matcher.group(3);
                double dataValue;
                if(file.getName().equals("Saturation.txt"))//saturation and alert are strings, rest doubles
                    dataValue = Double.parseDouble(matcher.group(4).substring(0, matcher.group(4).length()-1));
                else if(file.getName().equals("Alert.txt"))
                    dataValue = matcher.group(4).equals("triggered") ? 1 : 0; 
                else
                    dataValue = Double.parseDouble(matcher.group(4));
                // Read the data from the file
                // Store the data in the data storage
                dataStorage.addPatientData(patientID, dataValue, label, timestamp);
                System.out.println("Patient ID: " + patientID + ", Timestamp: " + timestamp + ", Label: " + label + ", Data: " + dataValue);
            }
        }
        //System.out.println(Arrays.toString(files));
    }
    public static void main(String[] args) {
        DataReaderReal reader = new DataReaderReal();
        DataStorage dataStorage = new DataStorage(reader);
    }

    
}
