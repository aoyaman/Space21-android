package aoyaman.github.io;

import java.util.Date;

import aoyaman.github.io.model.CellInfo;
import aoyaman.github.io.model.GameStatus;
import aoyaman.github.io.model.ColorType;
import aoyaman.github.io.model.PlayerInfo;
import aoyaman.github.io.model.PlayerType;
import aoyaman.github.io.model.SpaceInfo;
import aoyaman.github.io.model.SpaceType;

public class Game {
    public final static int PLAYER_NUM = 4;
    public final static int BOARD_WIDTH = 21;
    public final static int BOARD_HEIGHT = 21;
    public final static int KOUHO_WIDTH = 21;
    public final static int KOUHO_HEIGHT = 14;


    private GameStatus status;
    private Date date;
    private int nowPlayer;
    private CellInfo[][] boardInfo;

    private PlayerInfo[] playerInfos = new PlayerInfo[4];

    public Game(PlayerType[] players) {
        if (players == null) {
            throw new IllegalArgumentException("引数は必須です");
        } else if (players.length != 4) {
            throw new IllegalArgumentException("プレイヤーの人数は４人でないといけません");
        }

        int userCount = 0;
        int cpuCount = 0;
        for (int i = 0; i < players.length; i++) {
            String name = "";
            if (players[i].equals(PlayerType.HUMAN)) {
                name = "USER" + ++userCount;
            } else {
                name = "CPU" + ++cpuCount;
            }

            SpaceInfo[] spaces = new SpaceInfo[SpaceType.SPACE_TYPES.length];
            playerInfos[i] = new PlayerInfo(name, players[i], i, spaces);

            for (int b = 0; b < SpaceType.SPACE_TYPES.length; b++) {
                SpaceType.SPACE_TYPES[b].index = b;

                spaces[b] = new SpaceInfo();
                spaces[b].type = SpaceType.SPACE_TYPES[b];
                spaces[b].isSet = false;
                spaces[b].color = playerInfos[i].getColor();
                spaces[b].x = -1;
                spaces[b].y = -1;
                spaces[b].angle = 0;
                spaces[b].flip = false;

            }

        }
        this.date = new Date();
        this.nowPlayer = 0;


    }

    public CellInfo[][] getBoardInfo() {
        return boardInfo;
    }
    public PlayerInfo getPlayerInfo(int index) {
        return this.playerInfos[index];
    }
    public int getNowPlayer() {
        return this.nowPlayer;
    }
}
