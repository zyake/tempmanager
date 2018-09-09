package tempmanager.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailSender {

    public void sendEmail(Properties serverConfig, String to, String mailAddress, String password, String subject, String text) {
        //Establishing a session with required user details
        Session session = Session.getInstance(serverConfig, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAddress, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            InternetAddress[] address = InternetAddress.parse(to, true);
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(text);
            msg.setHeader("XPriority", "1");

            Transport.send(msg);
        } catch (MessagingException mex) {
            throw new RuntimeException(mex);
        }
    }
}
