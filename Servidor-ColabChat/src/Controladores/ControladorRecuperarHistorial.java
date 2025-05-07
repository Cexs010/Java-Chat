package controladores;

import com.esotericsoftware.kryonet.Connection;
import modelos.RecuperarMensajes;
import Packages.PaqueteHistorial;
import java.util.List;
import java.util.Map;

public class ControladorRecuperarHistorial {

    public static void procesarHistorial(java.sql.Connection conn, Connection cliente) {
        RecuperarMensajes recuperar = new RecuperarMensajes();
        List<Map<String, Object>> mensajes = recuperar.obtenerMensajesUltimos30Dias(conn);
        //log
        System.out.println("[Servidor] Se recuperaron " + mensajes.size() + " mensajes del historial.");

        PaqueteHistorial paquete = new PaqueteHistorial();
        paquete.mensajes = mensajes;

        cliente.sendTCP(paquete);
    }
}