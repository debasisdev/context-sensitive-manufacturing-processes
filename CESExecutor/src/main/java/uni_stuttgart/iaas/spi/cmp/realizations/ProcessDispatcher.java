package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcess;
import de.uni_stuttgart.iaas.cmp.v0.TRealizationProcesses;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;

import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessEngine;
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
 * A generic class that implements {@link IProcessEngine}, {@link IDataSerializer}, and {@link Processor}.
 * This module deploys the main business process of the {@link TRealizationProcess} selected by {@link ProcessSelector}. 
 * @author Debasis Kar
 */

public class ProcessDispatcher implements IProcessEngine, IDataSerializer, Processor {
	
	/**Variable to store input to the main process model
	 * @author Debasis Kar
	 * */
	private TDataList inputData;
	
	/**Variable to store output holder
	 * @author Debasis Kar
	 * */
	private TDataList outputPlaceholder;
	
	/**Variable to store {@link TTaskCESDefinition} 
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(ProcessDispatcher.class);
	
	/**Default constructor of {@link ProcessDispatcher}
	 * @author Debasis Kar
	 * */
	public ProcessDispatcher() {
		this.inputData = null;
		this.outputPlaceholder = null;
	}
	
	/**Parameterized constructor of {@link ProcessDispatcher}
	 * @author Debasis Kar
	 * @param cesDefinition
	 * */
	public ProcessDispatcher(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
		this.inputData = this.cesDefinition.getInputData();
		this.outputPlaceholder = this.cesDefinition.getOutputVariable();
	}
	
	@Override
	public TDataList deployMainProcess(TRealizationProcess processDefinition) {
		try{
			//Fetch the process model path and process name of main process model
			String mainModel = null;
			NodeList nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
			for(int count=0; count < nodeList.getLength(); count++){
				if(nodeList.item(count).getNodeName().equals(CESExecutorConfig.REPOSITORY_FIELD_MAINMODEL)){
					mainModel = nodeList.item(count).getTextContent().trim();
				}
			}
			//Start deployment code for main process model
			log.info(mainModel + " will be Executed.");
			if(processDefinition.getProcessType().equals(CESExecutorConfig.BPMN_NAMESPACE)){
				//Activiti specific execution
				if(processDefinition.getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getTargetNamespace().equals(CESExecutorConfig.ACTIVITI_NAMESPACE)){
					ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESExecutorConfig.SPRING_BEAN);
					DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESExecutorConfig.ACTIVITI_NAMESPACE);
					this.outputPlaceholder = selectionProcessor.deployProcess(mainModel, processDefinition.getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName(), this.inputData, this.outputPlaceholder);
					appContext.registerShutdownHook();
					appContext.close();
				}
				//Add other BPMN engine specific code here (if required)
				else{
					log.info("Suitable BPMN Engine Not Found!!");
				}
			}
			//Add BPEL specific execution Here (if required)
			else {
				log.info("Suitable Workflow Engine Not Found!!");
			}
			//End deployment code for main process model
		} catch(Exception e) {
			log.error("PRODI10: Unknown Exception has Occurred - " + e);
			return this.outputPlaceholder;
		}
		return this.outputPlaceholder;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		de.uni_stuttgart.iaas.cmp.v0.ObjectFactory cmpMaker = new de.uni_stuttgart.iaas.cmp.v0.ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			log.info("Deployment is about to Start...");
			//JAXB implementation for de-serializing the process definition selected by Process Selector
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TRealizationProcess processDef = (TRealizationProcess) rootElement.getValue();
			//Perform execution of main business process
			this.outputPlaceholder = this.deployMainProcess(processDef);
			//Bind output to an envelope of TTaskCESDefinition
			TTaskCESDefinition cesDef = new TTaskCESDefinition();
			cesDef.setOutputVariable(this.outputPlaceholder);
			jaxbContext = JAXBContext.newInstance(cmpMaker.getClass());
			//JAXB implementation for serializing the Process Dispatcher output into byte array for message exchange
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<TTaskCESDefinition> rootElem = cmpMaker.createCESDefinition(cesDef);
			jaxbMarshaller.marshal(rootElem, outputStream);
			//Perform execution of complementary business process
			this.deployComplementaryProcess(processDef);
		} catch (NullPointerException e) {
			log.error("PRODI01: NullPointerException has Occurred.");
		} catch(Exception e) {
      		log.error("PRODI00: Unknown Exception has Occurred - " + e);
      	} finally{
			log.info("Process has been Dispatched and Deployed Successfully!!");
		}
		return outputStream.toByteArray();
	}
	
	@Override
	public boolean deployComplementaryProcess(TRealizationProcess processDefinition) {
		try{
			//Fetch the complementary process model details from process definition received
			String mainProcessNamespace = processDefinition.getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getTargetNamespace();
			ProcessRepository processRepository = new ProcessRepository();
			TRealizationProcesses processDefinitions = processRepository.getProcessRepository(this.cesDefinition);
			//Search through the process repository for the complementary process
			for(TRealizationProcess processDef : processDefinitions.getRealizationProcess()){
				//Match the intention and name-space
				if(processDef.getIntention().getInteractiveInitializableEntityDefinition().getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName().equals(CESExecutorConfig.REPOSITORY_FIELD_COMPLEMENTARYMODEL) && processDef./*.*/getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getTargetNamespace().equals(mainProcessNamespace)){
					String complementaryModel = null;
					//Fetch the process model path and process name of the complementary process definition
					NodeList nodeList = ((Node) processDef.getProcessContent().getAny()).getChildNodes();
					for(int count=0; count < nodeList.getLength(); count++){
						if(nodeList.item(count).getNodeName().equals(CESExecutorConfig.REPOSITORY_FIELD_MAINMODEL)){
							complementaryModel = nodeList.item(count).getTextContent().trim();
						}
					}
					//Start deployment code for complementary process model
					if(processDefinition.getProcessType().equals(CESExecutorConfig.BPMN_NAMESPACE)){
						//Activiti specific execution
						if(processDefinition.getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getTargetNamespace().equals(CESExecutorConfig.ACTIVITI_NAMESPACE)){
							ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESExecutorConfig.SPRING_BEAN);
							DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESExecutorConfig.ACTIVITI_NAMESPACE);
							selectionProcessor.deployProcess(complementaryModel, processDef.getInitializableEntityDefinition().getIdentifiableEntityDefinition().getEntityIdentity().getName(), this.inputData, this.outputPlaceholder);
							appContext.registerShutdownHook();
							appContext.close();
						}
						//Add other BPMN engine specific code here (if required)
						else{
							log.info("Suitable BPMN Engine Not Found!!");
						}
					}
					//Add BPEL specific execution Here (if required)
					else {
						log.info("Suitable Workflow Engine Not Found!!");
					}
					//End deployment code for complementary process model
				}
			}
		}
		catch(Exception e){
			log.error("PRODI20: Unknown Exception has Occurred - " + e);
			return false;
		}
		return true;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		//Send output of Process Dispatcher to the Result Queue with relevant header information
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESExecutorConfig.RABBIT_STOP_SIGNAL);
        headerData.put(CESExecutorConfig.RABBIT_STATUS, CESExecutorConfig.RABBIT_MSG_PROCESSDISPATCHER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}

}
