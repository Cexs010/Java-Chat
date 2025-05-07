package controladores;

import com.esotericsoftware.kryonet.Connection;
import modelos.AutenticacionUsuario;
import Packages.PaqueteAutenticar;

public class ControladorAutenticacion {
    public static void procesarAutenticacion(java.sql.Connection conn, 
                                           Connection cliente, 
                                           PaqueteAutenticar paquete) {
        boolean credencialesValidas = AutenticacionUsuario.autenticar(
            conn, 
            paquete.nombreUsuario, 
            paquete.contraseña
        );
        
        if (credencialesValidas) {
            paquete.autenticado = true;
            paquete.mensajeError = "";
        } else {
            paquete.autenticado = false;
            paquete.mensajeError = "Credenciales inválidas";
        }
        
        cliente.sendTCP(paquete);
    }
}