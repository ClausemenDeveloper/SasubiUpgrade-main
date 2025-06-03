package Transportadora.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Transportadora.model.Entrega;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TransportadoraController {

    @FXML
    private ListView<String> listaEntregas;
    @FXML
    private TextField inputEstudante;
    @FXML
    private TextField inputTransportadoraId;
    @FXML
    private Button enviarButton;
    @FXML
    private Button cancelarButton;

    private ObservableList<String> entregasDisplay;
    private List<Entrega> entregas;
    private Stage primaryStage;
    private static final String CAMINHO_ARQUIVO_LOCAL = "entregas_transportadora.csv";
    private static final String CAMINHO_ARQUIVO_SASUBI = "encomendas_reportadas.csv"; // Same as in EncomendasController

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        entregas = new ArrayList<>();
        entregasDisplay = FXCollections.observableArrayList();
        listaEntregas.setItems(entregasDisplay);
        loadEntregas();

        // Enable buttons only when an item is selected
        listaEntregas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            enviarButton.setDisable(!isSelected);
            cancelarButton.setDisable(!isSelected);
        });
    }

    @FXML
    public void adicionarEntrega() {
        String nomeEstudante = inputEstudante.getText().trim();
        String transportadoraId = inputTransportadoraId.getText().trim();

        if (nomeEstudante.isEmpty() || transportadoraId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, preencha todos os campos!");
            alert.showAndWait();
            return;
        }

        // Generate a unique ID for the delivery
        String id = "ENT" + (entregas.size() + 1);
        Entrega entrega = new Entrega(id, nomeEstudante, transportadoraId);
        entregas.add(entrega);
        entregasDisplay.add(entrega.toString());
        saveEntregasLocal();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Entrega adicionada com sucesso!");
        alert.showAndWait();

        inputEstudante.clear();
        inputTransportadoraId.clear();
    }

    @FXML
    public void enviarEntrega() {
        String selected = listaEntregas.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Entrega entrega = findEntregaByDisplayString(selected);
            if (entrega == null || entrega.getStatus().equals("Enviada")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Entrega já enviada ou inválida!");
                alert.showAndWait();
                return;
            }

            entrega.setStatus("Enviada");
            // Update the display
            int index = listaEntregas.getSelectionModel().getSelectedIndex();
            entregasDisplay.set(index, entrega.toString());

            // Save to sasubiupgrade's encomendas_reportadas.csv
            try {
                Files.write(Paths.get(CAMINHO_ARQUIVO_SASUBI), 
                    (entrega.toSasubiFormat() + "\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao enviar entrega para o sistema SASUBI: " + e.getMessage());
                alert.showAndWait();
                return;
            }

            saveEntregasLocal();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Entrega enviada com sucesso!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma entrega para enviar!");
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelarEntrega() {
        String selected = listaEntregas.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Entrega entrega = findEntregaByDisplayString(selected);
            if (entrega == null || entrega.getStatus().equals("Cancelada")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Entrega já cancelada ou inválida!");
                alert.showAndWait();
                return;
            }

            entrega.setStatus("Cancelada");
            int index = listaEntregas.getSelectionModel().getSelectedIndex();
            entregasDisplay.set(index, entrega.toString());
            saveEntregasLocal();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Entrega cancelada com sucesso!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma entrega para cancelar!");
            alert.showAndWait();
        }
    }

    @FXML
    public void voltarParaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sasubiupgrade/view/StudentLoginView.fxml"));
            Parent pagina = loader.load();
            Scene scene = new Scene(pagina);
            primaryStage.setTitle("Sistema de Pagamentos - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao voltar para o login: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadEntregas() {
        try {
            if (Files.exists(Paths.get(CAMINHO_ARQUIVO_LOCAL))) {
                List<String> linhas = Files.readAllLines(Paths.get(CAMINHO_ARQUIVO_LOCAL), StandardCharsets.UTF_8);
                for (String linha : linhas) {
                    if (!linha.trim().isEmpty()) {
                        String[] partes = linha.split(" \\| ");
                        if (partes.length == 5) {
                            String id = partes[0].replace("ID: ", "").trim();
                            String nome = partes[1].replace("Estudante: ", "").trim();
                            String data = partes[2].replace("Registrado em: ", "").trim();
                            String status = partes[3].replace("Status: ", "").trim();
                            String transportadoraId = partes[4].replace("Transportadora ID: ", "").trim();

                            Entrega entrega = new Entrega(id, nome, transportadoraId);
                            entrega.setStatus(status);
                            entregas.add(entrega);
                            entregasDisplay.add(entrega.toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao carregar entregas: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void saveEntregasLocal() {
        try {
            List<String> linhas = new ArrayList<>();
            for (Entrega e : entregas) {
                linhas.add(e.toString());
            }
            Files.write(Paths.get(CAMINHO_ARQUIVO_LOCAL), linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar entregas: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private Entrega findEntregaByDisplayString(String displayString) {
        for (Entrega entrega : entregas) {
            if (entrega.toString().equals(displayString)) {
                return entrega;
            }
        }
        return null;
    }
}