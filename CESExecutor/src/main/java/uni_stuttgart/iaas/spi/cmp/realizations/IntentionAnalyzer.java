package uni_stuttgart.iaas.spi.cmp.realizations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import uni_stuttgart.iaas.spi.cmp.interfaces.IDataSerializer;
import uni_stuttgart.iaas.spi.cmp.interfaces.IProcessEliminator;
import uni_stuttgart.iaas.spi.cmp.utils.CESExecutorConfig;

/**
 * A generic class that implements {@link IProcessEliminator}, {@link IDataSerializer}, and {@link Processor}.
 * This module analyzes the received {@link TProcessDefinitions} by filtering them with required main- and subgoals, 
 * @author Debasis Kar
 */

public class IntentionAnalyzer implements IProcessEliminator, IDataSerializer, Processor {
	
	/**Variable to store the {@link TProcessDefinitions} that pass intention analysis 
	 * @author Debasis Kar
	 * */
	private TProcessDefinitions intentionAnalysisPassedProcesses;
	
	/**Variable to store {@link TTaskCESDefinition}
	 * @author Debasis Kar
	 * */
	private TTaskCESDefinition cesDefinition;
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	/**Default constructor of {@link IntentionAnalyzer}
	 * @author Debasis Kar
	 * */
	public IntentionAnalyzer(){
		this.intentionAnalysisPassedProcesses = null;
	}
	
	/**Parameterized constructor of {@link IntentionAnalyzer}
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * */
	public IntentionAnalyzer(TTaskCESDefinition cesDefinition){
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.intentionAnalysisPassedProcesses = ipsmMaker.createTProcessDefinitions();
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public TProcessDefinitions eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition){
		//Acquire intentions to be fulfilled by a process
		TIntention intention = cesDefinition.getIntention();
		String mainIntention = intention.getName();
		//Make a set of required sub-intentions
		Set<String> subIntentions = new TreeSet<String>();
		for(TSubIntention subIntention : intention.getSubIntentions().get(0).getSubIntention()){
			subIntentions.add(subIntention.getName());
		}
		try {
			log.info("Intention Analysis is Started by Deserializing the ProcessRepository.xml");
			//Scan each process definition and validate intentions
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				Set<String> extraIntentions = new TreeSet<String>();
				//Match main intentions
				if(processDefinition.getTargetIntention().getName().equals(mainIntention)){
					List<TSubIntention> subIntentionList = processDefinition.getTargetIntention().getSubIntentions().get(0).getSubIntention();
					for(TSubIntention intent : subIntentionList){
						extraIntentions.add(intent.getName());
					}
					extraIntentions.retainAll(subIntentions);
				}
				//Match sub intentions by Set intersection operation
				if(extraIntentions.size()>0){
					log.info(processDefinition.getId() + " Passes Intention Analysis.");
					this.intentionAnalysisPassedProcesses.getProcessDefinition().add(processDefinition);
				}
			}
		} catch (NullPointerException e) {
			log.severe("INTAN11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("INTAN10: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info("Overall " + this.intentionAnalysisPassedProcesses.getProcessDefinition().size() + " Processes Passed Intention Analysis.");
		}
		return this.intentionAnalysisPassedProcesses;
	}
	
	@Override
	public byte[] getSerializedOutput(Exchange exchange){
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			//JAXB implementation for de-serializing the process definitions received from Context Analyzer
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			if(processSet.getProcessDefinition().isEmpty()){
				//Get process repository know-how
				ProcessRepository processRepository = new ProcessRepository();
				processSet = processRepository.getProcessRepository(this.cesDefinition);
			}
			//Perform intention analysis
			this.intentionAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			//JAXB implementation for serializing the Intention Analyzer output into byte array for message exchange
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinitions> processDefSet = ipsmMaker.createProcessDefinitions(this.intentionAnalysisPassedProcesses);
			jaxbMarshaller.marshal(processDefSet, outputStream);
		} catch (NullPointerException e) {
			log.severe("INTAN02: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.severe("INTAN01: JAXBException has Occurred.");
		} catch (Exception e) {
			log.severe("INTAN00: Unknown Exception has Occurred - " + e);
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
