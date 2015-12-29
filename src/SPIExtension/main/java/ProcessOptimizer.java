package unistuttgart.iaas.spi.cmprocess.arch;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

public class ProcessOptimizer implements IProcessOptimizer {
	private File contextRepository;
	private File processRepository;
	private Set<String> listOfProcesses;
	private boolean contextAvailable;
	private static final Logger log = Logger.getLogger(ProcessOptimizer.class.getName());
	
	public ProcessOptimizer(){
		this.contextRepository = null;
		this.processRepository = null;
		this.listOfProcesses = null;
		this.contextAvailable = false;
	}
	
	public ProcessOptimizer(Set<String> listOfProcesses, boolean contextAvailable){
		this.contextRepository = new File(ContextConfig.CONTEXT_REPOSITORY);
		this.processRepository = new File(ContextConfig.PROCESS_REPOSITORY);
		this.listOfProcesses = listOfProcesses;
		this.contextAvailable = contextAvailable;
	}
	
	public boolean isOptimizationAvailable(){
		return contextAvailable;
	}

	public void optimizeProcess() {
		
	}

	@Override
	public void analyzeWeight() {
		// TODO Auto-generated method stub
		
	}
}
