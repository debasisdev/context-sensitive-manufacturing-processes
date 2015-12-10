package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.xml.bind.JAXBContext;
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

import unistuttgart.iaas.spi.cmprocess.cmp.TContent;
import unistuttgart.iaas.spi.cmprocess.cmp.TContext;
import unistuttgart.iaas.spi.cmprocess.cmp.TContexts;
import unistuttgart.iaas.spi.cmprocess.cmp.TDefinition;
import unistuttgart.iaas.spi.cmprocess.cmp.TLocationType;

public class QueryManager {
	
	private File contextData;
	private String contextQuery;
	private String intention;
	
	public QueryManager(){
		this.contextData = null;
		this.intention = null;
		this.queryRawContextData();
	}
	
	public QueryManager(String contextQuery, String intention){
		this.contextData = new File(ContextConfig.CONTEXT_REPOSITORY);
		this.contextQuery = contextQuery;
		this.intention = intention;
		this.queryRawContextData();
	}
	
	private void queryRawContextData() {
		try{
			System.out.println("Connecting to Middleware...");
			MongoClient mongoClient = new MongoClient(ContextConfig.MIDDLEWARE_DATABASE_ADDRESS, 
														Integer.parseInt(ContextConfig.MIDDLEWARE_DATABASE_PORT));
			DB db = mongoClient.getDB(ContextConfig.MIDDLEWARE_DATABASE_NAME);
			Set<String> mongoCollections = db.getCollectionNames();
			for(String coll : mongoCollections){
				if(this.contextQuery.equalsIgnoreCase(ContextConfig.CONTEXT_QUERY)){
					if(coll.equals(ContextConfig.MIDDLEWARE_DATABASE_COLLECTION_NAME)){
						DBCursor mongoCursor = db.getCollection(coll).find();
						while(mongoCursor.hasNext()) {
							BasicDBObject obj = (BasicDBObject) mongoCursor.next();
						    Date date = (Date) new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(obj.getString(ContextConfig.CD_FIELD_DELIVERYDATE));
						    Calendar cal = Calendar.getInstance();
						    cal.setTime(date);
						    BasicDBList sensorDataList = (BasicDBList) obj.get(ContextConfig.CD_FIELD_SENSORDATALIST);
						    BasicDBObject values = (BasicDBObject) sensorDataList.get(0);
						    
						    BigDecimal latitude = new BigDecimal(Double.parseDouble(obj.getString(ContextConfig.CD_FIELD_LATITUDE)));
					        BigDecimal longitude = new BigDecimal(Double.parseDouble(obj.getString(ContextConfig.CD_FIELD_LONGITUDE)));
					        latitude = latitude.setScale(6, BigDecimal.ROUND_CEILING);
					        longitude = longitude.setScale(6, BigDecimal.ROUND_CEILING);
					        TLocationType locType = new TLocationType();
					        locType.setLatitude(latitude);
					        locType.setLongitude(longitude);
					        locType.setMachineName(obj.getString(ContextConfig.CD_FIELD_MACHINE));
					        XMLGregorianCalendar dateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar
					        		(new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
					        				cal.get(Calendar.DATE) ));
					        
					        TContent defConTypeA = new TContent();
					        defConTypeA.setOrderID(obj.getString(ContextConfig.CD_FIELD_ORDERID));
					        defConTypeA.setDeliveryDate(dateTime);
					        defConTypeA.setLocation(locType);
					        defConTypeA.setTimestamp(obj.getString(ContextConfig.CD_FIELD_TIMESTAMP));
					        defConTypeA.setSenseValue(values.get(ContextConfig.SHOCKDETECTORSENSOR_CONTEXT_NAME).toString());
					        
					        TDefinition conDefTypeA = new TDefinition();
					        conDefTypeA.setDefinitionContent(defConTypeA);
					        conDefTypeA.setDefinitionLanguage(obj.getString(ContextConfig.CD_FIELD_LANGUAGE));
					        
					        TContext conTypeA = new TContext();
					        conTypeA.getContextDefinition().add(conDefTypeA);
					        conTypeA.setDocumentation(ContextConfig.SHOCKDETECTORSENSOR_DOC);
					        conTypeA.setName(ContextConfig.SHOCKDETECTORSENSOR_CONTEXT_NAME);
					        conTypeA.setTargetNamespace(obj.getString(ContextConfig.CD_FIELD_NAMESPACE));
					        
					        TContent defConTypeB = new TContent();
					        defConTypeB.setOrderID(obj.getString(ContextConfig.CD_FIELD_ORDERID));
					        defConTypeB.setDeliveryDate(dateTime);
					        defConTypeB.setLocation(locType);
					        defConTypeB.setTimestamp(obj.getString(ContextConfig.CD_FIELD_TIMESTAMP));
					        defConTypeB.setSenseValue(values.get(ContextConfig.INFRAREDSENSOR_CONTEXT_NAME).toString());
					        
					        TDefinition conDefTypeB = new TDefinition();
					        conDefTypeB.setDefinitionContent(defConTypeB);
					        conDefTypeB.setDefinitionLanguage(obj.getString(ContextConfig.CD_FIELD_LANGUAGE));
					        
					        TContext conTypeB = new TContext();
					        conTypeB.getContextDefinition().add(conDefTypeB);
					        conTypeB.setDocumentation(ContextConfig.INFRAREDSENSOR_DOC);
					        conTypeB.setName(ContextConfig.INFRAREDSENSOR_CONTEXT_NAME);
					        conTypeB.setTargetNamespace(obj.getString(ContextConfig.CD_FIELD_NAMESPACE));
					        
					        TContent defConTypeC = new TContent();
					        defConTypeC.setOrderID(obj.getString(ContextConfig.CD_FIELD_ORDERID));
					        defConTypeC.setDeliveryDate(dateTime);
					        defConTypeC.setLocation(locType);
					        defConTypeC.setTimestamp(obj.getString(ContextConfig.CD_FIELD_TIMESTAMP));
					        defConTypeC.setSenseValue(obj.getString(ContextConfig.UNITSORDERED_CONTEXT_NAME));
					        
					        TDefinition conDefTypeC = new TDefinition();
					        conDefTypeC.setDefinitionContent(defConTypeC);
					        conDefTypeC.setDefinitionLanguage(obj.getString(ContextConfig.CD_FIELD_LANGUAGE));
					        
					        TContext conTypeC = new TContext();
					        conTypeC.getContextDefinition().add(conDefTypeC);
					        conTypeC.setDocumentation(ContextConfig.UNITSORDERED_DOC);
					        conTypeC.setName(ContextConfig.UNITSORDERED_CONTEXT_NAME);
					        conTypeC.setTargetNamespace(obj.getString(ContextConfig.CD_FIELD_NAMESPACE));
					        
					        TContent defConTypeD = new TContent();
					        defConTypeD.setOrderID(obj.getString(ContextConfig.CD_FIELD_ORDERID));
					        defConTypeD.setDeliveryDate(dateTime);
					        defConTypeD.setLocation(locType);
					        defConTypeD.setTimestamp(obj.getString(ContextConfig.CD_FIELD_TIMESTAMP));
					        defConTypeD.setSenseValue(values.get(ContextConfig.GPSLOCATION_CONTEXT_NAME).toString());
					        
					        TDefinition conDefTypeD = new TDefinition();
					        conDefTypeD.setDefinitionContent(defConTypeD);
					        conDefTypeD.setDefinitionLanguage(obj.getString(ContextConfig.CD_FIELD_LANGUAGE));
					        
					        TContext conTypeD = new TContext();
					        conTypeD.getContextDefinition().add(conDefTypeD);
					        conTypeD.setDocumentation(ContextConfig.GPSLOCATION_DOC);
					        conTypeD.setName(ContextConfig.GPSLOCATION_CONTEXT_NAME);
					        conTypeD.setTargetNamespace(obj.getString(ContextConfig.CD_FIELD_NAMESPACE));
					        
					        TContexts conSet = new TContexts();
							conSet.getContext().add(conTypeA);
							conSet.getContext().add(conTypeB);
							conSet.getContext().add(conTypeC);
							conSet.getContext().add(conTypeD);
	
				    		JAXBContext jaxbContext = JAXBContext.newInstance("unistuttgart.iaas.spi.cmprocess.cmp");
				    		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				    		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				    		jaxbMarshaller.marshal(conSet, this.contextData);
//				    		jaxbMarshaller.marshal(conSet, System.out);
						}
					}
				}
				else {
					System.err.println("No Context Data Available!");
				}
			}
			mongoClient.close();
		} catch (JAXBException e) {
			System.err.println("JAXBException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} catch (NumberFormatException e) {
			System.err.println("NumberFormatException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} catch (DatatypeConfigurationException e) {
			System.err.println("DatatypeConfigurationException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} catch (ParseException e) {
			System.err.println("ParseException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} catch (NullPointerException e) {
			System.err.println("NullPointerException has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} catch(Exception e){
			System.err.println("Unknown Exception has occurred at Line " + e.getStackTrace()[e.getStackTrace().length-3].getLineNumber() + " in Query Manager!");
		} finally {
			System.out.println("Context Acquisition Is Finished.");
		}
	}

	public String getContextQuery() {
		return contextQuery;
	}

	public File getContextData() {
		return contextData;
	}

	public String getIntention() {
		return intention;
	}
	
}
