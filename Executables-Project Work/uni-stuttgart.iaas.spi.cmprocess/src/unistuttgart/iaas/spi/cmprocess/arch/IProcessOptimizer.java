package unistuttgart.iaas.spi.cmprocess.arch;

public interface IProcessOptimizer {
	public boolean isOptimizationAvailable();
	public void analyzeWeight();
	public void optimizeProcess();
}
