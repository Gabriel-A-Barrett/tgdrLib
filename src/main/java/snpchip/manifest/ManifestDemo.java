package snpchip.manifest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.HashMap;

public class ManifestDemo {
    public static void run() {
        System.out.println ("Running: " + ManifestDemo.class.getName());
        
        String filename = "../../test/resources/output/output.fasta";

        try {
            File manifestFile = new File("../../test/resources/manifest_chip34K.csv");
            if (!manifestFile.exists()) {
                throw new IllegalArgumentException("File " + manifestFile + " does not exist.");
            }

            ManifestIterator iterator = new ManifestIterator(manifestFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            try {
                while (iterator.hasNext()) {
                    HashMap<String, String> row = iterator.next();
                    Manifest manifest = new Manifest(row);
                    writer.write(">" + manifest.getIlmnId() + "\n");
                    writer.write(manifest.getReferenceSequence() + "\n");
                }
            } finally {
                writer.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        run();
    }
}