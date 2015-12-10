package unistuttgart.iaas.spi.cmprocess.test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import unistuttgart.iaas.spi.cmprocess.arch.ContextAnalyzer;
import unistuttgart.iaas.spi.cmprocess.arch.QueryManager;
import unistuttgart.iaas.spi.cmprocess.cmp.TContent;
import unistuttgart.iaas.spi.cmprocess.cmp.TContextExpression;
import unistuttgart.iaas.spi.cmprocess.cmp.TContextExpressions;
import unistuttgart.iaas.spi.cmprocess.cmp.TDefinition;

public class MyTester {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws JAXBException, DatatypeConfigurationException {
		QueryManager qum = new QueryManager("HowToSealAndSort","SealAndSortPackets");
		ContextAnalyzer can = new ContextAnalyzer(qum);
//		IntentionAnalyzer ian = new IntentionAnalyzer(can,qum);
		TContent tc = new TContent();
		tc.setExpression("72982#%#@8e8");
		TDefinition td = new TDefinition();
		td.setDefinitionLanguage("en_US");
		td.setDefinitionContent(tc);
		TContextExpression conExp = new TContextExpression();
		conExp.setDocumentation("It's a demo");
		conExp.setId("232");
		conExp.setName("manual");
		conExp.setTargetNamespace("iaas");
		conExp.getCondition().add(td);
		
		TContextExpressions tce = new TContextExpressions();
		tce.getContextExpression().add(conExp);
		JAXBContext jaxbContext = JAXBContext.newInstance("unistuttgart.iaas.spi.cmprocess.cmp");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		jaxbMarshaller.marshal(conExp, this.contextData);
		jaxbMarshaller.marshal(tce, System.out);
	}
}
