package org.demo.cmp.exec;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class RepairMachineDemo implements JavaDelegate{

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		if(delegateExecution.getCurrentActivityName().contains("Repair")){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("repair"));
			System.out.println("Repairing Complete...");
		}
		if(delegateExecution.getCurrentActivityName().contains("Install")){
			System.out.println("Machine Saying: " + DummySoapClient.SOAPWebServiceCall("install"));
			System.out.println("Installation Complete...");
		}
	}
}