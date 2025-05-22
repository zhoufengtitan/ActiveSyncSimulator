# ActiveSyncSimulator

Task: Implement a Java class that sends a simulated FolderSync request to a mock or
real ActiveSync endpoint and parses the response.
Requirements:
• Construct an XML request for the FolderSync command.
• Use HTTP POST to send the request.
• Accept the following inputs:
o syncKey
o deviceId
o policyKey
• Parse the XML response and print:
o FolderId
o DisplayName
o ParentId
Sample tools/libraries:
• Apache HttpClient or HttpURLConnection
• DOM, SAX, or JAXB for XML parsing
General Guidelines:
• The assignment should be completed in 2 to 3 hours.
• You may simulate server responses using static XML files if you don’t have a real
endpoint.
• Use clean, modular Java code with comments where necessary.
• Include basic exception handling.


Overview

• Constructs an XML FolderSync request

• Sends it via HTTP POST to a simulated local file-based endpoint

• Parses a static XML response from a file

• Extracts and prints: FolderId, DisplayName, and ParentId



How to Use

1, For simulation mode:

    • Open a terminal in that folder
    • Compile: javac FolderSyncClient.java
    • Run: java FolderSyncClient

    Sample Output:
    May 22, 2025 10:00:00 PM FolderSyncClient main
    INFO: Sending FolderSync request:
    <?xml version="1.0" encoding="UTF-8"?>
    <FolderSync xmlns="FolderHierarchy">
        <SyncKey>0</SyncKey>
    </FolderSync>

    May 22, 2025 10:00:00 PM FolderSyncClient runFolderSync
    INFO: Folder 1:
    FolderId: 1
    DisplayName: Inbox
    ParentId: 0

    May 22, 2025 10:00:00 PM FolderSyncClient runFolderSync
    INFO: Folder 2:
    FolderId: 2
    DisplayName: SubFolder
    ParentId: 1



2, For real HTTP mode:

    Set simulationMode = false in the main method

    And provide a real endpoint URL
