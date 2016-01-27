package org.activiti.designer.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import uni_stuttgart.iaas.spi.cmp.archint.IRealization;

public class POPT01 implements IRealization{
	
	private static final Logger log = Logger.getLogger(POPT01.class.getName());

	public TDataList startProcess(String filePath, TDataList input, TDataList outputHolder) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		try {
			repositoryService.createDeployment().addInputStream("optimizationprocess.bpmn20.xml",
					new FileInputStream(filePath)).deploy();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			Map<String, Object> variableMap = new HashMap<String, Object>();
			/*Process Inputs*/
			variableMap.put("orderID", "OD153728DE");
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("optimizationprocess", variableMap);
			log.info("<ID:" + processInstance.getId() + ">");
		} catch (FileNotFoundException e) {
			log.severe("POPT.01.02: FileNotFoundException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("POPT.01.01: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("POPT.01.00: Unknown Exception has Occurred - " + e);
		}
		return outputHolder;
	}
}