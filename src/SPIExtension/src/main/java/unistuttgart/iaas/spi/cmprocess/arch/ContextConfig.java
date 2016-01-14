package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;

public final class ContextConfig {

	public static final File TEST_DUMP = new File("D:/dev.xml");
	public static final String CONTEXT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v1/packaging";
	public static final String XPATH_NAMESPACE = "http://www.w3.org/TR/xpath";
	
	
	
	//	db.packagecontext.insert([
	//          {
	//             orderid: 'DE37464358BY',	  
	//             deliverydate: new Date(2015,12,10),
	//       	   language: 'en_US',
	//             by: 'Sealing Machine: SMEX207',
	//             url: 'http://www.uni-stuttgart.de/iaas/cmp/v1/packaging',
	//             timestamp: new Timestamp(),
	//       	   latitude: 48.145198,
	//       	   longitude: 11.5765667,
	//       	   unitsOrdered: 1000,
	//       	   sensordata: [	
	//                {
	//       			availableWorkers: 4,
	//                  infraredSensorStatus: 'Malfunctioned',
	//       			shockDetectorStatus: 'Okay'
	//                }
	//             ]
	//          }
	//       ])
}
