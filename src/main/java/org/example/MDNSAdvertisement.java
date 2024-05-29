package org.example;
import java.util.*;
import javax.jmdns.*;

public class MDNSAdvertisement extends Thread{
    private String deviceName;

    public MDNSAdvertisement(String deviceName){
        this.deviceName=deviceName;
    }

    public void run() {
        try {
            // Define the parameters for the MDNS advertisement
            Map<String, String> map = new HashMap<String, String>();

            String domain = "";
            String type = "_FC9F5ED42C8A._tcp.";
            int port = 12345; // arbitrary TCP port
            byte[] nameBytes = {
                    0x23, // Constant byte
                    // 4-byte endpoint ID (random alphanumeric characters)
                    (byte) 'E', (byte) 'P', (byte) 'I', (byte) 'D',
                    // 3-byte service ID
                    (byte) 0xFC, (byte) 0x9F, (byte) 0x5E,
                    // 2 zero bytes
                    0x00, 0x00
            };

            String nameDev = this.deviceName;
            byte[] txtRecord = createTXTRecord(nameDev);
            String txtEnc = Base64.getEncoder().encodeToString(txtRecord);
            map.put("n", txtEnc);
            // Encode the name in URL-safe base64
            String encodedName = Base64.getUrlEncoder().encodeToString(nameBytes);
            // Create the mDNS service
            ServiceInfo serviceInfo = ServiceInfo.create(type, encodedName, port, 0, 0, map);

            // Advertise the service
            JmDNS jmdns = JmDNS.create();
            jmdns.registerService(serviceInfo);
            while (!Thread.interrupted()) {
                Thread.sleep(1000); // Adjust sleep time as needed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] createTXTRecord(String named) {
        // Create the TXT record
        int lengthName = named.length();
        byte[] txtRecord = new byte[18+lengthName]; // 1 byte for bit field, 16 bytes for unknown purpose, 1 byte for length of device name
        txtRecord[0] = 0b00000100; // Bit field with version 0 and visibility set to visible
        // Set random 16 bytes
        new Random().nextBytes(Arrays.copyOfRange(txtRecord, 1, 17));
        // User-visible device name in UTF-8
        String deviceName = named; // Change this to the actual device name
        byte[] deviceNameBytes = deviceName.getBytes();
        txtRecord[17] = (byte) deviceNameBytes.length; // Length of the device name
        System.arraycopy(deviceNameBytes, 0, txtRecord, 18, deviceNameBytes.length); // Copy device name bytes
        return txtRecord;
    }
}