package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.ParseException;
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
import javax.xml.datatype.DatatypeConfigurationException;
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
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TDefinition;
import unistuttgart.iaas.spi.cmprocess.interfaces.IDataRepository;
import unistuttgart.iaas.spi.cmprocess.interfaces.IQueryManager;

public class QueryManager implements IQueryManager, IDataRepository {
	
	
	private boolean contextAvailable;
	private static final Logger log = Logger.getLogger(QueryManager.class.getName());
	
	public QueryManager(){
		this.contextAvailable = false;
	}
	
	public QueryManager(TTaskCESDefinition cesDefinition){
		this();
		this.queryRawContextData(cesDefinition);
	}
	
	@Override
	public void queryRawContextData(TTaskCESDefinition cesDefinition) {
		final String MONGO_FIELD_ORDERID = "orderid";
		final String MONGO_FIELD_DELIVERYDATE = "deliverydate";
		final String MONGO_FIELD_LANGUAGE = "language";
		final String MONGO_FIELD_MACHINE = "by";
		final String MONGO_FIELD_NAMESPACE = "url";
		final String MONGO_FIELD_TIMESTAMP = "timestamp";
		final String MONGO_FIELD_LONGITUDE = "longitude";
		final String MONGO_FIELD_LATITUDE = "latitude";
		final String MONGO_FIELD_SENSORDATALIST = "sensordata";
		try{
			log.info("Connecting to Middleware...");
			Properties propertyFile = new Properties();
	    	InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
	    	if(inputReader != null){
	    		propertyFile.load(inputReader);
				MongoClient mongoClient = new MongoClient(propertyFile.getProperty("MIDDLEWARE_DATABASE_ADDRESS"), 
															Integer.parseInt(propertyFile.getProperty("MIDDLEWARE_DATABASE_PORT")));
				DB db = mongoClient.getDB(propertyFile.getProperty("MIDDLEWARE_DATABASE_NAME"));
				Set<String> mongoCollections = db.getCollectionNames();
				log.info("Fetching Collections from MongoDB Sensor Data Repository...");
				ObjectFactory cmpMaker = new ObjectFactory();
				de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory();
				TContexts conSet = new TContexts();
				for(String coll : mongoCollections){
					if(coll.equals(propertyFile.getProperty("MIDDLEWARE_DATABASE_COLLECTION_NAME"))){
						DBCursor mongoCursor = db.getCollection(coll).find();
						while(mongoCursor.hasNext()) {
							BasicDBObject obj = (BasicDBObject) mongoCursor.next();
							List<TContext> contextList = cesDefinition.getRequiredContexts().getContext();
							if(!contextList.isEmpty()){
								this.contextAvailable = true;
								for(TContext context : contextList){
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
							        
							        
							        TManufacturingContent defConType = new TManufacturingContent();
							        defConType.setOrderID(obj.getString(MONGO_FIELD_ORDERID));
							        defConType.setDeliveryDate(dateTime);
							        defConType.setLocation(locType);
							        defConType.setTimestamp(obj.getString(MONGO_FIELD_TIMESTAMP));
							        if(context.getName().equals("unitsOrdered"))
							        	defConType.setSenseValue(obj.getString("unitsOrdered"));
							        else
							        	defConType.setSenseValue(values.get(context.getName()).toString());
							        
							        TContent tCon = new TContent();
							        tCon.setAny(cmpMaker.createManufacturingContent(defConType));
							        TDefinition conDefType = ipsmMaker.createTDefinition();
							        conDefType.setDefinitionContent(tCon);
							        conDefType.setDefinitionLanguage(obj.getString(MONGO_FIELD_LANGUAGE));
							        log.info("Context Acquisition Is In Progress...");
							        TContext conType = new TContext();
							        conType.getContextDefinition().add(conDefType);
							        conType.setDocumentation(context.getDocumentation());
							        conType.setName(context.getName());
							        conType.setTargetNamespace(obj.getString(MONGO_FIELD_NAMESPACE));
							        conSet.getContext().add(conType);
								}
					    		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
					    		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					    		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					            JAXBElement<TContexts> root = ipsmMaker.createContextSet(conSet);
					    		jaxbMarshaller.marshal(root, this.getContextRepository());
					    		log.info("Context Data is Serialized as ContextData.xml.");
	//					    	jaxbMarshaller.marshal(root, System.out);
							}
							else{
								this.contextAvailable = false;
								log.warning("Context Data is Not Available!");
							}
						}
					}
				}
				mongoClient.close();
				inputReader.close();
	    	}
			log.info("Connection to Middleware Is Closed.");
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred in Query Manager!");
		} catch (NumberFormatException e) {
			log.severe("NumberFormatException has occurred in Query Manager!");
		} catch (DatatypeConfigurationException e) {
			log.severe("DatatypeConfigurationException has occurred in Query Manager!");
		} catch (ParseException e) {
			log.severe("ParseException has occurred in Query Manager!");
		} catch (UnknownHostException e) {
			log.severe("UnknownHostException has occurred in Query Manager!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred in Query Manager!");
			e.printStackTrace();
		} catch (IOException e) {
			log.severe("IOException has occurred in Query Manager!");
		} catch(Exception e){
			log.severe("Unknown Exception has occurred in Query Manager!\n" + e.getMessage());
		} finally {
			log.info("Context Acquisition Is Finished.");
		}
	}
	
	@Override
	public boolean isContextAvailable() {
		return this.contextAvailable;
	}

	@Override
	public File getContextRepository() {
		Properties propertyFile = new Properties();
    	InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
    	String fileName = null;
		if(inputReader != null){
	        try {
				propertyFile.load(inputReader);
				fileName = propertyFile.getProperty("CONTEXT_REPOSITORY");
		        inputReader.close();
			} catch (IOException e) {
				log.severe("IOException has occurred in Query Manager!!");
			}
		}
		return new File(fileName);
	}

	@Override
	public File getProcessRepository(TTaskCESDefinition cesDefinition) {
		return new File(cesDefinition.getProcessRepository());
	}
}
