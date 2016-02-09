package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * A dummy Web Service call to show complementary maintenance process.
 * @author Debasis Kar
 */

public class MaintenanceDemo implements JavaDelegate{

	/**Any processing that needs to be carried out must be implemented in this Method which is defined 
	 * in JavaDelegate interface of Activiti Engine.
	 * @author Debasis Kar
	 * @param DelegateExecution
	 * @return void
	 * */
	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		//Request SOAP message to the Web Service based on some naive string
		if(delegateExecution.getCurrentActivityName().contains(Settings.SOAP_COMMAND_MAINTAIN)){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall(Settings.SOAP_COMMAND_MAINTAIN));
		}
	}

}