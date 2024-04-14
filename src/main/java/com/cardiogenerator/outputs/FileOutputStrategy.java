
//package name change to cardiogenerator instead of cardio_generator
package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Used to output data into a file format, in this case .txt
 *  
 */

//class names should be upper case
public class FileOutputStrategy implements OutputStrategy {

    //changed name to lowerCamelCase
    private String baseDirectory;

    //acces modifier changed from public to private
    private final ConcurrentHashMap<String, String> file_map = new ConcurrentHashMap<>();

    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        /**
         * Outputs given data into a txt file
         * @param patientId Id of patient to print
         * @param timestamp Time of said observation in a long format
         * @param label Label of said observation
         * @param data Data of said observation to print
         */
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the filePath variable
        //changed name to lowerCamelCase
        String filePath = file_map.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}