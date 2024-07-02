package snpchip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manifest2Fasta {
    private final File inputFile;
    private final File outputFile;
    private final Pattern pattern;
    private boolean isFirstLine = true; // Flag to indicate if the header has been processed

    public Manifest2Fasta(String inputFileName, String outputFileName) {
        this.inputFile = new File(inputFileName);
        this.outputFile = new File(outputFileName);
        this.pattern = Pattern.compile("(.*?)\\[([A-Z])/([A-Z])\\](.*)");
        
    }

    public void convert() {
        try (Scanner scanner = new Scanner(inputFile);
             PrintWriter writer = new PrintWriter(outputFile)) {
            if (scanner.hasNextLine()) scanner.nextLine();  // Skip the header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                processLine(line, writer); // reads manifest at line 16 and writes fasta                
            }
            System.out.println("Conversion to FASTA completed successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void processLine(String line, PrintWriter writer) {
        String[] columns = line.split(",");
        // check the number of columns in manifest
        if (columns.length >= 16) {
            if (isFirstLine) { // skip the first line. column names
                isFirstLine = false;
                return;
            }
            String illmnId = columns[0].trim(); //illuminaID
            String strand = columns[2].trim(); 
            String sequence = columns[15].trim();
            String transformedSequence = transformSequence(sequence, strand);
            writer.printf(">%s\n%s\n", illmnId, transformedSequence);
        }
    }

    private String transformSequence(String sequence, String strand) {
        Matcher matcher = pattern.matcher(sequence);
        if (matcher.find()) {
            String beforeBrackets = matcher.group(1); // before snp flank
            String afterBrackets = matcher.group(4); // after snp flank
            String snp = getSNP(matcher, strand);
            return beforeBrackets + snp + afterBrackets;
        }
        return sequence;  // Return original if no match found
    }

    private String getSNP(Matcher matcher, String strand) {
        if ("TOP".equals(strand)) {
            return matcher.group(2); // first snp is reference
        } else if ("BOT".equals(strand)) {
            return matcher.group(3); // second snp is reference
        } else {
            System.out.println("unrecognized strand nomenclature: " + strand);
            return "?";
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Manifest2Fasta <input file path> <output file path>");
            return;
        }
        Manifest2Fasta converter = new Manifest2Fasta(args[0], args[1]);
        converter.convert();
    }
}
