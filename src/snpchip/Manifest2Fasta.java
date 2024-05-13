import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manifest2Fasta {
    public static void main(String[] args) {
        boolean isFirstLine = true; // Flag to skip the header
        Pattern pattern = Pattern.compile("(.*?)\\[([A-Z])/([A-Z])\\](.*)");

        try (Scanner sc = new Scanner(new java.io.File("manifest_chip34K.csv"));
             PrintWriter writer = new PrintWriter("manifest.fasta")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] columns = line.split(","); // Correct delimiter to string
                if (columns.length >= 16) {
                    // Skip the first line if it's the header
                    if (isFirstLine) {
                        isFirstLine = false;
                    continue;
                    }
                    String header = columns[0].trim();
                    String strand = columns[2].trim();
                    String sequence = columns[15].trim();

                    // Apply regex to find and extract the required parts of the sequence
                    Matcher matcher = pattern.matcher(sequence);
                    if (matcher.find()) {
                        String beforeBrackets = matcher.group(1); // Text before the brackets
                        String afterBrackets = matcher.group(4); // Text after the brackets
                        String snp;
                        if ("TOP".equals(strand)) {
                            snp = matcher.group(2);   // The first letter inside the brackets
                        } else if ("BOT".equals(strand)) {
                            snp = matcher.group(3);
                        } else {
                            System.out.println("unrecognized strand nomenclature: " + strand);
                            break;
                        }
                        sequence = beforeBrackets + snp + afterBrackets; // Combine them for the output
                    }
                    writer.printf(">%s\n%s\n", header, sequence);
                }
            }
            System.out.println("Conversion to FASTA completed successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } 
    }
}
