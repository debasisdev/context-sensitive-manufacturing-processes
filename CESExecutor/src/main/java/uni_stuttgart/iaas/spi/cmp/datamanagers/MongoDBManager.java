package uni_stuttgart.iaas.spi.cmp.datamanagers;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TLocationType;
import de.uni_stuttgart.iaas.cmp.v0.TManufacturingContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContent;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TEntityDefinition;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataManager;
import uni_stuttgart.iaas.spi.cmp.realizations.QueryManager;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/** 
 * Copyright 2016 Debasis Kar
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

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
	private static final Logger log = LoggerFactory.getLogger(MongoDBManager.class);
	
	/**Default constructor of {@link MongoDBManager}
	 * @author Debasis Kar
	 * */
	public MongoDBManager(){
		this.contextAvailable = false;
	}
	
	@Override
	public TContextDefinitions getDataFromDatabase(List<TContextDefinition> contextList){
		TContextDefinitions conSet = new TContextDefinitions();
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
							for(TContextDefinition context : contextList){
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
						        if(context.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName().equals(CESExecutorConfig.PACKAGING_INTENTION_UNITS))
						        	defConType.setSenseValue(obj.getString(context.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName()));
						        else
						        	defConType.setSenseValue(values.get(context.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName()).toString());
						        //Create TContent Object to encapsulate TManufacturingContent inside it
						        TContent tCon = new TContent();
						        tCon.setAny(cmpMaker.createManufacturingContent(defConType));
						        TEntityDefinition conDefType = ipsmMaker.createTEntityDefinition();
						        conDefType.setDefinitionContent(tCon);
						        conDefType.setDefinitionLanguage(obj.getString(CESExecutorConfig.MONGO_FIELD_LANGUAGE));
						        log.info("Context Acquisition is in Progress...");
						        TContextDefinition conType = new TContextDefinition();
						        conType.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityDefinitions().getEntityDefinition().add(conDefType);
						        
						        conType.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setName(context.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName());
						        conType.getInteractiveEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().setTargetNamespace(obj.getString(CESExecutorConfig.MONGO_FIELD_NAMESPACE));
						        conSet.getContext().add(conType);
							}
						}
						else{
							//This is executed if there is no context data available
							this.contextAvailable = false;
							log.warn("Context Data is Not-Available!");
						}
					}
				}
			}
			//Close MongoDB and File Connection Objects
			mongoClient.close();
		} catch (NullPointerException e) {
			log.error("MONDB12: NullPointerException has Occurred.");
		} catch (IOException e) {
			log.error("MONDB11: IOException has Occurred.");
		} catch (Exception e) {
			log.error("MONDB10: Unknown Exception has Occurred - " + e);
		} 
		return conSet;
	}
	
	@Override
	public boolean isContextAvailable() {
		return contextAvailable;
	}
}
