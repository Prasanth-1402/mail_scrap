/**
 * 
 * @author PRASANTH
 *
 */
package com.scrap.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;

import org.springframework.beans.factory.annotation.Autowired;

import com.scrap.data.DbQueries;
import com.scrap.dto.IncomingMailDto;
/**
 * @author PRASANTH
 *
 * 9:44:35 PM
 */
public class ScrapApp {
	
	@Autowired
	DbQueries saveToDb;
		
	private Properties getServerProperties(String protocol, String host,String port) {
			Properties properties = new Properties();
			properties.put(String.format("mail.%s.host", protocol), host);
			properties.put(String.format("mail.%s.port", protocol), port);
			properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),"javax.net.ssl.SSLSocketFactory");
			properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol),"false");
			properties.setProperty(String.format("mail.%s.socketFactory.port", protocol),String.valueOf(port));	
			return properties;
		}
		
	public void getIncomingMails(String protocol, String host, String port,	String userName, String password) {
		Properties properties = getServerProperties(protocol, host, port);
		Session session = Session.getDefaultInstance(properties);
		try {
			Store store = session.getStore(protocol);
			store.connect(userName, password);
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);
			Message[] messages = folderInbox.getMessages();
			for (int i = 0; i < messages.length; i++) {
				Message msg = messages[i];
				Address[] fromAddress = msg.getFrom();
				String from = fromAddress[0].toString();
				String subject = msg.getSubject();
				
				Flags flag = msg.getFlags();
				String allSysFlag ="", allUserFlags = "";
				
				//Adding "Ecom-Receipt" label  to the transactional emails
				if((subject.startsWith("You Order") && subject.contains("flipkart.com")  && subject.endsWith("Successfully Placed")) || 
						(subject.contains("You Amazon") && subject.contains("payments-update@Amazon") ) ) 
							allUserFlags += "Ecom-Receipt";
				
				for(int k = 0; k < flag.getSystemFlags().length; k++) 
					allSysFlag +=  flag.getSystemFlags()[k].toString()+",";
				
				for(int k = 0; k < flag.getUserFlags().length; k++) 
					allUserFlags += flag.getUserFlags()[k].toString()+",";
				
				String userFlags = allUserFlags.substring(0, allUserFlags.length() - 1);
				String SysFlags = allSysFlag.substring(0, allSysFlag.length() - 1);
				
				String attachment = msg.getFileName();
				String toList = parseAddresses(msg.getRecipients(Message.RecipientType.TO));
				String ccList = parseAddresses(msg.getRecipients(Message.RecipientType.CC));
				Date sentDate = msg.getSentDate();
				String contentType = msg.getContentType();
				String messageContent = "";
				IncomingMailDto mailSaverDto = new IncomingMailDto();
				if (contentType.contains("text/plain") ||contentType.contains("text/html") || contentType.contains("multipart")) {
					try {
							Object content = msg.getContent();
							if (content != null) 
									messageContent = content.toString();
							  if (contentType.contains("multipart") && attachment!= null) {
				                    Multipart multiPart = (Multipart) content;
				                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(0);   // going with reading only 1st attachment since I was given restriction to use any cloud providers  for uploading and saving the link in DB. Having BLOB with more than 1 value is a poor design.
										  byte[] attachments = new byte[10 * 1024 * 1024];
				                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
				                        	InputStream is = part.getInputStream();
				                            ByteArrayOutputStream os = new ByteArrayOutputStream();
				                            byte[] buf = new byte[10 * 1024 * 1024];
				                            int bytesRead;
				                            while ((bytesRead = is.read(buf)) != -1) {
				                                os.write(buf, 0, bytesRead);
				                            }
				                            os.close();
				                            attachments = os.toByteArray();
											mailSaverDto = new IncomingMailDto(from, toList, ccList, sentDate, messageContent, attachments, userFlags, SysFlags);
				                        } else {
				                            messageContent = part.getContent().toString();
											mailSaverDto = new IncomingMailDto(from, toList, ccList, sentDate, messageContent, null, userFlags, SysFlags);
				                        }
				                }
							saveToDb.saveIncomingMail(mailSaverDto);
					}catch (Exception ex) {
								messageContent = "[Error downloading content]";
								ex.printStackTrace();
						}
				}
		}
		folderInbox.close(false);
		store.close();
		} catch (NoSuchProviderException ex) {
		System.out.println("No provider for protocol: " + protocol);
		ex.printStackTrace();
		}
		catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");
			ex.printStackTrace();
		}
	}
	
	
	private String parseAddresses(Address[] address) {
		String listAddress = "";
		if (address != null) 
			for (int i = 0; i < address.length; i++) 
				listAddress += address[i].toString() + ", ";
			listAddress = listAddress.length() > 1 ?  listAddress.substring(0, listAddress.length() - 2) : "";
		return listAddress;
	}

}
