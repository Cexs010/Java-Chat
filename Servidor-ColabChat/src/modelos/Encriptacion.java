package modelos;

import java.security.NoSuchAlgorithmException;

public class Encriptacion {
    /**
     * Encriptación SHA-256 para seguridad de contraseñas
     * @param texto
     * @return true si la autenticación es exitosa, false en caso contrario
     */
    public static String encriptarSHA256(String texto) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }
}