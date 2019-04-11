package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

import static risk.Game.PAUSE_GAME_MENU;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class GameSceneController extends RiskSceneController {

    /*
    TODO:
        Implement loading of previous Game-state.
     */

    @FXML
    public Group territoryButtons;

    public GameSceneController() {
        System.out.println("Initialized Controller for Scene: Game.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeKeyboardListeners();

        double BUTTON_SHAPE_RADIUS = 12.0;
        Circle circle = new Circle(BUTTON_SHAPE_RADIUS);
        for (Node node : territoryButtons.getChildren()) {

            ((Button) node).setShape(circle);
            ((Button) node).setMinSize(2* BUTTON_SHAPE_RADIUS, 2* BUTTON_SHAPE_RADIUS);
            ((Button) node).setMaxSize(2* BUTTON_SHAPE_RADIUS, 2* BUTTON_SHAPE_RADIUS);

        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void initializeKeyboardListeners() {

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE: instance.requestDisplayForScene(PAUSE_GAME_MENU);
            }
        });

    }

    public Scene getScene() {
        return scene;
    }


}
