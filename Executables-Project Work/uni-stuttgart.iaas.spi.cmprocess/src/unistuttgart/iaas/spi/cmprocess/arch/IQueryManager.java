package unistuttgart.iaas.spi.cmprocess.arch;

import unistuttgart.iaas.spi.cmprocess.cmp.TContexts;

public interface IQueryManager {
	/*Any Custom Query Manager has to implement queryRawContextData method which will
	 * take TContexts (a set of required contexts for a domain). The implementations 
	 * must look for the repository where the required contexts can be found such that
	 * they can be fetched and serialized in to an XML file named ContextData.*/
	public void queryRawContextData(TContexts requiredContexts);
}
