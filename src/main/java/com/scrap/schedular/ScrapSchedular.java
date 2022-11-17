/**
 * 
 * @author PRASANTH
 *
 */
package com.scrap.schedular;

import org.springframework.scheduling.annotation.Scheduled;

import com.scrap.app.ScrapApp;
import com.scrap.util.StaticVariables;

/**
 * @author PRASANTH
 *
 * 10:25:08 PM
 */
public class ScrapSchedular {

	ScrapApp scrapApp = new ScrapApp();

	@Scheduled(cron =  StaticVariables.scrapRunner)
	private void runScrapper() {
		
		scrapApp.getIncomingMails(StaticVariables.protocol, StaticVariables.host, 
														StaticVariables.port, StaticVariables.userName, StaticVariables.password);
	}
			
}
