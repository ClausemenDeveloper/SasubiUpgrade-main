package sasubiupgrade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import sasubiupgrade.model.Encomenda;

public class EncomendasController {

    @FXML
    private ListView<String> listaEncomendas;
    @FXML
    private Button encomendaRecebidaButton;

    private ObservableList<String> encomendas;
    private String nomeEstudante;
    private double saldoDevido;
    private Stage primaryStage;
    private String caminhoArquivoLocal; // Non-static, student-specific
    private static final String CAMINHO_ARQUIVO_REPORTADO = "encomendas_reportadas.csv";

    public void setEstudanteLogado(String nome, double saldo, Stage stage) {
        this.nomeEstudante = nome;
        this.saldoDevido = saldo;
        this.primaryStage = stage;
        this.caminhoArquivoLocal = "encomendas_" + nomeEstudante.replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
        if (encomendas == null) {
            encomendas = FXCollections.observableArrayList();
            loadEncomendas(); // Load encomendas after initializing caminhoArquivoLocal
        }
        listaEncomendas.setItems(encomendas);

        listaEncomendas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            encomendaRecebidaButton.setDisable(!isSelected);
        });
    }

    @FXML
    public void initialize() {
        // Do not load encomendas here; wait for setEstudanteLogado
        if (encomendas == null) {
            encomendas = FXCollections.observableArrayList();
        }
        listaEncomendas.setItems(encomendas);
    }

    @FXML
    public void encomendaRecebida() {
        String selected = listaEncomendas.getSelectionModel().getSelectedItem();
        if (selected != null) {
            encomendas.remove(selected);
            saveToFileLocal();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Encomenda marcada como recebida!");
            alert.showAndWait();
            listaEncomendas.refresh();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma encomenda para marcar como recebida!");
            alert.showAndWait();
        }
    }

    @FXML
    public void verEncomendas() {
        if (encomendas.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nenhuma encomenda registrada.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Encomendas exibidas na lista abaixo.");
            alert.showAndWait();
        }
        listaEncomendas.refresh();
    }

    @FXML
    public void voltarParaMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sasubiupgrade/view/MainView.fxml"));
            Parent pagina = loader.load();
            MainController controller = loader.getController();
            controller.setStage(primaryStage);
            controller.setEstudanteLogado(nomeEstudante, saldoDevido);
            Scene scene = new Scene(pagina);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao voltar para a página principal: " + e.getMessage());
            alert.showAndWait();
        }
    }

 private void loadEncomendas() {
    try {
        encomendas.clear();
        // Load student-specific local orders
        if (Files.exists(Paths.get(caminhoArquivoLocal))) {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivoLocal), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (linha.contains("Estudante: " + nomeEstudante)) {
                    encomendas.add(linha);
                }
            }
        }
        // Load external orders (from Transportadora)
        if (Files.exists(Paths.get(CAMINHO_ARQUIVO_REPORTADO))) {
            List<String> linhas = Files.readAllLines(Paths.get(CAMINHO_ARQUIVO_REPORTADO), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (linha.contains("Estudante: " + nomeEstudante)) {
                    encomendas.add(linha);
                }
            }
        }
        // Manually add some sample orders for testing
        if (nomeEstudante != null && encomendas.isEmpty()) {
            String dataRegistro = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            encomendas.add("ID: ENC001 | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + " | Fonte: Local");
            encomendas.add("ID: ENC002 | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + " | Fonte: Externa: DHL (ID: 12345)");
            // Additional sample orders for specific students
            if ("Clausemen Custodio Nanro".equals(nomeEstudante)) {
                encomendas.add("ID: ENC003 | Estudante: Clausemen Custodio Nanro | Registrado em: " + dataRegistro + " | Fonte: Local");
                encomendas.add("ID: ENC004 | Estudante: Clausemen Custodio Nanro | Registrado em: " + dataRegistro + " | Fonte: Externa: FedEx (ID: 67890)");
            } else if ("Ana Silva".equals(nomeEstudante)) {
                encomendas.add("ID: ENC005 | Estudante: Ana Silva | Registrado em: " + dataRegistro + " | Fonte: Local");
            } else if ("Joao Maravilhoso".equals(nomeEstudante)) {
                encomendas.add("ID: ENC006 | Estudante: Joao Maravilhoso | Registrado em: " + dataRegistro + " | Fonte: Externa: UPS (ID: 54321)");
            }
            saveToFileLocal(); // Save the manually added orders
        }
    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao carregar as encomendas: " + e.getMessage());
        alert.showAndWait();
    }
}
    private void saveToFileLocal() {
        try {
            Files.createDirectories(Paths.get(caminhoArquivoLocal).getParent());
            Files.write(Paths.get(caminhoArquivoLocal), String.join("\n", encomendas).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar as encomendas locais: " + e.getMessage());
            alert.showAndWait();
        }
    }
}