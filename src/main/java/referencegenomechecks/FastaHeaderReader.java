package referencegenomechecks.fastagffcomparator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

/**
 * Reads a FASTA file and checks for the presence of periods in the sequences.
 */
public class FastaHeaderReader {
    private File fastaFile;
    //private Set<Accession> rawHeaders;

    public FastaHeaderReader(File fastaFile) {
        this.fastaFile = fastaFile;
        //this.rawHeaders = new HashSet<>();
    }

    /**
     * Runs the FASTA file processing.
     * @throws IOException if an error occurs while reading the file.
     */
    public void runFastaFile() throws IOException {
        try {
            FastaSequenceFile fasta = new FastaSequenceFile(fastaFile, false);
            ReferenceSequence seq;

            while ((seq = fasta.nextSequence()) != null) {
                if (containsPeriod(seq)) {
                    System.out.println("Sequence contains period: " + seq.getBases());
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error running FASTA file: " + e.getMessage());
        }
    }

    /**
     * Checks if a sequence contains a period.
     * @param sequence the sequence to check.
     * @return true if the sequence contains a period, false otherwise.
     */
    private boolean containsPeriod(ReferenceSequence sequence) {
        Boolean found = false;
        byte[] bases = sequence.getBases();
        for (byte base : bases) {
            if (base == '.') { // or any other symbol you're looking for
                found = true;
            } else {
            }
        }
        return found; 
    }
}