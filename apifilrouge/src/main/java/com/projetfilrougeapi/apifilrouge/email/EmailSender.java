
package com.projetfilrougeapi.apifilrouge.email;

import com.projetfilrougeapi.apifilrouge.endpoint_api.event.Event;
import com.projetfilrougeapi.apifilrouge.endpoint_api.user.User;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.StringWriter;

import java.util.Date;
import java.util.Properties;

@Service
public class EmailSender {
    // Configuration des paramètres SMTP
    @Value("${email.smtp.host:smtp.gmail.com}")
    private String host;

    @Value("${email.smtp.port:587}")
    private String port;

    @Value("${email.smtp.username}")
    private String username;

    @Value("${email.smtp.password}")
    private String password;

    @Value("${email.from.address}")
    private String fromAddress;

    // Créer une nouvelle instance d'email pour chaque envoi avec configuration complète
    private HtmlEmail createEmail() throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(host);
        email.setSmtpPort(Integer.parseInt(port));
        email.setAuthentication(username, password);

        // Configuration SSL/TLS complète
        email.setStartTLSEnabled(true);
        email.setSSLOnConnect(false); // Pour STARTTLS
        email.setSSLCheckServerIdentity(true);

        // Configuration du charset
        email.setCharset("UTF-8");

        return email;
    }

    public void sendInvitationEmail(User sender, User receiver, Event event) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("nomEvenement", event.getName());
        context.put("nom", sender.getLastName());
        context.put("prenom", sender.getFirstName());
        context.put("emailExpediteur", sender.getEmail());
        context.put("dateEnvoi", new Date().getDay() + "/" + new Date().getMonth() + "/" + new Date().getYear());

        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/emailTemplate.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        try {
            // Configuration de l'email avec une nouvelle instance
            HtmlEmail email = createEmail();
            email.setFrom(fromAddress);
            email.setSubject("Invitation concernant l'événement : " + event.getName());
            email.setHtmlMsg(writer.toString());
            email.addTo(receiver.getEmail());

            // Envoi de l'email
            email.send();
        } catch (EmailException e) {
            throw new Exception("Erreur lors de l'envoi de l'email d'invitation: " + e.getMessage(), e);
        }
    }

    public void sendIUpdateEventEmail(User receiver, Event event) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("nomEvenement", event.getName());
        context.put("dateEvenement", event.getDate().getDayOfMonth() + "/" + event.getDate().getMonth() + "/" + event.getDate().getYear());
        context.put("emplacementEvenement", event.getPlace().getAddress());
        context.put("dateNotification", new Date().getDay() + "/" + new Date().getMonth() + "/" + new Date().getYear());

        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/emailTemplateUpdateEvent.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        try {
            // Configuration de l'email avec une nouvelle instance
            HtmlEmail email = createEmail();
            email.setFrom(fromAddress);
            email.setSubject("Mise à jour concernant l'événement : " + event.getName());
            email.setHtmlMsg(writer.toString());
            email.addTo(receiver.getEmail());

            System.out.println("Email envoyé à : " + receiver.getEmail());

            // Envoi de l'email
            email.send();
        } catch (EmailException e) {
            throw new Exception("Erreur lors de l'envoi de l'email de mise à jour: " + e.getMessage(), e);
        }
    }

    public void sendWelcomeEmail(User newAccount) throws Exception {
        // Configuration de Velocity
        VelocityEngine ve = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(props);

        // Création du contexte Velocity
        VelocityContext context = new VelocityContext();
        context.put("email", newAccount.getEmail());
        context.put("nom", newAccount.getLastName());
        context.put("prenom", newAccount.getFirstName());
        context.put("pseudo", newAccount.getPseudo());

        // Chargement et rendu du template
        Template template = ve.getTemplate("templates/welcomeMailTemplate.vm", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        try {
            // Configuration de l'email avec une nouvelle instance
            HtmlEmail email = createEmail();
            email.setFrom(fromAddress);
            email.setSubject("Création de votre compte sur notre plateforme");
            email.setHtmlMsg(writer.toString());
            email.addTo(newAccount.getEmail());

            // Envoi de l'email
            email.send();
        } catch (EmailException e) {
            throw new Exception("Erreur lors de l'envoi de l'email de bienvenue: " + e.getMessage(), e);
        }
    }
}