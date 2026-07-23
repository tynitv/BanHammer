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
                plugin.getLogger().info("Calculated ResourcePack SHA-1: " + sha1HashHex);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not calculate Resource Pack SHA-1: " + e.getMessage());
        }
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/resourcepack.zip", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) {
                    try (InputStream is = plugin.getResource("BanHammer_ResourcePack.zip")) {
                        if (is == null) {
                            exchange.sendResponseHeaders(404, 0);
                            exchange.close();
                            return;
                        }
                        byte[] bytes = is.readAllBytes();
                        exchange.getResponseHeaders().set("Content-Type", "application/zip");
                        exchange.sendResponseHeaders(200, bytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(bytes);
                        os.close();
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error serving resource pack: " + e.getMessage());
                    }
                }
            });
            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("Integrated Resource Pack HTTP Server started on port " + port);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not start integrated Resource Pack server on port " + port + ": " + e.getMessage());
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
