package fr.banhammer.resourcepack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class PublicIpFetcher {

    public static String fetchPublicIp() {
        String[] services = {
                "https://api.ipify.org",
                "https://checkip.amazonaws.com",
                "https://icanhazip.com"
        };

        for (String service : services) {
            try {
                URL url = URI.create(service).toURL();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String ip = br.readLine().trim();
                    if (!ip.isEmpty()) {
                        return ip;
                    }
                }
            } catch (Exception ignored) {}
        }
        return "127.0.0.1";
    }
}
