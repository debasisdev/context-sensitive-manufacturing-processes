package unistuttgart.iaas.spi.cmprocess.interfaces;

import java.io.File;

import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;

/**
 * A Generic Interface for Repository Access.
 * @author Debasis Kar
 */

public interface IDataRepository {
	/**
	 * This method must be implemented in order to read and writ to Context Repository of the application.
	 * @author Debasis Kar
	 * @param void
	 * @return File 
	 */
	public File getContextRepository();
	
	/**
	 * This method must be implemented in order to extract the Process Repository detail from TTaskCESDefinition.
	 * @author Debasis Kar
	 * @param TTaskCESDefinition
	 * @return File 
	 */
	public File getProcessRepository(TTaskCESDefinition cesDefinition);
	
}
