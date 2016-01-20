package unistuttgart.iaas.spi.cmprocess.test;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import de.uni_stuttgart.iaas.cmp.v0.ObjectFactory;
import de.uni_stuttgart.iaas.cmp.v0.TData;
import de.uni_stuttgart.iaas.cmp.v0.TDataList;
import de.uni_stuttgart.iaas.cmp.v0.TTaskCESDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TContext;
import de.uni_stuttgart.iaas.ipsm.v0.TContexts;
import de.uni_stuttgart.iaas.ipsm.v0.TIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinition;
import de.uni_stuttgart.iaas.ipsm.v0.TProcessDefinitions;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntention;
import de.uni_stuttgart.iaas.ipsm.v0.TSubIntentions;
import unistuttgart.iaas.spi.cmprocess.arch.CESExecutor;

public class DemoRunner {

	public static void main(String[] args) throws Exception {
	 	TIntention ti = new TIntention();
		ti.setName("SealAndSortPackets");
		TSubIntention tsa = new TSubIntention();
		tsa.setName("highAutomation");
		TSubIntention tsb = new TSubIntention();
		tsb.setName("highThroughput");
		TSubIntentions tsi = new TSubIntentions();
		tsi.setSubIntentionRelations("http://www.uni-stuttgart.de/ipsm/intention/selections/weight-based");
		tsi.getSubIntention().add(tsa);
		tsi.getSubIntention().add(tsb);
		ti.getSubIntentions().add(tsi);
		
		TDataList tio = new TDataList();
		TData td1 = new TData();
		td1.setName("Worker");
		td1.setValue("John");
		tio.getDataList().add(td1);
		TData td4 = new TData();
		td4.setName("TaskOutput");
		td4.setValue("");
		TDataList toi = new TDataList();
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
		
//		TManufacturingContent tcx = ob.createTManufacturingContent();
//		tcx.setExpression("//Context[@name='unitsOrdered']/ContextDefinition/DefinitionContent[SenseValue<=1000]/SenseValue/text()");
//		TContent tcxx = new TContent();
//		tcxx.setAny(ob.createManufacturingContent(tcx));
//		TDefinition tdx = new TDefinition();
//		tdx.setDefinitionLanguage("http://www.w3.org/TR/xpath");
//		tdx.setDefinitionContent(tcxx);
//		TContext conExp1 = new TContext();
//		conExp1.setDocumentation("This context is required to decide this process is okay for the purpose or not.");
//		conExp1.setName("CON002");
//		conExp1.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
//		conExp1.getContextDefinition().add(tdx);
//		
//		TManufacturingContent tcx1 = ob.createTManufacturingContent();
//		tcx1.setExpression("//Context[@name='unitsOrdered']/ContextDefinition/DefinitionContent[SenseValue<=1000]/SenseValue/text()");
//		TContent tcxx1 = new TContent();
//		tcxx1.setAny(ob.createManufacturingContent(tcx1));
//		TDefinition tdx1 = new TDefinition();
//		tdx.setDefinitionLanguage("http://www.w3.org/TR/xpath");
//		tdx.setDefinitionContent(tcxx);
		TContext conExp2 = new TContext();
		conExp2.setDocumentation("This context is required to decide this process is okay for the purpose or not.");
		conExp2.setName("CON002");
		conExp2.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
//		conExp2.getContextDefinition().add(tdx);
		
		TContexts tco1 = new TContexts();
//		tco1.getContext().add(conExp1);
		tco1.getContext().add(conExp2);
		
		de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory ox = new de.uni_stuttgart.iaas.ipsm.v0.ObjectFactory();
		TProcessDefinitions rtpd = ox.createTProcessDefinitions();
		TProcessDefinition tpd = new TProcessDefinition();
		tpd.setInitialContexts(tco1);
		rtpd.getProcessDefinition().add(tpd);
		Properties propertyFile = new Properties();
    	InputStream inputReader = DemoRunner.class.getClassLoader().getResourceAsStream("config.properties");
    	String fileName = null;
		if(inputReader != null){
			propertyFile.load(inputReader);
			fileName = propertyFile.getProperty("TEST_DUMP");
	        inputReader.close();
		}
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		JAXBElement<TTaskCESDefinition> root = ob.createCESDefinition(cesDefinition);
//		JAXBElement<TProcessDefinitions> root = ox.createProcessDefinitions(rtpd);
		jaxbMarshaller.marshal(root, new File(fileName));
		

		CESExecutor cesProcess = new CESExecutor(cesDefinition);
		System.out.println(cesProcess.hashCode());
		
//		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
//		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//		JAXBElement<?> rootElement = (JAXBElement<?>) jaxbUnmarshaller.unmarshal(new File(ContextConfig.PROCESS_REPOSITORY));
//		TProcessDefinitions processSet = (TProcessDefinitions) rootElement.getValue();
		
		
//		JAXBContext jaxbContext1 = JAXBContext.newInstance(ObjectFactory.class);
//		Unmarshaller jaxbMarshaller1 = jaxbContext1.createUnmarshaller();
//		JAXBElement<?> root1 = (JAXBElement<?>) jaxbMarshaller1.unmarshal(new File("src/main/resources/datarepos/ContextData.xml"));
//		TContexts processSet = (TContexts) root1.getValue();
//		ByteArrayOutputStream b = new ByteArrayOutputStream();
//        ObjectOutputStream o = new ObjectOutputStream(b);
//        o.writeObject(processSet);
//		System.out.println(b.toByteArray());
	}
}
