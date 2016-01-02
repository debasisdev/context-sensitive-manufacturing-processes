package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;

public final class ContextConfig {

	public static final File TEST_DUMP = new File("D:/dev.xml");
	public static final String MIDDLEWARE_DATABASE_ADDRESS = "127.0.0.1";
	public static final String MIDDLEWARE_DATABASE_PORT = "27017";
	public static final String MIDDLEWARE_DATABASE_NAME = "contextdb";
	public static final String MIDDLEWARE_DATABASE_COLLECTION_NAME = "packagecontext";
	
	public static final String CONTEXT_REPOSITORY = "src/main/resources/datarepos/ContextData.xml";
	public static final String PROCESS_REPOSITORY = "src/main/resources/processrepos/ProcessRepository.xml";
	
	public static final String CONTEXT_RULES = "resources/validators/ContextValidator.xsl";
	public static final String CONTEXT_NAMESPACE = "http://www.uni-stuttgart.de/iaas/cmp/v1/packaging";
	public static final String CONTEXT_QUERY = "SealAndSortPackets";
	public static final String XPATH_NAMESPACE = "http://www.w3.org/TR/xpath";
	public static final String SHOCKDETECTORSENSOR_CONTEXT_NAME = "shockDetectorStatus";
	public static final String INFRAREDSENSOR_CONTEXT_NAME = "infraredSensorStatus";
	public static final String GPSLOCATION_CONTEXT_NAME = "availableWorkers";
	public static final String UNITSORDERED_CONTEXT_NAME = "unitsOrdered";
	public static final String CD_FIELD_ORDERID = "orderid";
	public static final String CD_FIELD_DELIVERYDATE = "deliverydate";
	public static final String CD_FIELD_LANGUAGE = "language";
	public static final String CD_FIELD_MACHINE = "by";
	public static final String CD_FIELD_NAMESPACE = "url";
	public static final String CD_FIELD_TIMESTAMP = "timestamp";
	public static final String CD_FIELD_LONGITUDE = "longitude";
	public static final String CD_FIELD_LATITUDE = "latitude";
	public static final String CD_FIELD_SENSORDATALIST = "sensordata";
	public static final String IN_FIELD_AUTOMATION = "highAutomation";
	public static final String IN_FIELD_HR_UTILIZATION = "highHRUtilization";
	public static final String IN_FIELD_THROUGHPUT = "highThroughput";
	public static final String IN_FIELD_MAINTENANCE = "lowMaintenance";
	
	public static final String SHOCKDETECTORSENSOR_DOC = "If a sensor node is working fine, deploy the Automated Model (without Repair action). If some sensor node is faulty, deploy the Automated Model (with Repair action).";
	public static final String INFRAREDSENSOR_DOC = "If a sensor node is working fine, deploy the Automated Model (without Repair action). If some sensor node is faulty, deploy the Automated Model (with Repair action).";
	public static final String GPSLOCATION_DOC = "If available workers near the machine is less than equal to 10, deploy both Manual and Automated Model. If available workers near the machine is more than 10 and demand is low, deploy Manual Model. If available workers near the machine is more than 10 and demand is high, deploy both Manual and Automated Model with 10 People and remaining workers could be engaged in other works. (Optimization)";
	public static final String UNITSORDERED_DOC = "A demand is LOW if no. of units ordered is less than equal to 1000, else demand is said to be HIGH.";
	public static final String CONTEXT_PACKAGE_DOC = "The primary goal/intention is to seal and sort the packets as per the business rule.";
	public static final String HIGH_AUTOMATION_DOC = "Work must be automated more to gain higher productivity.";
	public static final String HIGH_THROUGHPUT_DOC = "Takt-time should be low.";
	public static final String HIGH_HR_UTILIZATION_DOC = "Work must be done in such a way that no employees sit idle in the premises of company.";
	public static final String LOW_MAINTENANCE_DOC = "Company requires new machinery and repeated maintenance for less on-demand repair activity.";
	
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
