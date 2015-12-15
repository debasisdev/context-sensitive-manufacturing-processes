package unistuttgart.iaas.spi.cmprocess.arch;

import java.util.Set;

import unistuttgart.iaas.spi.cmprocess.cmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.cmp.TProcessSet;

public interface IIntentionAnalyzer {
	/**/
	public Set<String> analyzeIntention(TProcessSet processSet, TIntention mainIntention);
	
	/**/
	public Set<String> getProcessListOfIntentionAnalyzer(Set<String> processesFromContextAnalyzer);
}
