package uni_stuttgart.iaas.spi.cmp.archdev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import uni_stuttgart.iaas.spi.cmp.archint.ICamelSerializer;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessEliminator;
import uni_stuttgart.iaas.spi.cmp.archint.IProcessRepository;

public class IntentionAnalyzer implements IProcessEliminator, ICamelSerializer, IProcessRepository {
	
	private TProcessDefinitions intentionAnalysisPassedProcesses;
	private TTaskCESDefinition cesDefinition;
	private static final Logger log = Logger.getLogger(IntentionAnalyzer.class.getName());
	
	public IntentionAnalyzer(){
		this.intentionAnalysisPassedProcesses = null;
	}
	
	public IntentionAnalyzer(TTaskCESDefinition cesDefinition){
		ObjectFactory ipsmMaker = new ObjectFactory();
		this.intentionAnalysisPassedProcesses = ipsmMaker.createTProcessDefinitions();
		this.cesDefinition = cesDefinition;
	}
	
	@Override
	public TProcessDefinitions eliminate(TProcessDefinitions processSet, TTaskCESDefinition cesDefinition){
		TIntention intention = cesDefinition.getIntention();
		String mainIntention = intention.getName();
		Set<String> subIntentions = new TreeSet<String>();
		for(TSubIntention subIntention : intention.getSubIntentions().get(0).getSubIntention()){
			subIntentions.add(subIntention.getName());
		}
		try {
			Thread.sleep(5000);
			log.info("Intention Analysis is Started by Deserializing the ProcessRepository.xml");
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				Set<String> extraIntentions = new TreeSet<String>();
				if(processDefinition.getTargetIntention().getName().equals(mainIntention)){
					List<TSubIntention> subIntentionList = processDefinition.
							getTargetIntention().getSubIntentions().get(0).getSubIntention();
					for(TSubIntention intent : subIntentionList){
						extraIntentions.add(intent.getName());
					}
					extraIntentions.retainAll(subIntentions);
				}
				if(extraIntentions.size()>0){
					log.info(processDefinition.getId() + " Passes Intention Analysis.");
					this.intentionAnalysisPassedProcesses.getProcessDefinition().add(processDefinition);
				}
			}
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			log.severe("INAN12: InterruptedException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("INTAN11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("INTAN10: Unknown Exception has Occurred - " + e);
	    } finally{
			log.info("Overall " + this.intentionAnalysisPassedProcesses.getProcessDefinition().size() + 
					" Processes Passed Intention Analysis.");
		}
		return this.intentionAnalysisPassedProcesses;
	}
	
	public byte[] getSerializedOutput(Exchange exchange){
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			if(processSet.getProcessDefinition().isEmpty()){
				processSet = this.getProcessRepository(this.cesDefinition);
			}
			this.intentionAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			
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
	public TProcessDefinitions getProcessRepository(TTaskCESDefinition cesDefinition) {
		TProcessDefinitions processDefinitions = null;
		String fileName = cesDefinition.getDomainKnowHowRepository() + "\\ProcessRepository.xml";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(fileName));
			processDefinitions = (TProcessDefinitions) rootElement.getValue();
		} catch (JAXBException e) {
			log.severe("INTAN22: JAXBException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("INTAN21: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("INTAN20: Unknown Exception has Occurred - " + e);
		}
		return processDefinitions;
	}
}
