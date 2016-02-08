package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessSelector;
import uni_stuttgart.iaas.spi.cmp.utils.CESConfigurations;

public class ProcessSelector implements IProcessSelector, IDataSerializer, Processor {
	private TProcessDefinition dispatchedProcess;
	private TTaskCESDefinition cesDefinition;
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(ProcessSelector.class.getName());
	
	public ProcessSelector() {
		this.dispatchedProcess = null;
		this.cesDefinition = null;
	}
	
	public ProcessSelector(TTaskCESDefinition cesDefinition) {
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.dispatchedProcess = ipsmMaker.createTProcessDefinition();
		this.cesDefinition = cesDefinition;
		log.info("Process Selector is About to Begin...");
	}
	
	@Override
	public TProcessDefinition selectProcess(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition){
		log.info("Selection by Strategy analysis is being done.");
		try {			
			List<TProcessDefinition> processDefinitionList = new LinkedList<TProcessDefinition>();
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				processDefinitionList.add(processDefinition);
			}
			String algoType = cesDefinition.getIntention().getSubIntentions().get(0).getSubIntentionRelations();
			ConfigurableApplicationContext appContext = new ClassPathXmlApplicationContext(CESConfigurations.SPRING_BEAN);
			DynamicSelector selectionProcessor = (DynamicSelector) appContext.getBean(algoType);
			this.dispatchedProcess = selectionProcessor.getRealizationProcess(processDefinitionList);
			appContext.registerShutdownHook();
			appContext.close();
			log.info(this.dispatchedProcess.getId() + " Is Selected for the Realization of Business Ojective.");
		} catch (NullPointerException e){
			log.severe("PROSE11: NullPointerException has Occurred.");
		} catch (Exception e){
			log.severe("PROSE10: Unknown Exception has Occurred - " + e);
		}
		return this.dispatchedProcess;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange){
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.dispatchedProcess = this.selectProcess(processSet, this.cesDefinition);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinition> processDef = ipsmMaker.createProcessDefinition(this.dispatchedProcess);
			jaxbMarshaller.marshal(processDef, outputStream);
		} catch (NullPointerException e) {
			log.severe("PROSE02: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.severe("PROSE01: JAXBException has Occurred.");
		} catch (Exception e) {
			log.severe("PROSE00: Unknown Exception has Occurred - " + e);
	    } finally {
			log.info("Intention Analysis is Completed.");
		}
		return outputStream.toByteArray();
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Object> headerData = new HashMap<>();
        headerData.put(RabbitMQConstants.ROUTING_KEY, CESConfigurations.RABBIT_SEND_SIGNAL);
        headerData.put(CESConfigurations.RABBIT_STATUS, CESConfigurations.RABBIT_MSG_PROCESSSELECTOR);
		exchange.getIn().setBody(this.getSerializedOutput(exchange));
		exchange.getIn().setHeaders(headerData);
	}
	
}
