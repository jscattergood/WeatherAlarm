/*
 * Copyright 2015 John Scattergood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package weatherAlarm.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.*;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherDataEnum;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Properties;


/**
 * This class is responsible for performing email notifications
 *
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 12/30/2014
 */
public class EmailNotificationHandler extends AbstractNotificationHandler {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationHandler.class);
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_USER = "mail.smtp.user";
    private static final String MAIL_SMTP_AUTH_PASS = "mail.smtp.authPass";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_PORT_VALUE = "587";
    private static final String MAIL_SMTP_AUTH_VALUE = "true";
    private static final String TRANSPORT_PROTOCOL = "smtps";
    private String emailHostName;
    private String emailAuthUser;
    private String emailAuthPass;

    public EmailNotificationHandler(EventStream stream) {
        super(stream);

        final String emailHostName = System.getProperty("weatherAlarm.emailHostName");
        if (emailHostName == null) {
            logger.error("No emailHostName defined. Cannot send notifications...");
            return;
        }
        final String emailAuthUser = System.getProperty("weatherAlarm.emailAuthUser");
        if (emailAuthUser == null) {
            logger.error("No emailAuthUser email defined. Cannot send notifications...");
            return;
        }
        final String emailAuthPass = System.getProperty("weatherAlarm.emailAuthPass");
        if (emailAuthPass == null) {
            logger.error("No emailAuthPass defined. Cannot send notifications...");
            return;
        }

        this.emailHostName = emailHostName;
        this.emailAuthUser = emailAuthUser;
        this.emailAuthPass = emailAuthPass;
    }

    @Override
    protected Func1<? super FilterMatchEvent, ? extends Observable<IModuleEvent>> sendNotification() {
        return event -> {
            if (emailHostName == null || emailHostName.isEmpty()) {
                return Observable.just(new NotificationNotSentEvent("Email configuration is undefined"));
            }
            WeatherAlarm alarm = event.getAlarm();
            try {
                String host = emailHostName;
                String authUser = emailAuthUser;
                String authPass = emailAuthPass;
                Properties props = System.getProperties();
                props.put(MAIL_SMTP_HOST, host);
                props.put(MAIL_SMTP_USER, authUser);
                props.put(MAIL_SMTP_AUTH_PASS, authPass);
                props.put(MAIL_SMTP_PORT, MAIL_SMTP_PORT_VALUE);
                props.put(MAIL_SMTP_AUTH, MAIL_SMTP_AUTH_VALUE);

                Session session = Session.getInstance(props);

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(authUser));

                message.addRecipient(Message.RecipientType.TO, new InternetAddress(alarm.getEmailAddress()));
                message.setSubject("Weather Alarm!");
                message.setText("Criteria - " + alarm.getCriteria(WeatherDataEnum.TEMPERATURE).getValue() + "\n" +
                        "Current Conditions - " + event.getConditions().getTemperature());

                Transport transport = session.getTransport(TRANSPORT_PROTOCOL);
                try {
                    transport.connect(host, authUser, authPass);
                    transport.sendMessage(message, message.getAllRecipients());
                } finally {
                    transport.close();
                }

            } catch (MessagingException e) {
                logger.error("Exception creating email", e);
                return Observable.just(new NotificationNotSentEvent(e.getMessage()));
            }
            return Observable.just(new NotificationSentEvent(event.getAlarm(), Instant.now()));
        };
    }
}
