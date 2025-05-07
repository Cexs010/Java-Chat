package Packages;

import java.io.Serializable;
import java.util.Date;

public class PaqueteConfirmacion implements Serializable {
    public boolean exito;
    public int idMensaje;
    public String mensajeError;
    public Date fecha;
}