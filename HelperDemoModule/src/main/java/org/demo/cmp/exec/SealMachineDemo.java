package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class SealMachineDemo implements JavaDelegate {

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		 String var = (String) delegateExecution.getVariable("packStatus");
		 var = var.toUpperCase();
		System.err.println(var);
		if(delegateExecution.getCurrentActivityName().contains("Seal")){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("seal"));
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("complete"));
		}
	}

}
