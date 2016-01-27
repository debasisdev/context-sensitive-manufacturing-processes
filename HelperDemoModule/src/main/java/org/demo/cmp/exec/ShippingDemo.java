package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * A Dummy Web Service Call to Show Shipping Process.
 * @author Debasis Kar
 */

public class ShippingDemo implements JavaDelegate {
	
	/**Any processing that needs to be carried out must be implemented in this Method which is defined 
	 * in JavaDelegate interface of Activiti Engine.
	 * @author Debasis Kar
	 * @param DelegateExecution
	 * @return void
	 * */
	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		 //Request SOAP Message to the Web Service based on some naive String
		 if(delegateExecution.getCurrentActivityName().contains("Ship")){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("ship"));
		 }
	}

}
