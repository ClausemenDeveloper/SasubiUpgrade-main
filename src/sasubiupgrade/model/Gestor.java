package sasubiupgrade.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class Gestor {
    private String username;
    private String password;

    public Gestor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Load manager credentials from a file
    public static Map<String, String> carregarGestores(String caminho) throws IOException {
        Map<String, String> gestores = new HashMap<>();
        if (Files.exists(Paths.get(caminho))) {
            for (String linha : Files.readAllLines(Paths.get(caminho), StandardCharsets.UTF_8)) {
                if (!linha.trim().isEmpty()) {
                    String[] partes = linha.split(",");
                    if (partes.length == 2) {
                        gestores.put(partes[0].trim(), partes[1].trim());
                    }
                }
            }
        }
        return gestores;
    }

    // Authenticate a manager
    public static boolean autenticar(String username, String password, String caminho) throws IOException {
        Map<String, String> gestores = carregarGestores(caminho);
        return gestores.containsKey(username) && gestores.get(username).equals(password);
    }
}