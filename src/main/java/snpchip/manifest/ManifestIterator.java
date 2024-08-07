package snpchip.manifest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;

public class ManifestIterator {
    private final BufferedReader reader;
    private String[] headers;

    public ManifestIterator(File file) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(file));

        readHeaders();
    
    }

    private void readHeaders() throws FileNotFoundException {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains("[Assay]")) {
                    reader.readLine();
                    headers = reader.readLine().split(",");
                    break;
                }
            }
            if (headers == null) {
                throw new FileNotFoundException("Headers not found in file");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Error reading file headers");
        }
    }

    public boolean hasNext() {
        try {
            return reader.ready();
        } catch (IOException e) {
            return false;
        }
    }

    public HashMap<String, String> next() {
        try {
            String line = reader.readLine();
            while (line != null && line.isEmpty()) {
                line = reader.readLine();
            }
            if (line == null) {
                return new HashMap<>(); // or throw an exception, depending on your requirements
            }
            String[] values = line.split(",");
            // Add null values if the values array is shorter than the headers array
            if (values.length < headers.length) {
                String[] newValues = new String[headers.length];
                System.arraycopy(values, 0, newValues, 0, values.length);
                values = newValues;
            }
            HashMap<String, String> row = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                row.put(headers[i], values[i]);
            }
            return row;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }
}