package es.ieslosmontecillos.appinformes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;


public class AppInformes extends Application {
    public static Connection conexion = null;
    @javafx.fxml.FXML
    private TextField clientNumber;


    @Override
    public void start(Stage primaryStage) {
        //establecemos la conexión con la BD
        conectaBD();
        //Creamos la escena
        try {
            FXMLLoader LOADER = new FXMLLoader(getClass().getResource("appinformes.fxml"));

            Parent root = LOADER.load();
            Scene scene = new Scene(root, 600, 600);

            primaryStage.setTitle("AppInformes");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(AppInformes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void stop() throws Exception {
        try {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/SampleDB;shutdown=true");
        } catch (Exception ex) {
            System.out.println("No se pudo cerrar la conexion a la BD");
            ex.printStackTrace();
        }
    }
    public void conectaBD(){
        //Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost:9001/test";
        String usuario = "sa";
        String clave = "";
        try{
            //Class.forName("org.hsqldb.jdbcDriver").newInstance();
            Class.forName("org.hsqldb.jdbcDriver");
            conexion = DriverManager.getConnection(baseDatos,usuario,clave);
        }
        catch (ClassNotFoundException cnfe){
            System.err.println("Fallo al cargar JDBC");
            cnfe.printStackTrace();
            System.exit(1);
        }
        catch (SQLException sqle){
            System.err.println("No se pudo conectar a BD");
            sqle.printStackTrace();
            System.exit(1);
        }
        catch (Exception ex){
            System.err.println("Imposible Conectar");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void generaInforme(String nombre) {
        try {
            JasperReport jr = (JasperReport)JRLoader.loadObject(getClass().getResource(nombre));
            Map<String, Object> parametros = new HashMap();
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaInforme(String nombre, Map<String, Object> parametros) {
        try {
            JasperReport jr = (JasperReport)JRLoader.loadObject(getClass().getResource(nombre));
            JasperPrint jp = JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaSubinforme() {
        try {
            JasperReport jr = (JasperReport)JRLoader.loadObject(getClass().getResource("ListadoFactura.jasper"));
            JasperReport jsr = (JasperReport)JRLoader.loadObject(getClass().getResource("SubinformeFactura.jasper"));

            Map parametros = new HashMap();
            parametros.put("subReportParameter", jsr);
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @javafx.fxml.FXML
    public void FacturasPorCliente(ActionEvent actionEvent) {
        try {
            if (clientNumber.getText() != null) {
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("IDENTIFICADOR", Integer.valueOf(clientNumber.getText()));
                generaInforme("FacturaPorClientes.jasper", parametros);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @javafx.fxml.FXML
    public void VentasTotales(ActionEvent actionEvent) {
        try {
            generaInforme("VentasTotales.jasper");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @javafx.fxml.FXML
    public void ListadoFacturas(ActionEvent actionEvent) {
        try {
            generaInforme("facturas.jasper");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @javafx.fxml.FXML
    public void SubinformeListadoFacturas(ActionEvent actionEvent) {
        try {
            generaSubinforme();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}