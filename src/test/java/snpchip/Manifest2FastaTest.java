package snpchip;

public class Manifest2FastaTest {

    public void test() throws Exception {
        String manifestFilePath = "src/test/resources/manifest_chip34K.csv";
        String outputfilename = "src/test/resources/output/manifest.fasta";

        // execute method under test

        // initialize
        Manifest2Fasta converter = new Manifest2Fasta(manifestFilePath, outputfilename);
        
        // converts manifest to fasta format
        converter.convert();


    }

    public static void main(String[] args) {
        try {
            Manifest2FastaTest testInstance = new Manifest2FastaTest();
            testInstance.test();
            System.out.println("Test executed succesfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Test execution failed.");
        }
    }
}