package ca.mcmaster.se2aa4.island.teamXXX.Map;

import java.security.PKCS12Attribute;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.teamXXX.Drone.Drone;
import ca.mcmaster.se2aa4.island.teamXXX.Drone.Radar;
import ca.mcmaster.se2aa4.island.teamXXX.Interfaces.Movable;

public class Searcher extends State {

    // boooleans indicating whether to fly or scan on this iteration 
    private boolean scan;
    private boolean fly;
    
    public Searcher(Movable drone, Radar radar, Report report) {
        super(drone, radar, report);

        scan = true;
        fly = false;
    }

    public State getNextState(JSONObject response) {

        if (fly) {
            // ADD LOGIC TO CHECK FOR CREEKS OR SITES

            //! add logic to add to report if creek or site is found
            if (foundCreek(response)) {
                report.addCreek(response.getJSONObject("extras").getJSONArray("creeks").getString(0));
                //return new State(this.drone, this.radar, this.report);
            }
            if (foundSite(response)) {
                report.addSite(response.getJSONObject("extras").getJSONArray("sites").getString(0));
                //return new State(this.drone, this.radar, this.report);
            }

            // logic to break out of search state
            if (containsOcean(response)) {
                return new TurnDrone(this.drone, this.radar, this.report);
            }

            drone.moveForward();
            fly = false;
            scan = true;
        } else if (scan) {
            // Echo in all directions and decide where to go
            JSONObject forwardResponse = radar.echoForward();
            if (isGround(forwardResponse)) {
                drone.moveForward();
                fly = true;
                scan = false;
                return this;
            }

            JSONObject leftResponse = radar.echoLeft();
            if (isGround(leftResponse)) {
                drone.turnLeft();
                drone.moveForward();
                fly = true;
                scan = false;
                return this;
            }

            JSONObject rightResponse = radar.echoRight();
            if (isGround(rightResponse)) {
                drone.turnRight();
                drone.moveForward();
                fly = true;
                scan = false;
                return this;
            }

            // If no ground is found, perform a U-turn
            drone.turnLeft();
            drone.turnLeft();
            fly = true;
            scan = false;
        }

        return this;
    }

    private boolean containsOcean(JSONObject response) {
        if (!response.has("extras")) return false;

        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("biomes")) return false;

        JSONArray biomes = extras.getJSONArray("biomes");
        for (int i = 0; i < biomes.length(); i++) {
            if (biomes.getString(i).equals("OCEAN")) {
                return true;
            }
        }
        return false;
    }

    private boolean foundCreek(JSONObject response) {
        if (!response.has("extras")) return false;

        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("creeks")) return false;

        JSONArray creeks = extras.getJSONArray("creeks");
        if (creeks.length() > 0) {
            return true;
        }
        return false;
    }

    //! need improvement for check (handling arrays of length > 1)
    private boolean foundSite(JSONObject response) {
        if (!response.has("extras")) return false;

        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("sites")) return false;

        JSONArray sites = extras.getJSONArray("sites");
        if (sites.length() > 0) {
            return true;
        }
        return false;
    }

}
