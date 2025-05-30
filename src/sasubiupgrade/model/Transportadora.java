package sasubiupgrade.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transportadora {
    private String nomeEstudante;
    private String id;
    private String dataEntrega;
    private String status; // "In Transit", "Delivered"

    public Transportadora(String nomeEstudante, String id, String dataEntrega, String status) {
        this.nomeEstudante = nomeEstudante;
        this.id = id;
        this.dataEntrega = dataEntrega;
        this.status = status;
    }

    public String getNomeEstudante() {
        return nomeEstudante;
    }

    public String getId() {
        return id;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Estudante: " + nomeEstudante + " | Data Entrega: " + dataEntrega + " | Status: " + status;
    }

    // Load deliveries from CSV
    public static List<Transportadora> carregarEntregas(String caminho) throws IOException {
        List<Transportadora> lista = new ArrayList<>();
        if (Files.exists(Paths.get(caminho))) {
            List<String> linhas = Files.readAllLines(Paths.get(caminho), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    Transportadora entrega = parseEntrega(linha);
                    if (entrega != null) {
                        lista.add(entrega);
                    }
                }
            }
        }
        return lista;
    }

    // Parse a line into a Transportadora object
    private static Transportadora parseEntrega(String linha) {
        // Expected format: "ID: id | Estudante: name | Data Entrega: date | Status: status"
        String[] partes = linha.split(" \\| ");
        if (partes.length == 4) {
            String id = partes[0].replace("ID: ", "").trim();
            String nome = partes[1].replace("Estudante: ", "").trim();
            String data = partes[2].replace("Data Entrega: ", "").trim();
            String status = partes[3].replace("Status: ", "").trim();
            return new Transportadora(nome, id, data, status);
        }
        return null;
    }

    // Save deliveries to CSV
    public static void salvarEntregas(List<Transportadora> entregas, String caminho) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Transportadora e : entregas) {
            linhas.add(e.toString());
        }
        Files.write(Paths.get(caminho), linhas, StandardCharsets.UTF_8);
    }

    // Add a new delivery
    public static void adicionarEntrega(String nomeEstudante, String id, String dataEntrega, String caminho) throws IOException {
        Transportadora novaEntrega = new Transportadora(nomeEstudante, id, dataEntrega, "In Transit");
        List<Transportadora> entregas = carregarEntregas(caminho);
        entregas.add(novaEntrega);
        salvarEntregas(entregas, caminho);
    }
}