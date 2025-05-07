package Packages;
import java.util.HashMap;

public class PaqueteUsuariosActivos {
    // Map<connectionId, nombreUsuario>
    public HashMap<Integer, String> usuarios;
    
    /** Constructor por defecto requerido por Kryo */
    public PaqueteUsuariosActivos() {
        this.usuarios = new HashMap<>();
    }

    // Constructor de conveniencia
    public PaqueteUsuariosActivos(HashMap<Integer, String> usuarios) {
        this.usuarios = usuarios;
    }
}