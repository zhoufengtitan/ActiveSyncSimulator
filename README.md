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


How to Use

1, For simulation mode:

    Run the ActiveSyncFolderSync class directly

    It will read from the simulated XML file

2, For real HTTP mode:

    Set simulationMode = false in the main method

    And provide a real endpoint URL