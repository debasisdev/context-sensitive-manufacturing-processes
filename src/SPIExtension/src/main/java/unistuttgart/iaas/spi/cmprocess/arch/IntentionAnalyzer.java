package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
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
import unistuttgart.iaas.spi.cmprocess.interfaces.ICamelSerializer;
import unistuttgart.iaas.spi.cmprocess.interfaces.IDataRepository;
import unistuttgart.iaas.spi.cmprocess.interfaces.IProcessEliminator;

public class IntentionAnalyzer implements IProcessEliminator, IDataRepository, ICamelSerializer {
	
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
		} catch (NullPointerException e) {
			log.severe("Code - INTAN21: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("Code - INTAN20: Unknown Exception has Occurred.");
	    } finally{
			log.info("Overall " + this.intentionAnalysisPassedProcesses.getProcessDefinition().size() + 
					" Processes Passed Intention Analysis.");
		}
		return this.intentionAnalysisPassedProcesses;
	}

	@Override
	public File getContextRepository() {
		Properties propertyFile = new Properties();
    	InputStream inputReader = this.getClass().getClassLoader().getResourceAsStream("config.properties");
    	String fileName = null;
		if(inputReader != null){
	        try {
				propertyFile.load(inputReader);
				fileName = propertyFile.getProperty("CONTEXT_REPOSITORY");
		        inputReader.close();
			} catch (IOException e) {
				log.severe("Code - INTAN11: IOException has Occurred.");
			} catch (Exception e) {
				log.severe("Code - INTAN10: Unknown Exception has Occurred.");
		    } 
		}
		return new File(fileName);
	}

	@Override
	public File getProcessRepository(TTaskCESDefinition cesDefinition) {
		return new File(cesDefinition.getProcessRepository());
	}
	
	public byte[] getSerializedOutput(Exchange exchange){
		try {
			InputStream byteInputStream = new ByteArrayInputStream((byte[]) exchange.getIn().getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) unmarshaller.unmarshal(byteInputStream);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			this.intentionAnalysisPassedProcesses = this.eliminate(processSet, this.cesDefinition);
			if(this.intentionAnalysisPassedProcesses.getProcessDefinition().isEmpty()){
				this.intentionAnalysisPassedProcesses = processSet;
			} 
		} catch (NullPointerException e) {
			log.severe("Code - INTAN01: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.severe("Code - INTAN01: JAXBException has Occurred.");
		} catch (Exception e) {
			log.severe("Code - INTAN00: Unknown Exception has Occurred.");
	    } finally {
			log.info("Intention Analysis is Completed.");
		}
		return this.getSerializedProcessListOfAnalyzer();
	}
	
	public byte[] getSerializedProcessListOfAnalyzer() {
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ipsmMaker = new ObjectFactory();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        JAXBElement<TProcessDefinitions> processDefSet = ipsmMaker.createProcessDefinitions(this.intentionAnalysisPassedProcesses);
			jaxbMarshaller.marshal(processDefSet, outputStream);
		} catch(NullPointerException e){
			log.severe("Code - INTAN32: NullPointerException has Occurred.");
		} catch (JAXBException e) {
			log.severe("Code - INTAN31: JAXBException has Occurred.");
		} catch (Exception e) {
			log.severe("Code - INTAN30: Unknown Exception has Occurred.");
	    } 
		return outputStream.toByteArray();
	}
}
