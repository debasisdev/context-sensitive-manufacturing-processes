package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcesses;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;

import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessSelector;
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
 * A generic class that implements {@link IProcessSelector}, {@link IDataSerializer}, and {@link Processor}.
 * This module filters the received {@link TRealizationProcesses } with some predefined strategy. 
 * @author Debasis Kar
 */

public class ProcessSelector implements IProcessSelector, IDataSerializer, Processor {
	
	/**Variable to store the {@link TRealizationProcesses } that pass intention analysis 
	 * @author Debasis Kar
	 * */
	private TRealizationProcess dispatchedProcess;
	
	/**Variable to store {@link TTaskCESDefinition} 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(ProcessSelector.class);
	
	/**Default constructor of {@link ProcessSelector}
	 * @author Debasis Kar
	 * */
	public ProcessSelector() {
		this.dispatchedProcess = null;
		this.cesDefinition = null;
	}
	
	/**Parameterized constructor of {@link ProcessSelector}
	 * @author Debasis Kar
	 * @param cesDefinition
	 * */
	public ProcessSelector(TTaskCESDefinition cesDefinition) {
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.dispatchedProcess = ipsmMaker.createTRealizationProcess();
		this.cesDefinition = cesDefinition;
		log.info("Process Selector is About to Begin...");
	}
	
	@Override
	public TRealizationProcess selectProcess(TRealizationProcesses  processSet, TTaskCESDefinition cesDefinition){
		log.info("Selection by Strategy analysis is being done.");
		try {			
			//Make a list of all process definitions received
			List<TRealizationProcess> processDefinitionList = new LinkedList<TRealizationProcess>();
			for(TRealizationProcess processDefinition : processSet.getRealizationProcess()){
				processDefinitionList.add(processDefinition);
			}
			//Select the strategy, i.e., algorithm for the selection of process defintion
			String algoType = cesDefinition.getIntention().getSelectionStrategy();
			ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESExecutorConfig.SPRING_BEAN);
			DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(algoType);
			//Store the selected process definition
			this.dispatchedProcess = selectionProcessor.getRealizationProcess(processDefinitionList);
			appContext.registerShutdownHook();
			appContext.close();
			log.info(this.dispatchedProcess.getId() + " Is Selected for the Realization of Business Ojective.");
		} catch (NullPointerException e){
			log.error("PROSE11: NullPointerException has Occurred.");
		} catch (Exception e){
			log.error("PROSE10: Unknown Exception has Occurred - " + e);
		}
		return this.dispatchedProcess;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange){
		ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			//JAXB implementation for de-serializing the process definitions received from Intention Analyzer
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TRealizationProcesses  processSet = (TRealizationProcesses ) rootElement.getValue();
			//Perform Selection based on some strategy
			this.dispatchedProcess = this.selectProcess(processSet, this.cesDefinition);
			//JAXB implementation for serializing the Process Selector output into byte array for message exchange
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TRealizationProcess> processDef = ipsmMaker.createRealizationProcess(this.dispatchedProcess);
			jaxbMarshaller.marshal(processDef, outputStream);
		} catch (NullPointerException e) {
			log.error("PROSE02: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.error("PROSE01: JAXBException has Occurred.");
		} catch (Exception e) {
			log.error("PROSE00: Unknown Exception has Occurred - " + e);
	    } finally {
			log.info("Intention Analysis is Completed.");
		}
		return outputStream.toByteArray();
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		//Send output of Intention Analyzer to Process Optimizer with relevant header information
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_SEND_SIGNAL);
        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_PROCESSSELECTOR);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}
	
}
