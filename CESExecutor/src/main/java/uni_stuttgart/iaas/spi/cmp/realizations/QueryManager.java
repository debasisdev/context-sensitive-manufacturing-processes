package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinition;

import de.uni_stuttgart.iaas.ipsm.v0.TContextDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IQueryProcessor;
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
 * A generic class that Implements {@link IQueryProcessor}, {@link IDataSerializer}, and {@link Processor}.
 * This module sends context query to the Middle-ware and fetches the context data serialized in byte array.
 * @author Debasis Kar
 */

public class QueryManager implements IQueryProcessor, IDataSerializer, Processor {
	
	/**Variable to store context availability 
	 * @author Debasis Kar
	 * */
	private boolean contextAvailable;
	
	/**Variable to Store {@link TTaskCESDefinition}
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(QueryManager.class);
	
	/**Default constructor of {@link QueryManager}
	 * @author Debasis Kar
	 * */
	public QueryManager(){
		this.contextAvailable = false;
	}
	
	/**Parameterized constructor of {@link QueryManager}
	 * @author Debasis Kar
	 * @param cesDefinition
	 * */
	public QueryManager(TTaskCESDefinition cesDefinition){
		this();
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public TContextDefinitions queryRawContextData(TTaskCESDefinition cesDefinition) {	
		TContextDefinitions conSet = new TContextDefinitions();
		try{
			log.info("Connecting to Middleware...");
			//Fetch required contexts specified by the modeler
			List<TContextDefinition> contextList = cesDefinition.getIntention().getRequiredContexts().getContext();
			//Calling Database Manager for getting data from MongoDB instance
			ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESExecutorConfig.SPRING_BEAN);
			DynamicSelector databaseManager = (DynamicSelector) appContext.getBean(CESExecutorConfig.MONGO_NAMESPACE);
			conSet = databaseManager.getData(contextList);
    		this.contextAvailable = databaseManager.isContextAvailable();
			appContext.registerShutdownHook();
			appContext.close();
			log.info("Connection to Middleware is Closed.");
		} catch (NullPointerException e) {
			log.error("QUEMA11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("QUEMA10: Unknown Exception has Occurred - " + e);
		} finally {
			log.info("Context Acquisition is Finished.");
		}
		return conSet;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		//JAXB implementation for serializing the context data into a byte array.
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory();
		TContextDefinitions contextSet = this.queryRawContextData(this.cesDefinition);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//Perform serialization if context is available
		if(this.contextAvailable){
			try {
				//Serializing the received data from Middle-ware to byte[] for messaging to other modules.
				JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		        JAXBElement<TContextDefinitions> root = ipsmMaker.createContextDefinitions(contextSet);
				jaxbMarshaller.marshal(root, outputStream);
				log.info("Context Data is Available on Message Queue.");
		    	jaxbMarshaller.marshal(root, System.out);
			} catch (JAXBException e) {
				log.error("QUEMA02: JAXBException has Occurred.");
			} catch (NullPointerException e) {
				log.error("QUEMA01: NullPointerException has Occurred.");
			} catch (Exception e) {
				log.error("QUEMA00: Unknown Exception has Occurred - " + e);
			} 
		}
		return outputStream.toByteArray();
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		//Send output of Query Manager to Context Analyzer with relevant header information
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_SEND_SIGNAL);
        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_QUERYMANAGER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}

}