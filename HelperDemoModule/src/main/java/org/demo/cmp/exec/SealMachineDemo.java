package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * A Dummy Web Service Call to Show Sealing Process.
 * @author Debasis Kar
 */

public class SealMachineDemo implements JavaDelegate {
	
	/**Any processing that needs to be carried out must be implemented in this Method which is defined 
	 * in JavaDelegate interface of Activiti Engine.
	 * @author Debasis Kar
	 * @param DelegateExecution
	 * @return void
	 * */
	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		 //Retrieve Workflow Engine, i.e., Activit Variable in Runtimes
		 String var = (String) delegateExecution.getVariable("packStatus");
		 var = var.toUpperCase();
		 System.err.println(var);
		 //Request SOAP Message to the Web Service based on some naive String
		 if(delegateExecution.getCurrentActivityName().contains("Seal")){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("seal"));
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("complete"));
		 }
	}

}
