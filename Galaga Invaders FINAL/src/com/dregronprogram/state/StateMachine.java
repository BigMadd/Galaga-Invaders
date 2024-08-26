package com.dregronprogram.state;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.dregronprogram.game_screen.GameScreen;
import com.dregronprogram.menu_screen.MenuScreen;
import com.dregronprogram.menu_screen.LevelSelectScreen;

public class StateMachine {

    private ArrayList<SuperStateMachine> states = new ArrayList<>();
    private Canvas canvas;
    private byte selectState = 0;

    public StateMachine(Canvas canvas){
        SuperStateMachine menu = new MenuScreen(this);
        SuperStateMachine levelSelect = new LevelSelectScreen(this);
        SuperStateMachine game1 = new GameScreen(this); // Asume que este es el juego del nivel 1
        SuperStateMachine game2 = new GameScreen(this); // Puedes crear diferentes GameScreens para cada nivel si es necesario
        SuperStateMachine game3 = new GameScreen(this); // Otro ejemplo

        states.add(menu);
        states.add(levelSelect);
        states.add(game1);
        states.add(game2);
        states.add(game3);

        this.canvas = canvas;
    }

    public void draw(Graphics2D g){
        states.get(selectState).draw(g);
    }

    public void update(double delta){
        states.get(selectState).update(delta);
    }

    public void setState(byte i){
        for(int r = 0; r < canvas.getKeyListeners().length; r++)
            canvas.removeKeyListener(canvas.getKeyListeners()[r]);
        selectState = i;
        states.get(selectState).init(canvas);
    }

    public byte getStates() {
        return selectState;
    }
}
