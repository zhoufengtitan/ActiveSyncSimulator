import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class FolderSyncClient {

    private static final Logger logger = Logger.getLogger(FolderSyncClient.class.getName());        

    private static final String DEFAULT_ENDPOINT = "http://localhost:8080/foldersync";
    private static final String SIMULATED_RESPONSE_FILE = "simulated-response.xml";

    private String endpoint;
    private boolean simulationMode;

    public FolderSyncClient(String endpoint, boolean simulationMode) {
        this.endpoint = endpoint != null ? endpoint : DEFAULT_ENDPOINT;
        this.simulationMode = simulationMode;
    }

    // Run the FolderSync operation
    public void runFolderSync(String syncKey, String deviceId, String policyKey) throws Exception {
        try {
            String xmlResponse;
            if (simulationMode) {
                xmlResponse = readSimulatedResponse();
            } else {
                String xmlRequest = buildFolderSyncRequest(syncKey);
                xmlResponse = sendHttpRequest(xmlRequest, deviceId, policyKey);
            }

            logger.info("Received FolderSync response: " + xmlResponse);
            parseAndPrintResponse(xmlResponse);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during FolderSync operation: ", e);
            throw e;
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
            logger.info("No folders found in response");
            return;
        }

        for (int i = 0; i < folders.getLength(); i++) {
            Element folder = (Element) folders.item(i);

            String id = getElementText(folder, "ServerId");
            String name = getElementText(folder, "DisplayName");
            String parentId = getElementText(folder, "ParentId");

            logger.info(String.format("Folder %d:\n  FolderId: %s\n  DisplayName: %s\n  ParentId: %s",
                    i + 1,
                    folderId != null ? folderId : "(missing)",
                    displayName != null ? displayName : "(missing)",
                    parentId != null ? parentId : "(missing)"
            ));
        }
    }

    private static String getElementText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent() : "";
    }

    public static void main(String[] args) {
        configureLogging();
        
        boolean simulationMode = true; // Set to false for HTTP requests
        ActiveSyncClinet folderSync = new ActiveSyncClinet(null, simulationMode);
        
        String syncKey = "123"; 
        String deviceId = "test-device";
        String policyKey = "45678";
        
        try {
            // Validate inputs
            ivalidateInputs(syncKey, deviceId, policyKey);
            folderSync.runFolderSync(syncKey, deviceId, policyKey);
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Simulation file not found: ", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Error while reading the simulation file: ", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred.", e);
        }
    }

    private static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.INFO);
    }

    //  Validate inputs
    private static void validateInputs(String syncKey, String deviceId, String policyKey) {
        if (syncKey == null || syncKey.isEmpty())
            throw new IllegalArgumentException("SyncKey cannot be null or empty.");
        if (deviceId == null || deviceId.isEmpty())
            throw new IllegalArgumentException("DeviceId cannot be null or empty.");
        if (policyKey == null || policyKey.isEmpty())
            throw new IllegalArgumentException("PolicyKey cannot be null or empty.");
    }
}
