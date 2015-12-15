package unistuttgart.iaas.spi.cmprocess.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import unistuttgart.iaas.spi.cmprocess.arch.CESExecutor;
import unistuttgart.iaas.spi.cmprocess.arch.ContextConfig;
import unistuttgart.iaas.spi.cmprocess.cmp.TCESTask;
import unistuttgart.iaas.spi.cmprocess.cmp.TContext;
import unistuttgart.iaas.spi.cmprocess.cmp.TContexts;
import unistuttgart.iaas.spi.cmprocess.cmp.TData;
import unistuttgart.iaas.spi.cmprocess.cmp.TDataList;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntentions;

public class MyTester {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		CESExecutor cesProcess = new CESExecutor("SealAndSortPackets, highAutomation, highThroughput",
				"shockDetectorStatus, infraredSensorStatus, unitsOrdered, availableWorkers");
		
		TCESTask cest = new TCESTask();
		cest.setDocumentation("MyDoc");
		cest.setName("Packaging");
		cest.setIsCommandAction(true);
		cest.setIsEventDriven(false);
		cest.setTargetNamespace("IAAS");
		TIntention ti = new TIntention();
		ti.setDocumentation("The primary goal/intention is to seal and sort the packets as per the business rule.");
		ti.setName("SealAndSortPackets");
		ti.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");

		TIntention t1 = new TIntention();
		t1.setDocumentation("Work must be done in such a way that no employees sit idle in the premises of company.");
		t1.setName("highHRUtilization");
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
		td1.setName("x");
		td1.setValue("10");
		TData td2 = new TData();
		td2.setName("y");
		td2.setValue("20");
		TDataList tio = new TDataList();
		tio.getDataList().add(td1);
		tio.getDataList().add(td2);
		TData td3 = new TData();
		td3.setName("a");
		TData td4 = new TData();
		td4.setName("b");
		TDataList toi = new TDataList();
		toi.getDataList().add(td3);
		toi.getDataList().add(td4);
		
		TContext tc = new TContext();
		tc.setName("shock");
		tc.setDocumentation("doccon");
		tc.setTargetNamespace("iaas/cmp");
		TContext tc1 = new TContext();
		tc1.setName("unit");
		tc1.setDocumentation("doccon");
		tc1.setTargetNamespace("iaas/cmp");
		TContext tc2 = new TContext();
		tc2.setName("infra");
		tc2.setDocumentation("doccon");
		tc2.setTargetNamespace("iaas/cmp");
		TContext tc3 = new TContext();
		tc3.setName("avail");
		tc3.setDocumentation("doccon");
		tc3.setTargetNamespace("iaas/cmp");
		TContexts tco = new TContexts();
		tco.getContext().add(tc);
		tco.getContext().add(tc1);
		tco.getContext().add(tc2);
		tco.getContext().add(tc3);
		
		cest.setIntention(ti);
		cest.setInputData(tio);
		cest.setOutputData(toi);
		cest.setRequiredContext(tco);
		
		JAXBContext jaxbContext = JAXBContext.newInstance("unistuttgart.iaas.spi.cmprocess.cmp");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(cest.getRequiredContext(), ContextConfig.TEST_DUMP);
		jaxbMarshaller.marshal(cest, System.out);
	}
}
