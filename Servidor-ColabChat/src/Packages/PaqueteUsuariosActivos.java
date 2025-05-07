package Packages;
import java.util.HashMap;

public class PaqueteUsuariosActivos {
    // Map<connectionId, nombreUsuario>
    public HashMap<Integer, String> usuarios;

    // Constructor de conveniencia
    public PaqueteUsuariosActivos(HashMap<Integer, String> usuarios) {
        this.usuarios = usuarios;
    }
}