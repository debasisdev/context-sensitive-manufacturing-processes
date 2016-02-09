package uni_stuttgart.iaas.spi.cmp.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TLocationType;
import de.uni_stuttgart.iaas.cmp.v0.TManufacturingContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataManager;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;

/**
 * A helper class to {@link QueryManager} that does all the database operations on behalf of it.
 * It implements the {@link IDataManager} interface.
 * @author Debasis Kar
 */

public class MongoDBManager implements IDataManager{
	
	/**Variable to store context availability 
	 * @author Debasis Kar
	 * */
	private boolean contextAvailable;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(MongoDBManager.class.getName());
	
	/**Default constructor of {@link MongoDBManager}
	 * @author Debasis Kar
	 * */
	public MongoDBManager(){
		this.contextAvailable = false;
	}
	
	/**This method connects to the MongoDB instance and tries to fetch the required data in runtime.
	 * @author Debasis Kar
	 * @param void
	 * @return TContexts
	 * */
	@Override
	public TContexts getDataFromDatabase(List<TContext> contextList){
		TContexts conSet = new TContexts();
		try {
			//Connect to MongoDB Instance
			MongoClient mongoClient = new MongoClient(CESExecutorConfig.MONGO_DATABASE_ADDRESS, Integer.parseInt(CESExecutorConfig.MONGO_DATABASE_PORT));
			//Search the Database in MongoDB
			DB db = mongoClient.getDB(CESExecutorConfig.MONGO_DATABASE_NAME);
			Set<String> mongoCollections = db.getCollectionNames();
			log.info("Fetching Collections from MongoDB Sensor Data Repository...");
			//Initialize TContexts and JAXB ObjectFactory classes
			ObjectFactory cmpMaker = new ObjectFactory();
			de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory();
			//Access the Relevant Collection in MongoDB
			for(String coll : mongoCollections){
				if(coll.equals(CESExecutorConfig.MONGO_COLLECTION_NAME)){
					DBCursor mongoCursor = db.getCollection(coll).find();
					while(mongoCursor.hasNext()) {
						BasicDBObject obj = (BasicDBObject) mongoCursor.next();
						if(!contextList.isEmpty()){
							//Set this field as context data has been found
							this.contextAvailable = true;
							//Iterate for each required Context to find its value in MongoDB Instance
							for(TContext context : contextList){
								//Schema Related Data Fetching valid for this specific example.
							    Date date = (Date) new SimpleDateFormat(CESExecutorConfig.TIME_FORMAT).
							    		parse(obj.getString(CESExecutorConfig.MONGO_FIELD_DELIVERYDATE));
							    Calendar cal = Calendar.getInstance();
							    cal.setTime(date);
							    BasicDBList sensorDataList = (BasicDBList) obj.get(CESExecutorConfig.MONGO_FIELD_SENSORDATALIST);
							    BasicDBObject values = (BasicDBObject) sensorDataList.get(0);
							    log.info("Acquiring Context Data...");
							    BigDecimal latitude = new BigDecimal(Double.parseDouble(
							    		obj.getString(CESExecutorConfig.MONGO_FIELD_LATITUDE)));
						        BigDecimal longitude = new BigDecimal(Double.parseDouble(
						        		obj.getString(CESExecutorConfig.MONGO_FIELD_LONGITUDE)));
						        latitude = latitude.setScale(6, BigDecimal.ROUND_CEILING);
						        longitude = longitude.setScale(6, BigDecimal.ROUND_CEILING);
						        TLocationType locType = new TLocationType();
						        locType.setLatitude(latitude);
						        locType.setLongitude(longitude);
						        locType.setMachineName(obj.getString(CESExecutorConfig.MONGO_FIELD_MACHINE));
						        XMLGregorianCalendar dateTime = DatatypeFactory.
						        		newInstance().newXMLGregorianCalendar
						        		(new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
						        				cal.get(Calendar.DATE) ));
						        //Create TManufacturingContent Object
						        TManufacturingContent defConType = new TManufacturingContent();
						        defConType.setOrderID(obj.getString(CESExecutorConfig.MONGO_FIELD_ORDERID));
						        defConType.setDeliveryDate(dateTime);
						        defConType.setLocation(locType);
						        defConType.setTimestamp(obj.getString(CESExecutorConfig.MONGO_FIELD_TIMESTAMP));
						        if(context.getName().equals(CESExecutorConfig.PACKAGING_INTENTION_UNITS))
						        	defConType.setSenseValue(obj.getString(context.getName()));
						        else
						        	defConType.setSenseValue(values.get(context.getName()).toString());
						        //Create TContent Object to encapsulate TManufacturingContent inside it
						        TContent tCon = new TContent();
						        tCon.setAny(cmpMaker.createManufacturingContent(defConType));
						        TDefinition conDefType = ipsmMaker.createTDefinition();
						        conDefType.setDefinitionContent(tCon);
						        conDefType.setDefinitionLanguage(obj.getString(CESExecutorConfig.MONGO_FIELD_LANGUAGE));
						        log.info("Context Acquisition is in Progress...");
						        TContext conType = new TContext();
						        conType.getContextDefinition().add(conDefType);
						        conType.setDocumentation(context.getDocumentation());
						        conType.setName(context.getName());
						        conType.setTargetNamespace(obj.getString(CESExecutorConfig.MONGO_FIELD_NAMESPACE));
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
		} catch (NullPointerException e) {
			log.severe("MONDB12: NullPointerException has Occurred.");
		} catch (IOException e) {
			log.severe("MONDB11: IOException has Occurred.");
		} catch (Exception e) {
			log.severe("MONDB10: Unknown Exception has Occurred - " + e);
		} 
		return conSet;
	}
	
	@Override
	public boolean isContextAvailable() {
		return contextAvailable;
	}
}
