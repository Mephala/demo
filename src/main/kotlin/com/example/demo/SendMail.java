package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {

  public static void main(String[] args) {
    try {
      sendo();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static void sendo() throws MessagingException, IOException {
    Properties prop = new Properties();
    prop.put("mail.smtp.auth", true);
    prop.put("mail.smtp.starttls.enable", "true");
    prop.put("mail.smtp.host", "127.0.0.1");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.ssl.trust", "127.0.0.1");
    prop.put("mail.smtp.connectiontimeout", "10000");
    prop.put("mail.smtp.timeout", "10000");

    Session session = Session.getInstance(prop, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("c09HWExVVGxUUXRCbWw2QXdtTGRiWlV6QkVxc0I1dG5FVVZNT2lObm11cz0=", "VmUvVjBCNTRObFZ2S09Yak5Mcm9pSHlUcWNNdEtJaWVHeVFFYnVZYUgvVT0=");
      }
    });

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("gokhan.ozgozen@gmail.com"));
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse("gokhan.ozgozen@gmail.com"));
    message.setSubject("Mail Subject");

    String msg = "Test";

    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(msg, "text/html");

    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
    attachmentBodyPart.attachFile(new File("/home/gokhanozg/p/ssp55/e2e-test/pdfs/Customer_2019-05-17_14132887.pdf"));


    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);
    multipart.addBodyPart(attachmentBodyPart);

    message.setContent(multipart);

    Transport.send(message);

  }

}
