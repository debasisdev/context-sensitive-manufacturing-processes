/**
 * 
 */
package uni_stuttgart.iaas.cmp.extension;

import org.activiti.designer.integration.annotation.Help;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.annotation.PropertyItems;
import org.activiti.designer.integration.annotation.Runtime;
import org.activiti.designer.integration.annotation.TaskName;
import org.activiti.designer.integration.annotation.TaskNames;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;

/** 
 * Copyright 2016 Debasis Kar
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

/**
 * Defines the CES Task node.
 * @author Debasis Kar
 * @version 1
 * @since 27.12.2015
 */
@Runtime(javaDelegateClass = "uni_stuttgart.iaas.spi.cmp.delegates.CESTaskDelegation")
@Help(displayHelpShort = "Context-sensitive Execution Step", 
displayHelpLong = "Creates a Context-sensitive Task that runs a process according to present scenario.")
@TaskNames(
    {
      @TaskName(locale = "en", name = "CES Task"),
      @TaskName(locale = "de", name = "CES Aufgabe")
    }
)

public class CESTask extends AbstractCustomServiceTask {
	
  @Property(type = PropertyType.TEXT, displayName = "Main Intention", required = true, defaultValue = "SealAndSortPackets")
  @Help(displayHelpShort = "Define the main goal/inention.", displayHelpLong = "You can provide the main intention of the task. It shouldn't contain any special character.")
  private String mainIntention;
     
  @Property(type = PropertyType.TEXT, displayName = "Sub Intention(s)", required = true, defaultValue = "highAutomation, highThroughput")
  @Help(displayHelpShort = "Define the sub-goals/inentions.", displayHelpLong = "You can provide the sub intentions (if any) of the task separated by commas(,).")
  private String subIntentions;
  
  @Property(type = PropertyType.TEXT, displayName = "Required Context Data", required = true, defaultValue = "shockDetectorStatus, unitsOrdered, availableWorkers, infraredSensorStatus")
  @Help(displayHelpShort = "Mentioned required context data.", displayHelpLong = "You can provide the list of required context names to be looked for separted by commas(,).")
  private String requiredContext;
  
  @Property(type = PropertyType.COMBOBOX_CHOICE, displayName = "Process Repository Type", required = true, defaultValue = "xml")
  @Help(displayHelpShort = "Give the type of your process repository", displayHelpLong = "You can provide the type of URI of the process repository you are going to use.")
  @PropertyItems({"XML", "xml", "SQL Database", "SQL"})
  private String processRepositoryType;
  
  @Property(type = PropertyType.TEXT, displayName = "Process Repository URI", required = true, defaultValue = "D:\\MyWorkThesis\\SPIExtension\\src\\main\\resources\\dataRepository")
  @Help(displayHelpShort = "Give the path to the process models' store.", displayHelpLong = "You can provide the URI of the process repository you are going to use.")
  private String processRepositoryPath;
  
  @Property(type = PropertyType.TEXT, displayName = "Input Variable(s)", required = false, defaultValue = "operatorName = Wolfgang, supervisorName = Frank")
  @Help(displayHelpShort = "Give any extra input as key-value pair.", displayHelpLong = "You can give any extra-input as a key-value pair separated by commas, e.g., age=25, experience=2.")
  private String inputVariable;

  @Property(type = PropertyType.TEXT, displayName = "Output Variable", required = true, defaultValue = "finalStatus, packOutput")
  @Help(displayHelpShort = "Give variable names to store final result.", displayHelpLong = "Final output would be stored in this variable.")
  private String outputVariable;
  
  @Property(type = PropertyType.RADIO_CHOICE, displayName = "Require Optimization", required = true, defaultValue = "True")
  @Help(displayHelpShort = "Should BPMN engine do any optimization?", displayHelpLong = "Choose the optimization criterion depending on the requirement and need of process model optimization in rumtime.")
  @PropertyItems({"Yes (Do It, If Strategies are Available!)", "True", "No (Don't Do It!)", "False"})
  private String performOptimization;

  @Property(type = PropertyType.COMBOBOX_CHOICE, displayName = "Selection Strategy", required = true, defaultValue = "http://www.uni-stuttgart.de/iaas/cmp/weight-based")
  @Help(displayHelpShort = "How should a process be selected?", displayHelpLong = "Choose the selection strategy that can be used to choose one process among multiple candidate processes.")
  @PropertyItems({"Weight Based", "http://www.uni-stuttgart.de/iaas/cmp/weight-based", "Random", "http://www.uni-stuttgart.de/iaas/cmp/random", "Most Used", "http://www.uni-stuttgart.de/iaas/cmp/most-used"})
  private String selectionStrategy;
  
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
