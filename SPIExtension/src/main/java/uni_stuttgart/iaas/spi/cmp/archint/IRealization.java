package uni_stuttgart.iaas.spi.cmp.archint;

import org.activiti.engine.ProcessEngine;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;

public interface IRealization {
	public TDataList startProcess(String fileName, ProcessEngine processEngine, TDataList input);
}
