package sasubiupgrade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import sasubiupgrade.model.Gestor;

public class GestorLoginController {

    @FXML
    private TextField inputUsername;

    @FXML
    private PasswordField inputPassword;

    private Stage primaryStage;
    private String nomeEstudante;
    private double saldoDevido;
    private static final String CAMINHO_ARQUIVO = "gestores.csv";

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setEstudanteLogado(String nome, double saldo) {
        this.nomeEstudante = nome;
        this.saldoDevido = saldo;
    }

    @FXML
    public void initialize() {
        // No additional initialization needed
    }

    @FXML
    public void login() {
        String username = inputUsername.getText().trim();
        String password = inputPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, preencha todos os campos!");
            alert.showAndWait();
            return;
        }

        try {
            if (Gestor.autenticar(username, password, CAMINHO_ARQUIVO)) {
                // Successful login, navigate to GestorTransportadoraView
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sasubiupgrade/view/GestorTransportadoraView.fxml"));
                Parent pagina = loader.load();
                Scene scene = new Scene(pagina);
                primaryStage.setTitle("Sistema de Pagamentos - Gestor Transportadora");
                primaryStage.setScene(scene);
                primaryStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Credenciais inválidas!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao autenticar: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML

public void voltarMain() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sasubiupgrade/view/MainView.fxml"));
        Parent pagina = loader.load();
        MainController controller = loader.getController();
        controller.setStage(primaryStage);
        if (nomeEstudante != null) {
            controller.setEstudanteLogado(nomeEstudante, saldoDevido);
        } else {
            controller.setEstudanteLogado("Gestor", 0.0); // Fallback for gestor context
        }
        Scene scene = new Scene(pagina);
        primaryStage.setTitle("Sistema de Pagamentos - Menu Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao voltar para a tela principal: " + e.getMessage());
        alert.showAndWait();
    }
}
}