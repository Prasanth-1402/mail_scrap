/**
 * 
 * @author PRASANTH
 *
 */
package com.scrap.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author PRASANTH
 *
 * 11:45:53 PM
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IncomingMailDto {
	
	private String from;
	private String toList;
	private String ccList;
	private Date sentDate;
	private String messageContent;
	private byte[] attachment;
	private String userFlag;
	private String systemFlag;
	
}
