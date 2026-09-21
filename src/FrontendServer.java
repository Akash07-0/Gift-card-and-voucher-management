import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FrontendServer {

    public static void main(String[] args) throws Exception {

        Path root = Paths.get(".").toAbsolutePath().normalize();

        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 5500), 0
        );

        server.createContext("/", exchange -> {

            String path = exchange.getRequestURI().getPath();

            if (path.equals("/")) {
                path = "/index.html";
            }

            Path file = root.resolve(path.substring(1)).normalize();

            if (!file.startsWith(root)
                    || !Files.exists(file)
                    || Files.isDirectory(file)) {

                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            byte[] data = Files.readAllBytes(file);

            exchange.getResponseHeaders()
                    .set("Content-Type", "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(200, data.length);

            try (OutputStream out = exchange.getResponseBody()) {
                out.write(data);
            }
        });

        server.start();

        System.out.println(
                "Frontend running at http://127.0.0.1:5500/index.html"
        );
    }
}