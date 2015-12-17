package unistuttgart.iaas.spi.cmprocess.arch;

/**
 * A Generic Interface for Process Optimizer Module.
 * @author Debasis Kar
 */
public interface IProcessOptimizer {
	public boolean isOptimizationAvailable();
	public void analyzeWeight();
	public void optimizeProcess();
}
