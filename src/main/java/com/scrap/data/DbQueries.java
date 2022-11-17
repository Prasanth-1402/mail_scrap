/**
 * 
 * @author PRASANTH
 *
 */
package com.scrap.data;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.scrap.dto.IncomingMailDto;

/**
 * @author PRASANTH
 *
 * 11:17:13 PM
 */
public class DbQueries {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	

	public void saveIncomingMail(IncomingMailDto mailSaverDto){
		
		String sql = "INSERT into incoming_mails VALUES(?,?,?,?,?,?,?,?) ";
		Object[] queryParamsValue = { mailSaverDto.getFrom(), mailSaverDto.getToList(),
				mailSaverDto.getCcList(), mailSaverDto.getSentDate(), mailSaverDto.getAttachment(), mailSaverDto.getUserFlag(), mailSaverDto.getSystemFlag()};
		try{
			int value = jdbcTemplate.update(sql, queryParamsValue);
		}
		catch(Exception e){
			System.err.println("---- EXCEPTION IN  SAVING INCOMING MAIL ----");
			e.printStackTrace();
		}
	}
}
