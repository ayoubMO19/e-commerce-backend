package com.vexa.ecommerce.Utils;

public class SecurityUtils {

    // Función para ocultar parte de un email por seguridad
    public static String hideSecureEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        int arrobaIndex = email.indexOf('@');
        if (arrobaIndex == -1) {
            return email; // No es un email válido
        }

        String usuario = email.substring(0, arrobaIndex);
        String dominio = email.substring(arrobaIndex + 1);

        // Mostrar solo los primeros 3 caracteres del usuario y los primeros 3 del dominio
        String partialUser = usuario.substring(0, Math.min(usuario.length(), 3)) + "...";
        String partialDomain = dominio.substring(0, Math.min(dominio.length(), 3)) + "...";

        return partialUser + "@" + partialDomain; // Salida: usu...@dom...
    }

    // Función para ocultar parte de una key por seguridad
    public static String maskKey(String key) {
        if (key == null) return null;
        if (key.length() < 10) return "********";
        return key.substring(0, 4) + "..." + key.substring(key.length() - 4);
    }
}
