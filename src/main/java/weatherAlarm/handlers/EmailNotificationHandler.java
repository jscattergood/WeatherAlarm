/*
 * Copyright 2019 John Scattergood
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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.*;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherDataEnum;
import weatherAlarm.services.IConfigService;

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
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 12/30/2014
 */
@Singleton
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

    @Inject
    public EmailNotificationHandler(IEventStream stream, IConfigService configService) {
        super(stream);

        final String emailHostName = configService.getConfigValue(IConfigService.CONFIG_EMAIL_HOST_NAME);
        if (emailHostName == null) {
            logger.error("No emailHostName defined. Cannot send notifications...");
            return;
        }
        final String emailAuthUser = configService.getConfigValue(IConfigService.CONFIG_EMAIL_AUTH_USER);
        if (emailAuthUser == null) {
            logger.error("No emailAuthUser email defined. Cannot send notifications...");
            return;
        }
        final String emailAuthPass = configService.getConfigValue(IConfigService.CONFIG_EMAIL_AUTH_PASS);
        if (emailAuthPass == null) {
            logger.error("No emailAuthPass defined. Cannot send notifications...");
            return;
        }

        this.emailHostName = emailHostName;
        this.emailAuthUser = emailAuthUser;
        this.emailAuthPass = emailAuthPass;
    }

    @Override
    protected Func1<? super FilterMatchEvent, ? extends Observable<IEvent>> sendNotification() {
        return event -> {
            WeatherAlarm alarm = event.getAlarm();
            if (emailHostName == null || emailHostName.isEmpty()) {
                return Observable.just(new NotificationNotSentEvent(alarm, "Email configuration is undefined"));
            }
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
                message.setText("Criteria - " + alarm.getCriteriaFor(WeatherDataEnum.TEMPERATURE).getValue() + "\n" +
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
                return Observable.just(new NotificationNotSentEvent(alarm, e.getMessage()));
            }
            return Observable.just(new NotificationSentEvent(alarm, Instant.now()));
        };
    }
}
