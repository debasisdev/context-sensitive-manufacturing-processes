package unistuttgart.iaas.spi.cmprocess.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntentions;
import de.uni_stuttgart.iaas.ipsm.v0.TTypeOfSubInstentions;
import unistuttgart.iaas.spi.cmprocess.arch.CESExecutor;

public class TestBench {
	 @Test
	  public void multiplicationOfZeroIntegersShouldReturnZero() {
		 	TIntention ti = new TIntention();
			ti.setName("SealAndSortPackets");
			TSubIntention tsa = new TSubIntention();
			tsa.setName("highAutomation");
			TSubIntention tsb = new TSubIntention();
			tsb.setName("highThroughput");
			TSubIntentions tsi = new TSubIntentions();
			tsi.setSubIntentionRelations(TTypeOfSubInstentions.AND);
			tsi.getSubIntention().add(tsa);
			tsi.getSubIntention().add(tsb);
			ti.getSubIntentions().add(tsi);
			
			TDataList tio = new TDataList();
			TData td1 = new TData();
			td1.setName("Worker");
			td1.setValue("John");
			tio.getDataList().add(td1);
			TData td3 = new TData();
			td3.setName("FinalSatusOfRun");
			td3.setValue("");
			TData td4 = new TData();
			td4.setName("TaskOutput");
			td4.setValue("");
			TDataList toi = new TDataList();
			toi.getDataList().add(td3);
			toi.getDataList().add(td4);
			
			TContext tc = new TContext();
			tc.setName("shockDetectorStatus");
			TContext tc1 = new TContext();
			tc1.setName("unitsOrdered");
			TContext tc2 = new TContext();
			tc2.setName("infraredSensorStatus");
			TContext tc3 = new TContext();
			tc3.setName("availableWorkers");
			TContexts tco = new TContexts();
			tco.getContext().add(tc);
			tco.getContext().add(tc1);
			tco.getContext().add(tc2);
			tco.getContext().add(tc3);
			
			ObjectFactory ob = new ObjectFactory();
			TTaskCESDefinition cesDefinition = ob.createTTaskCESDefinition();
			cesDefinition.setName("Packaging");
			cesDefinition.setIsCommandAction(true);
			cesDefinition.setIsEventDriven(false);
			cesDefinition.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
			cesDefinition.setOptimizationRequired(true);
			cesDefinition.setProcessRepository("src/main/resources/processrepos/ProcessRepository.xml");
			cesDefinition.setIntention(ti);
			cesDefinition.setInputData(tio);
			cesDefinition.setOutputVariable(toi);
			cesDefinition.setRequiredContexts(tco);
			
			CESExecutor cesProcess = new CESExecutor(cesDefinition);
			assertEquals("PRS001 Will Be Executed" ,true,cesProcess.getResult().equals("PRS001"));
	  }
}
