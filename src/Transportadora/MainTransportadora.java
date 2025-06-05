/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Transportadora;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author clausemen-custodio-nanro
 */
public class MainTransportadora extends Application{
    public void start(Stage primaryStage) throws Exception {
        // Carregar a tela de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transportadora/view/TransportadoraView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);//root
        primaryStage.setTitle("Sistema de Pagamentos - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);//mostra
        
    }
}
