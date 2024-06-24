import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public final class GrabEndPoint {
    private GrabEndPoint() {
    }

    public static void main(final String[] args) throws IOException {
        if (args.length < 1) { // test minimum args requirement
            System.out.println("Usage: " + GrabEndPoint.class + " tgdr [opt.]");
            System.exit(1);
        }
    
        final URL inputurl = new URL(args[0]);
        final File outputFile = args.length >=2 ? new File(args[1]) : null;

        final long start = System.currentTimeMillis();
        
        try (BufferedInputStream in = new BufferedInputStream(inputurl.openStream());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                baos.write(dataBuffer, 0, bytesRead);
            }
            
            String jsonString = baos.toString(StandardCharsets.UTF_8.name());
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract the value from the JSON object
            String code = jsonObject.getJSONObject("vcf").getString("1");
            
            String vcf_path_url = "https://treegenesdb.org/api/file/" + code + "/info?token=7a2aef974774b9447eb1b7e475e6c991.1"

            

        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }

        final long end = System.currentTimeMillis();
        System.out.printf("Download completed in %d ms\n", (end - start));
    }
}

