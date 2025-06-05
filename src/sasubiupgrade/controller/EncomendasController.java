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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
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
    private String caminhoArquivoLocal;
private static final String CAMINHO_ARQUIVO_REPORTADO = "/home/clausemen-custodio-nanro/shared/encomendas_reportadas.csv";

@FXML
public void initialize() {
    if (encomendas == null) {
        encomendas = FXCollections.observableArrayList();
    }
    loadEncomendas(); // Load data on initialization
    listaEncomendas.setItems(encomendas);
}

public void setEstudanteLogado(String nome, double saldo, Stage stage) {
    this.nomeEstudante = nome;
    this.saldoDevido = saldo;
    this.primaryStage = stage;
    this.caminhoArquivoLocal = "encomendas_" + nomeEstudante.replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
    if (encomendas == null) {
        encomendas = FXCollections.observableArrayList();
    }
    loadEncomendas(); // Reload data when student is set
    listaEncomendas.setItems(encomendas);

    listaEncomendas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        boolean isSelected = newSelection != null;
        encomendaRecebidaButton.setDisable(!isSelected);
    });
}



@FXML
public void refreshEncomendas() {
    loadEncomendas();
    listaEncomendas.refresh();
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
        Set<String> uniqueEntries = new HashSet<>();
        // Only load if caminhoArquivoLocal is set
        if (caminhoArquivoLocal != null && Files.exists(Paths.get(caminhoArquivoLocal))) {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivoLocal), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (linha.contains("Estudante: " + nomeEstudante) && uniqueEntries.add(linha)) {
                    encomendas.add(linha);
                    System.out.println("Loaded local: " + linha);
                }
            }
        }
        if (Files.exists(Paths.get(CAMINHO_ARQUIVO_REPORTADO))) {
            List<String> linhas = Files.readAllLines(Paths.get(CAMINHO_ARQUIVO_REPORTADO), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (linha.contains("Estudante: " + nomeEstudante) && uniqueEntries.add(linha)) {
                    encomendas.add(linha);
                    System.out.println("Loaded external: " + linha);
                }
            }
        }
        if (encomendas.isEmpty() && nomeEstudante != null) {
            String dataRegistro = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String[] sampleOrders = {
                "ID: ENC001 | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + " | Fonte: Local",
                "ID: ENC002 | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + " | Fonte: Externa: DHL (ID: 12345)"
            };
            for (String order : sampleOrders) {
                if (uniqueEntries.add(order)) {
                    encomendas.add(order);
                }
            }
            if ("Clausemen Custodio Nanro".equals(nomeEstudante)) {
                String[] specificOrders = {
                    "ID: ENC003 | Estudante: Clausemen Custodio Nanro | Registrado em: " + dataRegistro + " | Fonte: Local",
                    "ID: ENC004 | Estudante: Clausemen Custodio Nanro | Registrado em: " + dataRegistro + " | Fonte: Externa: FedEx (ID: 67890)"
                };
                for (String order : specificOrders) {
                    if (uniqueEntries.add(order)) {
                        encomendas.add(order);
                    }
                }
            } else if ("Ana Silva".equals(nomeEstudante)) {
                String order = "ID: ENC005 | Estudante: Ana Silva | Registrado em: " + dataRegistro + " | Fonte: Local";
                if (uniqueEntries.add(order)) {
                    encomendas.add(order);
                }
            } else if ("Joao Maravilhoso".equals(nomeEstudante)) {
                String order = "ID: ENC006 | Estudante: Joao Maravilhoso | Registrado em: " + dataRegistro + " | Fonte: Externa: UPS (ID: 54321)";
                if (uniqueEntries.add(order)) {
                    encomendas.add(order);
                }
            }
            saveToFileLocal();
        }
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Load error: " + e.getMessage());
        Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao carregar as encomendas: " + e.getMessage());
        alert.showAndWait();
    }
}
    private void startFileWatcher() {
    new Thread(() -> {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(CAMINHO_ARQUIVO_REPORTADO).getParent();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals("encomendas_reportadas.csv")) {
                        Platform.runLater(this::refreshEncomendas);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
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