package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessEngine;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessRepository;
import uni_stuttgart.iaas.spi.cmp.utils.CESConfigurations;

public class ProcessDispatcher implements IProcessEngine, IDataSerializer, IProcessRepository, Processor {
	
	private TDataList inputData;
	private TDataList outputPlaceholder;
	private TTaskCESDefinition cesDefinition;
	private static final Logger log = Logger.getLogger(ProcessDispatcher.class.getName());
	
	public ProcessDispatcher() {
		this.inputData = null;
		this.outputPlaceholder = null;
	}
	
	public ProcessDispatcher(TTaskCESDefinition cesDefinition){
		this.cesDefinition = cesDefinition;
		this.inputData = this.cesDefinition.getInputData();
		this.outputPlaceholder = this.cesDefinition.getOutputVariable();
	}
	
	@Override
	public TDataList deployMainProcess(TProcessDefinition processDefinition) {
		String mainModel = null;
		NodeList nodeList = ((Node) processDefinition.getProcessContent().getAny()).getChildNodes();
		for(int count=0; count < nodeList.getLength(); count++){
			if(nodeList.item(count).getNodeName().equals(CESConfigurations.REPOSITORY_FIELD_MAINMODEL)){
				mainModel = nodeList.item(count).getTextContent().trim();
			}
		}
		//Start Deployment Code for Main Model
		log.info(mainModel + " will be Executed.");
		if(processDefinition.getProcessType().equals(CESConfigurations.BPMN_NAMESPACE)){
			if(processDefinition.getTargetNamespace().equals(CESConfigurations.ACTIVITI_NAMESPACE)){
				ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESConfigurations.SPRING_BEAN);
				DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESConfigurations.ACTIVITI_NAMESPACE);
				this.outputPlaceholder = selectionProcessor.deployProcess(mainModel, processDefinition.getName(), this.inputData, this.outputPlaceholder);
				appContext.registerShutdownHook();
				appContext.close();
			}
			else{
				log.info("Suitable BPMN Engine Not Found!!");
			}
		}
		else {
			log.info("Suitable Workflow Engine Not Found!!");
		}
		//End Deployment Code for Main Model
		return this.outputPlaceholder;
	}

	@Override
	public byte[] getSerializedOutput(Exchange exchange) {
		de.uni_stuttgart.iaas.cmp.v0.ObjectFactory cmpMaker = new de.uni_stuttgart.iaas.cmp.v0.ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			log.info("Deployment is about to Start...");
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinition processDef = (TProcessDefinition) rootElement.getValue();
			this.outputPlaceholder = this.deployMainProcess(processDef);
			this.deployComplementaryProcess(processDef);
			TTaskCESDefinition cesDef = new TTaskCESDefinition();
			cesDef.setOutputVariable(this.outputPlaceholder);
			jaxbContext = JAXBContext.newInstance(cmpMaker.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<TTaskCESDefinition> rootElem = cmpMaker.createCESDefinition(cesDef);
			jaxbMarshaller.marshal(rootElem, outputStream);
		} catch (NullPointerException e) {
			log.severe("PRODI01: NullPointerException has Occurred.");
		} catch(Exception e) {
      		log.severe("PRODI00: Unknown Exception has Occurred - " + e);
      	} finally{
			log.info("Process has been Dispatched and Deployed Successfully!!");
			log.info("CES Task Completed!!");
		}
		return outputStream.toByteArray();
	}
	
	@Override
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition) {
		TProcessDefinitions processDefinitions = null;
		String repositoryType = cesDefinition.getDomainKnowHowRepositoryType();
		String fileName = null;
		if(repositoryType.equals(CESConfigurations.XML_EXTENSION)){
			fileName = cesDefinition.getDomainKnowHowRepository();
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(fileName));
			processDefinitions = (TProcessDefinitions) rootElement.getValue();
		} catch (JAXBException e) {
			log.severe("PRODI32: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("PRODI31: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("PRODI30: Unknown Exception has Occurred - " + e);
		}
		return processDefinitions;
	}

	@Override
	public boolean deployComplementaryProcess(TProcessDefinition processDefinition) {
		try{
			//Start Deploying Complementary Process
			String mainProcessNamespace = processDefinition.getTargetNamespace();
			TProcessDefinitions processDefinitions = this.getProcessRepository(this.cesDefinition);
			for(TProcessDefinition processDef : processDefinitions.getProcessDefinition()){
				if(processDef.getTargetIntention().getName().equals(CESConfigurations.REPOSITORY_FIELD_COMPLEMENTARYMODEL) && processDef.getTargetNamespace().equals(mainProcessNamespace)){
					String complementaryModel = null;
					NodeList nodeList = ((Node) processDef.getProcessContent().getAny()).getChildNodes();
					for(int count=0; count < nodeList.getLength(); count++){
						if(nodeList.item(count).getNodeName().equals(CESConfigurations.REPOSITORY_FIELD_MAINMODEL)){
							complementaryModel = nodeList.item(count).getTextContent().trim();
						}
					}
					if(processDefinition.getProcessType().equals(CESConfigurations.BPMN_NAMESPACE)){
						if(processDefinition.getTargetNamespace().equals(CESConfigurations.ACTIVITI_NAMESPACE)){
							ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESConfigurations.SPRING_BEAN);
							DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(CESConfigurations.ACTIVITI_NAMESPACE);
							this.outputPlaceholder = selectionProcessor.deployProcess(complementaryModel, processDef.getName(), this.inputData, this.outputPlaceholder);
							appContext.registerShutdownHook();
							appContext.close();
						}
						else{
							log.info("Suitable BPMN Engine Not Found!!");
						}
					}
					else {
						log.info("Suitable Workflow Engine Not Found!!");
					}
				}
			}
		}
		catch(Exception e){
			log.severe("PRODI20: Unknown Exception has Occurred - " + e);
			return false;
		}
		return true;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESConfigurations.RABBIT_STOP_SIGNAL);
        headerData.put(CESConfigurations.RABBIT_STATUS, CESConfigurations.RABBIT_MSG_PROCESSDISPATCHER);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}

}
