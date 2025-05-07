package modelos;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionBD {                         //localhost:3306 para wampp
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/collabchat";
    private static final String USUARIO = "root";  // usuario
    private static final String CONTRASENA = "";  // contraseña

    public Connection conectarBD() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexion exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: no se encontró el driver JDBC.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos.");
        }

        return conexion;
    }
}
