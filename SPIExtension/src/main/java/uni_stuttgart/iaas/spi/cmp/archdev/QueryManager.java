package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.camel.Exchange;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TLocationType;
import de.uni_stuttgart.iaas.cmp.v0.TManufacturingContent;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TDefinition;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IQueryManager;

/**
 * A Demo Implementation Class that Implements IQueryManager and IDataRepository.
 * This module sends Context Query to the Middleware and fetches the ContextData serialized in XML format.
 * @author Debasis Kar
 */

public class QueryManager implements IQueryManager, ICamelSerializer {
	
	/**Variable to Store Context Availability 
	 * @author Debasis Kar
	 * */
	private boolean contextAvailable;
	
	/**Variable to Store CES Definition 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(QueryManager.class.getName());
	
	/**Default Constructor of QueryManager
	 * @author Debasis Kar
	 * */
	public QueryManager(){
		this.contextAvailable = false;
	}
	
	/**Parameterized Constructor of QueryManager that will query and fecth context data
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * */
	public QueryManager(TTaskCESDefinition cesDefinition){
		/*Calling Default Constructor*/
		this();
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public TContexts queryRawContextData(TTaskCESDefinition cesDefinition) {
		
		//Few Constants for this specific case
		final String MONGO_FIELD_ORDERID = "orderid";
		final String MONGO_FIELD_DELIVERYDATE = "deliverydate";
		final String MONGO_FIELD_LANGUAGE = "language";
		final String MONGO_FIELD_MACHINE = "by";
		final String MONGO_FIELD_NAMESPACE = "url";
		final String MONGO_FIELD_TIMESTAMP = "timestamp";
		final String MONGO_FIELD_LONGITUDE = "longitude";
		final String MONGO_FIELD_LATITUDE = "latitude";
		final String MONGO_FIELD_SENSORDATALIST = "sensordata";
		
		TContexts conSet = new TContexts();
		
		try{
			log.info("Connecting to Middleware...");
			//Fetch Properties from config.properties
			Properties propertyFile = new Properties();
	    	InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
	    	if(inputReader != null){
	    		propertyFile.load(inputReader);
	    		//Connect to MongoDB Instance
				MongoClient mongoClient = new MongoClient(propertyFile.getProperty("MIDDLEWARE_DATABASE_ADDRESS"), 
															Integer.parseInt(propertyFile.getProperty("MIDDLEWARE_DATABASE_PORT")));
				//Search the Database in MongoDB
				DB db = mongoClient.getDB(propertyFile.getProperty("MIDDLEWARE_DATABASE_NAME"));
				Set<String> mongoCollections = db.getCollectionNames();
				log.info("Fetching Collections from MongoDB Sensor Data Repository...");
				
				//Initialize TContexts and JAXB ObjectFactory classes
				ObjectFactory cmpMaker = new ObjectFactory();
				de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = 
											new de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory();
				
				//Access the Relevant Collection in MongoDB
				for(String coll : mongoCollections){
					if(coll.equals(propertyFile.getProperty("MIDDLEWARE_DATABASE_COLLECTION_NAME"))){
						DBCursor mongoCursor = db.getCollection(coll).find();
						
						while(mongoCursor.hasNext()) {
							BasicDBObject obj = (BasicDBObject) mongoCursor.next();
							//Check the Context Name Found in MongoDB with the Required Contexts Specified by the Modeler
							List<TContext> contextList = cesDefinition.getRequiredContexts().getContext();
							
							if(!contextList.isEmpty()){
								//Set this field as context data has been found
								this.contextAvailable = true;
								//Iterate for each required Context to find its value in MongoDB Instance
								for(TContext context : contextList){
									//Schema Related Data Fetching valid for this specific example.
								    Date date = (Date) new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").
								    		parse(obj.getString(MONGO_FIELD_DELIVERYDATE));
								    Calendar cal = Calendar.getInstance();
								    cal.setTime(date);
								    BasicDBList sensorDataList = (BasicDBList) obj.get(MONGO_FIELD_SENSORDATALIST);
								    BasicDBObject values = (BasicDBObject) sensorDataList.get(0);
								    log.info("Acquiring Context Data...");
								    BigDecimal latitude = new BigDecimal(Double.parseDouble(
								    		obj.getString(MONGO_FIELD_LATITUDE)));
							        BigDecimal longitude = new BigDecimal(Double.parseDouble(
							        		obj.getString(MONGO_FIELD_LONGITUDE)));
							        latitude = latitude.setScale(6, BigDecimal.ROUND_CEILING);
							        longitude = longitude.setScale(6, BigDecimal.ROUND_CEILING);
							        TLocationType locType = new TLocationType();
							        locType.setLatitude(latitude);
							        locType.setLongitude(longitude);
							        locType.setMachineName(obj.getString(MONGO_FIELD_MACHINE));
							        XMLGregorianCalendar dateTime = DatatypeFactory.
							        		newInstance().newXMLGregorianCalendar
							        		(new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
							        				cal.get(Calendar.DATE) ));
							        Thread.sleep(3000);
							        //Create TManufacturingContent Object
							        TManufacturingContent defConType = new TManufacturingContent();
							        defConType.setOrderID(obj.getString(MONGO_FIELD_ORDERID));
							        defConType.setDeliveryDate(dateTime);
							        defConType.setLocation(locType);
							        defConType.setTimestamp(obj.getString(MONGO_FIELD_TIMESTAMP));
							        if(context.getName().equals("unitsOrdered"))
							        	defConType.setSenseValue(obj.getString("unitsOrdered"));
							        else
							        	defConType.setSenseValue(values.get(context.getName()).toString());
							        //Create TContent Object to encapsulate TManufacturingContent inside it
							        TContent tCon = new TContent();
							        tCon.setAny(cmpMaker.createManufacturingContent(defConType));
							        TDefinition conDefType = ipsmMaker.createTDefinition();
							        conDefType.setDefinitionContent(tCon);
							        conDefType.setDefinitionLanguage(obj.getString(MONGO_FIELD_LANGUAGE));
							        log.info("Context Acquisition is in Progress...");
							        Thread.sleep(4000);
							        TContext conType = new TContext();
							        conType.getContextDefinition().add(conDefType);
							        conType.setDocumentation(context.getDocumentation());
							        conType.setName(context.getName());
							        conType.setTargetNamespace(obj.getString(MONGO_FIELD_NAMESPACE));
							        conSet.getContext().add(conType);
								}
							}
							else{
								//This is executed if there is no context data available
								this.contextAvailable = false;
								log.warning("Context Data is Not-Available!");
							}
						}
					}
				}
				//Close MongoDB and File Connection Objects
				mongoClient.close();
				inputReader.close();
	    	}
			log.info("Connection to Middleware is Closed.");
		} catch (NullPointerException e) {
			log.severe("QUEMA13: NullPointerException has Occurred.");
		} catch (InterruptedException e) {
			log.severe("QUEMA12: InterruptedException has Occurred.");
		} catch (IOException e) {
			log.severe("QUEMA11: IOException has Occurred.");
		} catch (Exception e) {
			log.severe("QUEMA10: Unknown Exception has Occurred - " + e);
		} finally {
			log.info("Context Acquisition is Finished.");
		}
		return conSet;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		//JAXB Implementation for Serializing the Context Data into a Repository of XML.
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = 
								new de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory();
		TContexts contextSet = this.queryRawContextData(this.cesDefinition);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if(this.contextAvailable){
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		        JAXBElement<TContexts> root = ipsmMaker.createContextSet(contextSet);
				jaxbMarshaller.marshal(root, outputStream);
				log.info("Context Data is Available on Message Queue.");
		    	jaxbMarshaller.marshal(root, System.out);
			} catch (JAXBException e) {
				log.severe("QUEMA02: JAXBException has Occurred.");
			} catch (NullPointerException e) {
				log.severe("QUEMA01: NullPointerException has Occurred.");
			} catch (Exception e) {
				log.severe("QUEMA00: Unknown Exception has Occurred - " + e);
			} 
		}
		return outputStream.toByteArray();
	}

}

/*Mongo Schema
	db.packagecontext.insert([
          {
             orderid: 'DE37464358BY',	  
             deliverydate: new Date(2015,12,10),
       	   language: 'en_US',
             by: 'Sealing Machine: SMEX207',
             url: 'http://www.uni-stuttgart.de/iaas/cmp/v1/packaging',
             timestamp: new Timestamp(),
       	   latitude: 48.145198,
       	   longitude: 11.5765667,
       	   unitsOrdered: 1000,
       	   sensordata: [	
                {
       			availableWorkers: 4,
                  infraredSensorStatus: 'Malfunctioned',
       			shockDetectorStatus: 'Okay'
                }
             ]
          }
       ])*/