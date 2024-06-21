package me.podsialdy.api.Utils;


public interface EmailContent {

    public static final String VALIDATION_CODE_SUBJECT = "Code de validation";
    public static final String VALIDATION_CODE_MESSAGE = "<html><body>"
                    + "<h2 style='color: #fcb974;'>Votre code de validation</h2>"
                    + "<p>Une tentative de connexion à votre compte a été détectée.</p>"
                    + "<p>Utilisez le code suivant pour valider votre connexion :</p>"
                    + "<h3 style='background-color: #fcb974; color: white; padding: 10px; border-radius: 5px; display: inline-block;'>"
                    + "%s" + "</h3>"
                    + "<p>Si ce n'était pas vous, veuillez sécuriser votre compte immédiatement.</p>"
                    + "<p>Cordialement,<br>Votre équipe de sécurité</p>"
                    + "</body></html>";
}