/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import entities.Offre;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import services.CategorieService;
import services.OffreService;
import util.MyDB;
import util.SessionManager;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class AddOffreController_1 implements Initializable {

    @FXML
    private TextField tfDesc;
    @FXML
    private TextField tfPrix;
    @FXML
    private TextField tfNbp;
    @FXML
    private Button imageUpload;
    @FXML
    private ComboBox<String> tfCat;
    @FXML
    private Button btn_ajoutoff;

    private String lien;
    private File file;
    @FXML
    private Button MesOffA;
    private Button MesreservationA;
    @FXML
    private VBox postsContainer;
    @FXML
    private Button btnRetourA;
    @FXML
    private Button MesressA;
    private SessionManager session;
    @FXML
    private Button utilisateur;
    @FXML
    private Button plan;
    @FXML
    private Button cat;
    @FXML
    private Button act;
    @FXML
    private Button comm;
    @FXML
    private Button part;
    @FXML
    private Button evente;

    public TextField getTfDesc() {
        return tfDesc;
    }

    public void setTfDesc(String tfDesc) {
        this.tfDesc.setText(tfDesc);
    }

    public TextField getTfPrix() {
        return tfPrix;
    }

    public void setTfPrix(Float tfPrix) {
        this.tfPrix.setText(String.valueOf(tfPrix));
    }

    public TextField getTfNbp() {
        return tfNbp;
    }

    public void setTfNbp(TextField tfNbp) {
        this.tfNbp = tfNbp;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection cnx = MyDB.getInstance().getCnx();
            String query = "SELECT nom FROM categorie_activite";
            PreparedStatement pst = cnx.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String categoryName = rs.getString("nom");
                tfCat.getItems().add(categoryName);
            }
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void PhotoImportation(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG
                = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter extFilterjpg
                = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG
                = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter extFilterpng
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters()
                .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng);
        //Show open file dialog
        file = fileChooser.showOpenDialog(null);

        try {
            BufferedImage image = ImageIO.read(file);
            WritableImage imagee = SwingFXUtils.toFXImage(image, null);

            try {
                // save image to PNG file
                this.lien = UUID.randomUUID().toString();
                File f = new File("src\\img\\" + this.lien + ".png");
                System.out.println(f.toURI().toString());
                ImageIO.write(image, "PNG", f);
                imageUpload.setText(file.getName());

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
//      
    }

    @FXML

    private void AjouterOff(ActionEvent event) throws SQLException {
        // Validate form input
        if (tfDesc.getText().isEmpty() || tfPrix.getText().isEmpty() || tfNbp.getText().isEmpty() || tfCat.getValue() == null || file == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et importer une image !");
            alert.showAndWait();
            return;
        }

        if (tfDesc.getText().length() < 10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("La description doit comporter au moins 10 caractères !");
            alert.showAndWait();
            return;
        }

        try {
            CategorieService cs = new CategorieService();
            int id_user = session.getId(); // replace this with the actual user ID
            int id_category = cs.recupererBynom(tfCat.getValue()).getId();
            LocalDateTime now = LocalDateTime.now();
            java.sql.Date date = java.sql.Date.valueOf(now.toLocalDate());
            String description = tfDesc.getText();
            float prix = Float.parseFloat(tfPrix.getText());
            int nb_place = Integer.parseInt(tfNbp.getText());
            Offre o = new Offre(id_user, id_category, nb_place, date, lien + ".png", description, prix);
            OffreService oc = new OffreService();
            oc.ajouter(o);

            // Display success message and return to previous page
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Offre ajoutée");
            alert.setHeaderText(null);
            alert.setContentText("L'offre a été ajoutée avec succès !");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListOffreBack.fxml"));

                Parent root = loader.load();
                btn_ajoutoff.getScene().setRoot(root);
            } catch (IOException ex) {
                Logger.getLogger(espaceUserController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NumberFormatException ex) {
            // Display error message if price or number of places are not valid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Le prix et le nombre de places doivent être des nombres !");
            alert.showAndWait();
        }
    }

//    private void MesOffresA(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListOffreBack.fxml"));
//
//            Parent root = loader.load();
//            MesOffA.getScene().setRoot(root);
//        } catch (IOException ex) {
//            Logger.getLogger(espaceUserController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    private void MesreseA(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListOffreBack.fxml"));
//
//            Parent root = loader.load();
//            MesreservationA.getScene().setRoot(root);
//        } catch (IOException ex) {
//            Logger.getLogger(espaceUserController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    @FXML
    private void RetourHandleButtonA(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListOffreBack.fxml"));

            Parent root = loader.load();
            btnRetourA.getScene().setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(espaceUserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleClicks(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();

        if (clickedButton.getId().equals("MesOffA")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOffreBack.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) MesOffA.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else if (clickedButton.getId().equals("utilisateur")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) utilisateur.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (clickedButton.getId().equals("plan")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminActivite.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) plan.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (clickedButton.getId().equals("cat")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admi_categ.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) cat.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (clickedButton.getId().equals("act")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("afficher_Act.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) act.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (clickedButton.getId().equals("comm")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("afficher_Com.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) comm.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (clickedButton.getId().equals("MesressA")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListResBack.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) MesressA.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        else if (clickedButton.getId().equals("evente")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) evente.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
             else if (clickedButton.getId().equals("part")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) evente.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

}
