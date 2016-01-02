package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TLocationType;
import de.uni_stuttgart.iaas.ipsm.v0.TManufacturingContent;

public class QueryManager implements IQueryManager {
	
	private File contextData;
	private TContexts requiredContexts;
	private TIntention intention;
	private boolean contextAvailable;
	private static final Logger log = Logger.getLogger(QueryManager.class.getName());
	
	public QueryManager(){
		this.contextData = new File(ContextConfig.CONTEXT_REPOSITORY);
		this.requiredContexts = null;
		this.intention = null;
		this.contextAvailable = false;
	}
	
	public QueryManager(TContexts requiredContexts, TIntention intention){
		this.contextData = new File(ContextConfig.CONTEXT_REPOSITORY);
		this.requiredContexts = requiredContexts;
		this.intention = intention;
		this.contextAvailable = false;
		this.queryRawContextData(requiredContexts);
	}
	
	public void queryRawContextData(TContexts requiredContexts) {
		try{
			log.info("Connecting to Middleware...");
			MongoClient mongoClient = new MongoClient(ContextConfig.MIDDLEWARE_DATABASE_ADDRESS, 
														Integer.parseInt(ContextConfig.MIDDLEWARE_DATABASE_PORT));
			DB db = mongoClient.getDB(ContextConfig.MIDDLEWARE_DATABASE_NAME);
			Set<String> mongoCollections = db.getCollectionNames();
			log.info("Fetching Collections from MongoDB Sensor Data Repository...");
			ObjectFactory contextSetCreator = new ObjectFactory();
			TContexts conSet = new TContexts();
			for(String coll : mongoCollections){
				if(coll.equals(ContextConfig.MIDDLEWARE_DATABASE_COLLECTION_NAME)){
					DBCursor mongoCursor = db.getCollection(coll).find();
					while(mongoCursor.hasNext()) {
						BasicDBObject obj = (BasicDBObject) mongoCursor.next();
						List<TContext> contextList = requiredContexts.getContext();
						if(!contextList.isEmpty()){
							this.contextAvailable = true;
							for(TContext context : contextList){
							    Date date = (Date) new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").
							    		parse(obj.getString(ContextConfig.CD_FIELD_DELIVERYDATE));
							    Calendar cal = Calendar.getInstance();
							    cal.setTime(date);
							    BasicDBList sensorDataList = (BasicDBList) obj.get(ContextConfig.CD_FIELD_SENSORDATALIST);
							    BasicDBObject values = (BasicDBObject) sensorDataList.get(0);
							    log.info("Acquiring Context Data...");
							    BigDecimal latitude = new BigDecimal(Double.parseDouble(
							    		obj.getString(ContextConfig.CD_FIELD_LATITUDE)));
						        BigDecimal longitude = new BigDecimal(Double.parseDouble(
						        		obj.getString(ContextConfig.CD_FIELD_LONGITUDE)));
						        latitude = latitude.setScale(6, BigDecimal.ROUND_CEILING);
						        longitude = longitude.setScale(6, BigDecimal.ROUND_CEILING);
						        TLocationType locType = new TLocationType();
						        locType.setLatitude(latitude);
						        locType.setLongitude(longitude);
						        locType.setMachineName(obj.getString(ContextConfig.CD_FIELD_MACHINE));
						        XMLGregorianCalendar dateTime = DatatypeFactory.
						        		newInstance().newXMLGregorianCalendar
						        		(new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
						        				cal.get(Calendar.DATE) ));
						        
						        
						        TManufacturingContent defConType = new TManufacturingContent();
						        defConType.setOrderID(obj.getString(ContextConfig.CD_FIELD_ORDERID));
						        defConType.setDeliveryDate(dateTime);
						        defConType.setLocation(locType);
						        defConType.setTimestamp(obj.getString(ContextConfig.CD_FIELD_TIMESTAMP));
						        if(context.getName().equals(ContextConfig.UNITSORDERED_CONTEXT_NAME))
						        	defConType.setSenseValue(obj.getString(ContextConfig.UNITSORDERED_CONTEXT_NAME));
						        else
						        	defConType.setSenseValue(values.get(context.getName()).toString());
						        
						        TContent tCon = new TContent();
						        tCon.setAny(contextSetCreator.createManufacturingContent(defConType));
						        TDefinition conDefType = contextSetCreator.createTDefinition();
						        conDefType.setDefinitionContent(tCon);
						        conDefType.setDefinitionLanguage(obj.getString(ContextConfig.CD_FIELD_LANGUAGE));
						        log.info("Context Acquisition Is In Progress...");
						        TContext conType = new TContext();
						        conType.getContextDefinition().add(conDefType);
						        conType.setDocumentation(context.getDocumentation());
						        conType.setName(context.getName());
						        conType.setTargetNamespace(obj.getString(ContextConfig.CD_FIELD_NAMESPACE));
						        conSet.getContext().add(conType);
							}
				    		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
				    		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				    		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				            JAXBElement<TContexts> root = contextSetCreator.createContextSet(conSet);
				    		jaxbMarshaller.marshal(root, this.contextData);
				    		log.info("Context Data is Serialized as ContextData.xml.");
					    	jaxbMarshaller.marshal(root, System.out);
						}
						else{
							this.contextAvailable = false;
							log.warning("Context Data is Not Available!");
						}
					}
				}
			}
			mongoClient.close();
			log.info("Connection to Middleware Is Closed.");
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Query Manager!");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			log.severe("NumberFormatException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Query Manager!");
		} catch (DatatypeConfigurationException e) {
			log.severe("DatatypeConfigurationException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Query Manager!");
		} catch (ParseException e) {
			log.severe("ParseException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Query Manager!");
		} catch (UnknownHostException e) {
			log.severe("UnknownHostException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Query Manager!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + " in Query Manager!");
			e.printStackTrace();
		} catch(Exception e){
			log.severe("Unknown Exception has occurred at Line " + 
					e.getStackTrace()[e.getStackTrace().length-5].getLineNumber() + 
					" in Query Manager!\n" + e.getMessage());
		} finally {
			log.info("Context Acquisition Is Finished.");
		}
	}
	
	public TContexts getRequiredContexts() {
		return requiredContexts;
	}

	public TIntention getIntention() {
		return intention;
	}

	public boolean isContextAvailable() {
		return contextAvailable;
	}
}
