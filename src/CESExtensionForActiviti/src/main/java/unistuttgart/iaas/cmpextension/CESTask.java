/**
 * 
 */
package unistuttgart.iaas.cmpextension;

import org.activiti.designer.integration.annotation.Help;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.annotation.PropertyItems;
import org.activiti.designer.integration.annotation.Runtime;
import org.activiti.designer.integration.annotation.TaskName;
import org.activiti.designer.integration.annotation.TaskNames;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;

/**
 * Defines the CES Task node.
 * @author Debasis Kar
 * @version 1
 * @since 27.12.2015
 */
@Runtime(javaDelegateClass = "unistuttgart.iaas.cmpextension.runtime.CESTaskDelegation")
@Help(displayHelpShort = "Context-sensitive Execution Step", 
displayHelpLong = "Creates a Context-sensitive Task that runs a process according to present scenario.")
@TaskNames(
    {
      @TaskName(locale = "en", name = "CES Task"),
      @TaskName(locale = "de", name = "CES Aufgabe")
    }
)

public class CESTask extends AbstractCustomServiceTask {
	
  @Property(type = PropertyType.TEXT, displayName = "Main Intention/Goal", required = true, defaultValue = "SealAndSortPackets")
  @Help(displayHelpShort = "Define the main goal/inention.", displayHelpLong = "You can provide the main intention of the task. It shouldn't contain any special character.")
  private String mainIntention;
     
  @Property(type = PropertyType.TEXT, displayName = "Sub Intention(s)/Goal(s)", required = true, defaultValue = "highAutomation, highThroughput")
  @Help(displayHelpShort = "Define the sub-goals/inentions.", displayHelpLong = "You can provide the sub intentions (if any) of the task separated by commas(,).")
  private String subIntentions;
  
  @Property(type = PropertyType.TEXT, displayName = "Required Context Data", required = true, defaultValue = "shockDetectorStatus, unitsOrdered, availableWorkers, infraredSensorStatus")
  @Help(displayHelpShort = "Mentioned required context data.", displayHelpLong = "You can provide the list of required context names to be looked for separted by commas(,).")
  private String requiredContext;
  
  @Property(type = PropertyType.TEXT, displayName = "Process Repository Location", required = true, defaultValue = "\\src\\main\\resources\\processrepos\\ProcessRepository.xml")
  @Help(displayHelpShort = "Give the path to the process models' store.", displayHelpLong = "You can provide the list of required context names to be looked for separted by commas(,).")
  private String processRepositoryPath;
  
  @Property(type = PropertyType.TEXT, displayName = "Input Variable(s)", required = false, defaultValue = "operatorName = Wolfgang, supervisorName = Frank")
  @Help(displayHelpShort = "Give any extra input as key-value pair.", displayHelpLong = "You can give any extra-input as a key-value pair separated by commas, e.g., age=25, experience=2.")
  private String inputVariable;

  @Property(type = PropertyType.TEXT, displayName = "Output Variable(s)", required = false, defaultValue = "finalStatus, packOutput")
  @Help(displayHelpShort = "Give variable names to store final result.", displayHelpLong = "Final output would be stored in these variables separated by commas.")
  private String outputVariable;
  
  @Property(type = PropertyType.RADIO_CHOICE, displayName = "Require Optimization", required = true, defaultValue = "True")
  @Help(displayHelpShort = "Should BPMN engine do any optimization?", displayHelpLong = "Choose the optimization criterion depending on the requirement and need of process model optimization in rumtime.")
  @PropertyItems({"Yes (Do It, If Strategies are Available!)", "True", "No (Don't Do It!)", "False"})
  private String performOptimization;
  
  @Property(type = PropertyType.TEXT, visible = false, defaultValue = "demo")
  private String hiddenField;
 
  @Override
  public String contributeToPaletteDrawer() {
    return "IAAS - University of Stuttgart";
  }

  public String getName() {
    return "CES Task";
  }
  
  @Override
  public String getSmallIconPath() {
    return "icons/cessquareicon.png";
  }

}
