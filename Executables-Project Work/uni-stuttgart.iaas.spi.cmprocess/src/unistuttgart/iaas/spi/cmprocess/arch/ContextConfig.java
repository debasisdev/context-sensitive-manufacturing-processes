package unistuttgart.iaas.spi.cmprocess.arch;

public final class ContextConfig {

	public static final String MIDDLEWARE_DATABASE_ADDRESS = "127.0.0.1";
	public static final String MIDDLEWARE_DATABASE_PORT = "27017";
	public static final String MIDDLEWARE_DATABASE_NAME = "contextdb";
	public static final String MIDDLEWARE_DATABASE_COLLECTION_NAME = "packagecontext";
	public static final String CONTEXT_REPOSITORY = "resources/datarepos/ContextData.xml";
	public static final String PROCESS_REPOSITORY = "resources/processrepos/ProcessRepository.xml";
	public static final String CONTEXT_ANALYSIS_REPORT = "resources/datarepos/ContextAnalyzerReport.xml";
	public static final String CONTEXT_RULES = "resources/validators/ContextValidator.xsl";
	
	public static final String CONTEXT_QUERY = "HowToSealAndSort";
	public static final String INTENTION_QUERY = "SealAndSortPackets";
	
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
	public static final String SHOCKDETECTORSENSOR_DOC = "If a sensor node is working fine, deploy the Automated Model (without Repair action). If some sensor node is faulty, deploy the Automated Model (with Repair action).";
	public static final String INFRAREDSENSOR_DOC = "If a sensor node is working fine, deploy the Automated Model (without Repair action). If some sensor node is faulty, deploy the Automated Model (with Repair action).";
	public static final String GPSLOCATION_DOC = "If available workers near the machine is less than equal to 10, deploy both Manual and Automated Model. If available workers near the machine is more than 10 and demand is low, deploy Manual Model. If available workers near the machine is more than 10 and demand is high, deploy both Manual and Automated Model with 10 People and remaining workers could be engaged in other works. (Optimization)";
	public static final String UNITSORDERED_DOC = "A demand is LOW if no. of units ordered is less than equal to 1000, else demand is said to be HIGH.";
	
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
