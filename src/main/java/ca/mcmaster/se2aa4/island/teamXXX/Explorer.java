package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.teamXXX.Enums.Directions;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private DroneController droneController;
    int i = 0;
    boolean explorationComplete;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));

        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");

        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
        System.out.println(info);

        droneController = new DroneController(direction, batteryLevel);
        explorationComplete = false;

        // initialize DroneController here (send in battery level and heading)
    }

    @Override
    public String takeDecision() {

        i++;

        // if (i > 10000) {
        //     JSONObject decision = droneController.stopExploration();
        //     return decision.toString();
        // }

        logger.info("\n take decision \n");

        JSONObject decision = droneController.makeDecision();

        logger.info("** Decision: {}", decision.toString());

        if (decision.has("action") && "stop".equals(decision.getString("action"))) {
            // The decision was to stop
            logger.info("\n DECISION IS TO STOP \n");
            explorationComplete = true;
        }
        

        //logger.info("\n take decision \n");

        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        logger.info("\n acknowledge results \n");
        droneController.setResult(response);
        if (explorationComplete) {
            String results = deliverFinalReport();
            logger.info("The results are {}", results);
            //deliverFinalReport();
        }
        logger.info("\n acknowledge results complete \n");
    }

    @Override
    public String deliverFinalReport() {
        return droneController.getDiscoveries();
        //return "no creek found";
    }

}
