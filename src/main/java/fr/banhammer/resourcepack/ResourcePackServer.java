package fr.banhammer.resourcepack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.banhammer.BanHammerPlugin;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.HexFormat;

public class ResourcePackServer {

    private final BanHammerPlugin plugin;
    private HttpServer server;
    private final int port;
    private byte[] sha1HashBytes;
    private String sha1HashHex;

    public ResourcePackServer(BanHammerPlugin plugin, int port) {
        this.plugin = plugin;
        this.port = port;
        calculateHash();
    }

    private void calculateHash() {
        try (InputStream is = plugin.getResource("BanHammer_ResourcePack.zip")) {
            if (is != null) {
                byte[] data = is.readAllBytes();
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                sha1HashBytes = digest.digest(data);
                sha1HashHex = HexFormat.of().formatHex(sha1HashBytes);
                plugin.getLogger().info("[ResourcePack DEBUG] Pack Size: " + data.length + " bytes | SHA-1: " + sha1HashHex);
            } else {
                plugin.getLogger().severe("[ResourcePack DEBUG ERROR] BanHammer_ResourcePack.zip IS NULL in plugin resources!");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[ResourcePack DEBUG ERROR] Failed to calculate SHA-1: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/resourcepack.zip", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) {
                    String clientAddress = exchange.getRemoteAddress().toString();
                    plugin.getLogger().info("[ResourcePack DEBUG] Incoming HTTP Request from " + clientAddress + " for URI: " + exchange.getRequestURI());

                    try (InputStream is = plugin.getResource("BanHammer_ResourcePack.zip")) {
                        if (is == null) {
                            plugin.getLogger().severe("[ResourcePack DEBUG ERROR] Cannot serve /resourcepack.zip - File not found in JAR!");
                            exchange.sendResponseHeaders(404, 0);
                            exchange.close();
                            return;
                        }
                        byte[] bytes = is.readAllBytes();
                        exchange.getResponseHeaders().set("Content-Type", "application/zip");
                        exchange.getResponseHeaders().set("Accept-Ranges", "bytes");
                        exchange.sendResponseHeaders(200, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                        plugin.getLogger().info("[ResourcePack DEBUG SUCCESS] Served " + bytes.length + " bytes to " + clientAddress);
                    } catch (Exception e) {
                        plugin.getLogger().severe("[ResourcePack DEBUG ERROR] Failed serving pack to " + clientAddress + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("[ResourcePack DEBUG] Integrated HTTP Server successfully listening on port " + port);
        } catch (Exception e) {
            plugin.getLogger().severe("[ResourcePack DEBUG ERROR] Could not start HTTP server on port " + port + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public byte[] getSha1HashBytes() {
        return sha1HashBytes;
    }

    public String getSha1HashHex() {
        return sha1HashHex;
    }
}
