package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TCESIntentionDefinition;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcesses;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;


import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessEliminator;
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
 * A generic class that implements {@link IProcessEliminator}, {@link IDataSerializer}, and {@link Processor}.
 * This module analyzes the received {@link TRealizationProcesses} by filtering them with required main- and subgoals, 
 * @author Debasis Kar
 */

public class IntentionAnalyzer implements IProcessEliminator, IDataSerializer, Processor {
	
	/**Variable to store the {@link TRealizationProcesses} that pass intention analysis 
	 * @author Debasis Kar
	 * */
	private TRealizationProcesses intentionAnalysisPassedProcesses;
	
	/**Variable to store {@link TTaskCESDefinition}
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(IntentionAnalyzer.class);
	
	/**Default constructor of {@link IntentionAnalyzer}
	 * @author Debasis Kar
	 * */
	public IntentionAnalyzer(){
		this.intentionAnalysisPassedProcesses = null;
	}
	
	/**Parameterized constructor of {@link IntentionAnalyzer}
	 * @author Debasis Kar
	 * @param cesDefinition
	 * */
	public IntentionAnalyzer(TTaskCESDefinition cesDefinition){
		ObjectFactory cmpMaker = new ObjectFactory();
		this.intentionAnalysisPassedProcesses = cmpMaker.createTRealizationProcesses();
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public TRealizationProcesses eliminate(TRealizationProcesses processSet, TTaskCESDefinition cesDefinition){
		//Acquire intentions to be fulfilled by a process
		TCESIntentionDefinition intention = cesDefinition.getIntention();
		String mainIntention = intention.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName();
		//Make a set of required sub-intentions
		Set<String> subIntentions = new TreeSet<String>();
		for(TCESIntentionDefinition subIntention : intention.getSubIntentions().getCESIntentionDefinitions()){
			subIntentions.add(subIntention.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName());
		}
		try {
			log.info("Intention Analysis is Started by Deserializing the ProcessRepository.xml");
			//Scan each process definition and validate intentions
			for(TRealizationProcess processDefinition : processSet.getRealizationProcess()){
				Set<String> extraIntentions = new TreeSet<String>();
				//Match main intentions
				if(processDefinition.getIntention().getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName().equals(mainIntention)){
					List<TCESIntentionDefinition> subIntentionList = processDefinition.getIntention().getSubIntentions().getCESIntentionDefinitions();
					for(TCESIntentionDefinition intent : subIntentionList){
						extraIntentions.add(intent.getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName());
					}
					extraIntentions.retainAll(subIntentions);
				}
				//Match sub intentions by Set intersection operation
				if(extraIntentions.size()>0){
					log.info(processDefinition.getId() + " Passes Intention Analysis.");
					this.intentionAnalysisPassedProcesses.getRealizationProcess().add(processDefinition);
				}
			}
		} catch (NullPointerException e) {
			log.error("INTAN11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("INTAN10: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info("Overall " + this.intentionAnalysisPassedProcesses.getRealizationProcess().size() + " Processes Passed Intention Analysis.");
		}
		return this.intentionAnalysisPassedProcesses;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange){
		ObjectFactory cmpMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			//JAXB implementation for de-serializing the process definitions received from Context Analyzer
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TRealizationProcesses processSet = (TRealizationProcesses) rootElement.getValue();
			if(processSet.getRealizationProcess().isEmpty()){
				//Get process repository know-how
				ProcessRepository processRepository = new ProcessRepository();
				processSet = processRepository.getProcessRepository(this.cesDefinition);
			}
			//Perform intention analysis
			this.intentionAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			//JAXB implementation for serializing the Intention Analyzer output into byte array for message exchange
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TRealizationProcesses> processDefSet = cmpMaker.createRealizationProcesses(this.intentionAnalysisPassedProcesses);
			jaxbMarshaller.marshal(processDefSet, outputStream);
		} catch (NullPointerException e) {
			log.error("INTAN02: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.error("INTAN01: JAXBException has Occurred.");
		} catch (Exception e) {
			log.error("INTAN00: Unknown Exception has Occurred - " + e);
	    } finally {
			log.info("Intention Analysis is Completed.");
		}
		return outputStream.toByteArray();
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		//Send output of Intention Analyzer to Process Selector with relevant header information
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_SEND_SIGNAL);
        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_INTENTIONANALYZER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}

}
