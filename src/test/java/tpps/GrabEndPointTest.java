package tpps;

import java.util.Map;

public class GrabEndPointTest {

    public Map<String, Map<String, String>> test(String token) throws Exception {
        String tgdr = "TGDR682";

        // Execute the method under test
        Map<String, Map<String, String>> result = GrabEndPoint.fetchCodeAndReferenceSubmission(tgdr, token);

        return result;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a token as the first argument.");
            System.exit(1);
        }

        String token = args[0];
        GrabEndPointTest testInstance = new GrabEndPointTest();
        
        try {
            Map<String, Map<String, String>> result = testInstance.test(token);
            System.out.println("Test result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}