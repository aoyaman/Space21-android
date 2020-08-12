package aoyaman.github.io.model;

import android.graphics.Color;

public class PlayerInfo {
    String name;
    PlayerType playerType;
    int color;
    int blockZansu;
    int point;
    boolean pass;
    SpaceInfo[] spaces;

    CellInfo[][] tegoma;
    SelectInfo selectInfo;

    public PlayerInfo(String name, PlayerType playerType, int index,
                      SpaceInfo[] spaces) {
        this.name = name;
        this.playerType = playerType;
        this.color = calcColor(index);
        this.blockZansu = spaces.length;
        this.point = 0;
        this.pass = false;
        this.spaces = spaces;
        this.tegoma = null;
        this.selectInfo = null;
    }

    public int getColor() {
        return this.color;
    }
    public String getName() {
        return this.name;
    }

    public static int calcColor(int index) {
        switch(index) {
            case 0: return Color.argb(0xff, 0xff, 0, 0);
            case 1: return Color.BLUE;
            case 2: return Color.argb(0xff, 0x00, 0x64, 0x00);
            case 3: return  Color.argb(0xff, 0xff, 0xa5, 0x00);
        }
        return 0;
    }

    public SpaceInfo[] getSpaces() {
        return this.spaces;
    }
    public PlayerType getPlayerType() {
        return this.playerType;
    }

    public SelectInfo getSelectInfo() {
        return this.selectInfo;
    }
    public void setSelectInfo(SelectInfo selectInfo) {
        this.selectInfo = selectInfo;
    }



}
