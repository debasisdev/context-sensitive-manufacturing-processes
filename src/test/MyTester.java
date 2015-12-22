package unistuttgart.iaas.spi.cmprocess.test;

import unistuttgart.iaas.spi.cmprocess.arch.CESExecutor;
import unistuttgart.iaas.spi.cmprocess.arch.ContextConfig;
import unistuttgart.iaas.spi.cmprocess.tcmp.TContext;
import unistuttgart.iaas.spi.cmprocess.tcmp.TContexts;
import unistuttgart.iaas.spi.cmprocess.tcmp.TData;
import unistuttgart.iaas.spi.cmprocess.tcmp.TDataList;
import unistuttgart.iaas.spi.cmprocess.tcmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.tcmp.TIntentions;
import unistuttgart.iaas.spi.cmprocess.tcmp.TTaskCESDefinition;

public class MyTester {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		TIntention ti = new TIntention();
		ti.setDocumentation("The primary goal/intention is to seal and sort the packets as per the business rule.");
		ti.setName("SealAndSortPackets");
		ti.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
		TIntention t1 = new TIntention();
		t1.setDocumentation("Work must be automated more to gain higher productivity.");
		t1.setName("highAutomation");
		t1.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
		TIntention t2 = new TIntention();
		t2.setDocumentation("Work must be done in such a way that no employees sit idle in the premises of company.");
		t2.setName("highThroughput");
		t2.setTargetNamespace("Throughput time must be increased to remain competitive in the market.");
		TIntentions tis = new TIntentions();
		tis.getIntention().add(t1);
		tis.getIntention().add(t2);
		ti.setSubIntentions(tis);
		
		TData td1 = new TData();
		td1.setName("xInput");
		td1.setValue("10");
		TData td2 = new TData();
		td2.setName("yInput");
		td2.setValue("20");
		TDataList tio = new TDataList();
		tio.getDataList().add(td1);
		tio.getDataList().add(td2);
		TData td3 = new TData();
		td3.setName("xOutput");
		TData td4 = new TData();
		td4.setName("yOutput");
		TDataList toi = new TDataList();
		toi.getDataList().add(td3);
		toi.getDataList().add(td4);
		
		TContext tc = new TContext();
		tc.setName("shockDetectorStatus");
		tc.setDocumentation(ContextConfig.SHOCKDETECTORSENSOR_DOC);
		tc.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v0.1/");
		TContext tc1 = new TContext();
		tc1.setName("unitsOrdered");
		tc1.setDocumentation(ContextConfig.UNITSORDERED_DOC);
		tc1.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v0.1/");
		TContext tc2 = new TContext();
		tc2.setName("infraredSensorStatus");
		tc2.setDocumentation(ContextConfig.INFRAREDSENSOR_DOC);
		tc2.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v0.1/");
		TContext tc3 = new TContext();
		tc3.setName("availableWorkers");
		tc3.setDocumentation(ContextConfig.GPSLOCATION_DOC);
		tc3.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v0.1/");
		TContexts tco = new TContexts();
		tco.getContext().add(tc);
		tco.getContext().add(tc1);
		tco.getContext().add(tc2);
		tco.getContext().add(tc3);
		
		TTaskCESDefinition cesDefinition = new TTaskCESDefinition();
		cesDefinition.setDocumentation("It's a dynamic process selector.");
		cesDefinition.setName("Packaging");
		cesDefinition.setIsCommandAction(true);
		cesDefinition.setIsEventDriven(false);
		cesDefinition.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
		cesDefinition.setIntention(ti);
		cesDefinition.setInputData(tio);
		cesDefinition.setOutputData(toi);
		cesDefinition.setRequiredContexts(tco);
		
		CESExecutor cesProcess = new CESExecutor(cesDefinition);

	}
}