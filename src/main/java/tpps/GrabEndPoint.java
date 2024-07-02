package tpps;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.json.JSONException;
import org.json.JSONObject;

public final class GrabEndPoint {

    private GrabEndPoint() {}

    public static Map<String, Map<String, String>> fetchCodeAndReferenceSubmission(String tgdr, String token) {
        Map<String, Map<String, String>> result = new HashMap<>();
        List<String> items = List.of("ref-genome", "vcf"); // based on JSON from API/submission
        String log = "";
        
        try {

            // download study vcf info
            String file_api = "https://treegenesdb.org/api/submission/" + tgdr + "/vcf?token=" + token;
            ByteArrayOutputStream baos = fetchUrl(file_api);

            // for every organism extract items in json
            // [code:,ref:]
            result = extractItemsPerOrganismFromJson(baos, items);
            // Collect updates to apply after the loop
            Map<String, Map<String, String>> updates = new HashMap<>();
            
            // Loop through every organism and extract VCF code
            for (Map.Entry<String, Map<String, String>> entry : result.entrySet()) {
                String organismNumber = entry.getKey();
                Map<String, String> details = entry.getValue();
                String vcfCode = details.get("vcf");

                if (vcfCode != null) {
                    String submission_api = "https://treegenesdb.org/api/file/" + vcfCode + "/info?token=" + token;
                    baos = fetchUrl(submission_api);
                    Map<String, String> fileJsonElements = extractJsonElements(baos);
                    updates.put(organismNumber, fileJsonElements);

                }
            }
            // Apply updates
            for (Map.Entry<String, Map<String, String>> update : updates.entrySet()) {
                result.put(update.getKey(), update.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log = "Failed to fetch code or path: " + e.getMessage();
        }


        final long end = System.currentTimeMillis();
        log += String.format("Download completed in %d ms\n", (end));

        return result;
    }

    public static ByteArrayOutputStream fetchUrl(String url) throws IOException {
        URL submissionUrl = new URL(url);
        ByteArrayOutputStream baos = downloadUrl(submissionUrl);

        return baos;
    }


    public static ByteArrayOutputStream fetchTreeGenesApiFilePerOrganism(String tgdr, String token, String code) throws IOException {
        String submissionUrlString = "https://treegenesdb.org/api/file/" + code + "/info?token=" + token;
        URL submissionUrl = new URL(submissionUrlString);
        ByteArrayOutputStream baos = downloadUrl(submissionUrl);
        
        return baos;
    }


    private static ByteArrayOutputStream fetchTreeGenesApiSubmissionVcf(String tgdr, String token) throws IOException {
        String submissionUrlString = "https://treegenesdb.org/api/submission/" + tgdr + "/vcf?token=" + token;
        URL submissionUrl = new URL(submissionUrlString);
        ByteArrayOutputStream baos = downloadUrl(submissionUrl);
        
        return baos;
    }

    public static ByteArrayOutputStream downloadUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; wget/1.20.3)");
        connection.setInstanceFollowRedirects(true);
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.err.println("Failed to fetch URL: " + url);
            System.err.println("HTTP response code: " + responseCode);
            return null;
        }

        try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                baos.write(dataBuffer, 0, bytesRead);
            }
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static Map<String, Map<String, String>> extractItemsPerOrganismFromJson(ByteArrayOutputStream baos, List<String> items) throws IOException, JSONException {
        Map<String, Map<String, String>> extractedItems = new HashMap<>();
        String jsonString = baos.toString(StandardCharsets.UTF_8.name());
        JSONObject jsonObject = new JSONObject(jsonString);

        for (String itemKey : items) {
            JSONObject itemObject = jsonObject.getJSONObject(itemKey);
            for (String organismNumber : itemObject.keySet()) {
                Object value = itemObject.get(organismNumber);
                if (value instanceof String) {
                    String stringvalue = (String) value;
                    if (!stringvalue.isEmpty()){
                        extractedItems.computeIfAbsent(organismNumber, k -> new HashMap<>()).put(itemKey, stringvalue);
                    }
                }                
            }
        }

        return extractedItems;
    }
    private static Map<String, String> extractJsonElements(ByteArrayOutputStream baos) throws IOException, JSONException {
        Map<String, String> jsonElements = new HashMap<>();
        String jsonString = baos.toString(StandardCharsets.UTF_8.name());
        JSONObject jsonObject = new JSONObject(jsonString);

        for (String key : jsonObject.keySet()) {
            jsonElements.put(key, jsonObject.get(key).toString());
        }
        return jsonElements;
    }   
}