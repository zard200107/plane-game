package com.example.demo.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.example.demo.LevelParent;

public class Controller implements Observer {

	private static final String LEVEL_ONE_CLASS_NAME = "com.example.demo.LevelOne";
	private final Stage stage;
	// 是否正在切换关卡
	private boolean isChangingLevel = false;  
	// 当前关卡
    private LevelParent currentLevel;         


	public Controller(Stage stage) {
		this.stage = stage;
	}

	public void launchGame() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {

			stage.show();
			goToLevel(LEVEL_ONE_CLASS_NAME);
	}

	private void goToLevel(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 创建新关卡
		Class<?> myClass = Class.forName(className);
		Constructor<?> constructor = myClass.getConstructor(double.class, double.class);
		currentLevel = (LevelParent) constructor.newInstance(stage.getHeight(), stage.getWidth());

		// 设置观察者
		currentLevel.addObserver(this);

		// 初始化场景
		Scene scene = currentLevel.initializeScene();
		stage.setScene(scene);

		// 开始游戏
		currentLevel.startGame();

	}

	@Override
	public void update(Observable arg0, Object arg1) {

		if(isChangingLevel) return;

		try {
			isChangingLevel = true;

			// 停止当前关卡
			if(currentLevel != null) {
				currentLevel.stopGame();
			}

			// 切换到新关卡
			String nextLevel = (String) arg1;
			goToLevel(nextLevel);

		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException	
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("关卡切换失败: " + e.getMessage());
			alert.show();

			isChangingLevel = false;
		}
	}


	    private void handleError(String message, Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("错误");
            alert.setContentText(message + ": " + e.getMessage());
            alert.show();
        });
    }
}
