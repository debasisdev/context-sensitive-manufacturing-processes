package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.camel.Exchange;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IQueryManager;
import uni_stuttgart.iaas.spi.cmp.helper.DataManager;

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
	
	/**Parameterized Constructor of QueryManager
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * */
	public QueryManager(TTaskCESDefinition cesDefinition){
		this();
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public TContexts queryRawContextData(TTaskCESDefinition cesDefinition) {	
		TContexts conSet = new TContexts();
		try{
			log.info("Connecting to Middleware...");
			//Fetch Required Contexts Specified by the Modeler
			List<TContext> contextList = cesDefinition.getRequiredContexts().getContext();
			//Calling Database Manager for Getting Data from Mongo Instance
    		DataManager dataCollector = new DataManager();
    		conSet = dataCollector.getDataFromDatabase(contextList);
    		this.contextAvailable = dataCollector.isContextAvailable();
			log.info("Connection to Middleware is Closed.");
		} catch (NullPointerException e) {
			log.severe("QUEMA11: NullPointerException has Occurred.");
			e.printStackTrace();
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
				//Serializing the received data from Middleware to byte[] for messaging to other modules.
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