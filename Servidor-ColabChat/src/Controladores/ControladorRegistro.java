package controladores;
import com.esotericsoftware.kryonet.Connection;
import modelos.RegistrarUsuario;
import Packages.PaqueteRegistro;
import Packages.RespuestaRegistro;

public class ControladorRegistro {
    public static void procesarRegistro(java.sql.Connection conn, Connection cliente, PaqueteRegistro packet){
        boolean exito = RegistrarUsuario.registrar
        (conn, packet.nombreUsuario, packet.contraseña, packet.edad, packet.ciudadNacimiento);
        
        RespuestaRegistro respuesta = new RespuestaRegistro();
        respuesta.respuesta = exito;
        respuesta.mensaje = exito ? "Registro exitoso" : "Error al registrarse";
        
        cliente.sendTCP(respuesta);
    }
}

