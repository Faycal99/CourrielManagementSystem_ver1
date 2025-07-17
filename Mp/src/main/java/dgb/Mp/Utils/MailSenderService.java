package dgb.Mp.Utils;

import dgb.Mp.generalAdvice.customException.MailNotSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class MailSenderService {

    private final JavaMailSender mailSender;


    public boolean sendEmail(String toEmail, String username, String password) throws MailException, MessagingException {
        // Create a new email message
        MimeMessage message = mailSender.createMimeMessage();


        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String emailContent = genareteHtmlEmail(username,password);// true enables multipart (for attachments)

        helper.setFrom("spring.mail.username");  // Sender's email
        helper.setTo(toEmail);  // Recipient email
        helper.setSubject("Welcome to Couriel Management !");  // Subject
        helper.setText(emailContent, true);  // true means HTML content
   try{
    // Send the email
    mailSender.send(message);
    return true;

    }catch (MailException e){


       return false;  // Returning false if email sending fails

   }

    }


    public String genareteHtmlEmail(String user, String password) {

      return  "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Welcome to [Your App Name]</title>\n" +
                "\n" +
                "    <!-- Google Fonts -->\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap\" rel=\"stylesheet\">\n" +
                "\n" +
                "    <style>\n" +
                "        /* Reset default styles */\n" +
                "        body, table, td, a {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            font-family: 'Roboto', sans-serif;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            background-color: #f4f4f4;\n" +
                "            color: #333;\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            padding: 40px 0;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .email-container {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .email-header {\n" +
                "            background-color: #3498db;\n" +
                "            color: #fff;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 700;\n" +
                "            border-radius: 8px 8px 0 0;\n" +
                "        }\n" +
                "\n" +
                "        .email-body {\n" +
                "            padding: 20px;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            background-color: #3498db;\n" +
                "            color: white;\n" +
                "            text-decoration: none;\n" +
                "            padding: 12px 24px;\n" +
                "            border-radius: 30px;\n" +
                "            font-size: 16px;\n" +
                "            text-align: center;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            font-size: 12px;\n" +
                "            color: #aaa;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .footer a {\n" +
                "            color: #3498db;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        /* Responsive Design */\n" +
                "        @media screen and (max-width: 600px) {\n" +
                "            .email-container {\n" +
                "                padding: 15px;\n" +
                "            }\n" +
                "\n" +
                "            .email-header {\n" +
                "                font-size: 20px;\n" +
                "            }\n" +
                "\n" +
                "            .email-body {\n" +
                "                padding: 15px;\n" +
                "            }\n" +
                "\n" +
                "            .button {\n" +
                "                width: 100%;\n" +
                "                padding: 14px;\n" +
                "                font-size: 18px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <td class=\"email-container\">\n" +
                "                <div class=\"email-header\">\n" +
                "                    Welcome to Your Courial Management!\n" +
                "                </div>\n" +
                "                <div class=\"email-body\">\n" +
                "                    <p>Hi <strong>{{"+user+"}}</strong>,</p>\n" +
                "                    <p>Thank you for registering with Courial Management. Below are your login credentials:</p>\n" +
                "                    <table style=\"width: 100%; margin-top: 20px;\">\n" +
                "                        <tr>\n" +
                "                            <td style=\"padding: 8px; background-color: #f4f4f4; font-weight: bold;\">Username:</td>\n" +
                "                            <td style=\"padding: 8px; background-color: #f4f4f4;\">{{"+user+"}}</td>\n" +
                " <tr>\n" +
                "                            <td style=\"padding: 8px; background-color: #f4f4f4; font-weight: bold;\">Password:</td>\n" +
                "                            <td style=\"padding: 8px; background-color: #f4f4f4;\">{{"+password+"}}</td>\n" +
                "\n" +
                "                        </tr>\n" +
                "        \n"
              ;


    }




}
