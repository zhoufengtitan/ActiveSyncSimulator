import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FolderSyncClient {

    private static final String DEFAULT_ENDPOINT = "http://localhost:8080/foldersync";
    private static final String SIMULATED_RESPONSE_FILE = "simulated-response.xml";

    private String endpoint;
    private boolean simulationMode;

    public FolderSyncClient(String endpoint, boolean simulationMode) {
        this.endpoint = endpoint != null ? endpoint : DEFAULT_ENDPOINT;
        this.simulationMode = simulationMode;
    }

    // Run the FolderSync operation
    public void runFolderSync(String syncKey, String deviceId, String policyKey) {
        try {
            String xmlResponse;
            if (simulationMode) {
                xmlResponse = readSimulatedResponse();
            } else {
                String xmlRequest = buildFolderSyncRequest(syncKey);
                xmlResponse = sendHttpRequest(xmlRequest, deviceId, policyKey);
            }

            System.out.println("Received FolderSync response: " + xmlResponse);
            parseAndPrintResponse(xmlResponse);

        } catch (Exception e) {
            System.err.println("Error during FolderSync operation: " + e.getMessage());
            e.printStackTrace();
        }
    }
   
    // Builds the XML request for FolderSync command
    private String buildFolderSyncRequest(String syncKey) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<FolderSync xmlns=\"FolderHierarchy\">"
                + "<SyncKey>" + syncKey + "</SyncKey>"
                + "</FolderSync>";
    }

    // Send HTTP POST request
    private String sendHttpPost(String xmlRequest, String deviceId, String policyKey) throws IOException {
        URL url = new URL(this.endpoint + "?DeviceId=" + deviceId + "&DeviceType=SmartPhone");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/vnd.ms-sync.wbxml");
        conn.setRequestProperty("X-MS-PolicyKey", policyKey);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(xmlRequest.getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Request failed with code : " + responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    // Read static XML file for response
    private static String readSimulatedResponse() throws IOException {
        InputStream is = FolderSyncClient.class.getClassLoader().getResourceAsStream(SIMULATED_RESPONSE_FILE);
        if (is == null) throw new FileNotFoundException("Simulation file not found.");
        return new String(is.readAllBytes());
    }

    // Parse XML response and print FolderId, DisplayName, ParentId
    private static void parseAndPrintResponse(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

        NodeList folders = doc.getElementsByTagNameNS("FolderHierarchy", "Folder");
        if (folders.getLength() == 0) {
            System.out.println("No folders found in response");
            return;
        }

        for (int i = 0; i < folders.getLength(); i++) {
            Element folder = (Element) folders.item(i);

            String id = getElementText(folder, "ServerId");
            String name = getElementText(folder, "DisplayName");
            String parentId = getElementText(folder, "ParentId");

            System.out.println("FolderId: " + id);
            System.out.println("DisplayName: " + name);
            System.out.println("ParentId: " + parentId);
        }
    }

    private static String getElementText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent() : "";
    }

    public static void main(String[] args) {
        boolean simulationMode = true; // Set to false for HTTP requests
        ActiveSyncClinet folderSync = new ActiveSyncClinet(null, simulationMode);
        
        String syncKey = "123"; 
        String deviceId = "test-device";
        String policyKey = "45678";
        
        folderSync.runFolderSync(syncKey, deviceId, policyKey);
    }
}
