package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory;
import de.uni_stuttgart.iaas.ipsm.v0.TDataList;
import de.uni_stuttgart.iaas.ipsm.v0.TManufacturingContent;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;

public class DeploymentManager implements IProcessEngine {
	private File processRepository;
	private String processId;
	private TProcessDefinition goalProcessDefinition;
	private TDataList inputData;
	private TDataList outputPlaceholder;
	private Object[] output;
	private static final Logger log = Logger.getLogger(DeploymentManager.class.getName());
	
	public DeploymentManager() {
		this.processRepository = null;
		this.processId = null;
		this.goalProcessDefinition = null;
		this.inputData = null;
		this.outputPlaceholder = null;
		this.output = null;
	}
	
	public DeploymentManager(TDataList input, TDataList output, String processIdentifier){
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.processId = processIdentifier;
		this.inputData = input;
		this.outputPlaceholder = output;
		try {
			log.info("Deployment is about to Start...");
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(this.processRepository);
			TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
			int localIndex = 0;
			for(TProcessDefinition processDefinition : processSet.getProcessDefinition()){
				if(processDefinition.getId().equals(this.processId)){
					break;
				}
				localIndex++;
			}
			log.info("Process Found for Optimization...");
			this.goalProcessDefinition = processSet.getProcessDefinition().get(localIndex);
			this.output = this.deployProcess(this.goalProcessDefinition);
		} catch (JAXBException e) {
			log.severe("JAXBException has occurred at Line in Process Optimizer!");
		} catch (NullPointerException e) {
			log.severe("NullPointerException has occurred at Line in Process Optimizer!");
		} catch (Exception e) {
			log.severe("Unknown Exception has occurred in Process Optimizer!\n" + e.getMessage());
		} finally{
			log.info("Process Has Been Deployd and Executed Successfully!!");
			log.info("CES Task Completed!!");
		}
	}
	
	@Override
	public Object[] deployProcess(TProcessDefinition processDefinition) {
		JAXBElement<?> optProcessContent = (JAXBElement<?>) processDefinition.getProcessContent().getAny();
		//Start Deployment Code for Main Model
		log.info(((TManufacturingContent) optProcessContent.getValue()).getMainModel() + " Will Be Executed");
		//End Deployment Code for Main Model
		//Start Deployment Code for Complementary Model
		log.info(((TManufacturingContent) optProcessContent.getValue()).getComplementaryModel() + " Will Be Executed");
		//End Deployment Code for Complementary Model
		return null;
	}

	public Object[] getOutput() {
		return output;
	}

	public void setOutput(Object[] output) {
		this.output = output;
	}

	public TDataList getInputData() {
		return inputData;
	}

	public TDataList getOutputPlaceholder() {
		return outputPlaceholder;
	}

}
