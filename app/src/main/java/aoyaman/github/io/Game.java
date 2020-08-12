package aoyaman.github.io;

import android.util.Log;

import java.util.Date;

import aoyaman.github.io.model.CellInfo;
import aoyaman.github.io.model.GameStatus;
import aoyaman.github.io.model.ColorType;
import aoyaman.github.io.model.PlayerInfo;
import aoyaman.github.io.model.PlayerType;
import aoyaman.github.io.model.SelectInfo;
import aoyaman.github.io.model.SpaceInfo;
import aoyaman.github.io.model.SpaceType;

public class Game {
    private static final String TAG = Game.class.getSimpleName();
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
        this.status = playerInfos[0].getPlayerType() == PlayerType.CPU
                ? GameStatus.WAIT_CPU : GameStatus.WAIT_USER;
        this.boardInfo = new CellInfo[BOARD_HEIGHT][BOARD_WIDTH];
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                this.boardInfo[y][x] = new CellInfo();
            }
        }

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

    public int getNowPlayerColor() {
        return this.playerInfos[this.nowPlayer].getColor();
    }

    public SelectInfo getSelectInfo() {
        return this.playerInfos[this.nowPlayer].getSelectInfo();
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void setStatus(GameStatus status) {
        Log.i(TAG, "change status " + this.status + " -> " + status);
        this.status = status;
    }

    public void onSelect(SpaceInfo spaceInfo) {
        SelectInfo selectInfo = new SelectInfo(spaceInfo, 0, false);
        this.playerInfos[this.nowPlayer].setSelectInfo(selectInfo);
    }

    public void doRotate() {
        this.playerInfos[this.nowPlayer].getSelectInfo().doRotate();
    }

    public void doFlip() {
        this.playerInfos[this.nowPlayer].getSelectInfo().doFlip();
    }

    public boolean check(int targetX, int targetY) {
        boolean isCheck = false;
        PlayerInfo playerInfo = this.playerInfos[this.nowPlayer];
        int color = playerInfo.getColor();
        SelectInfo selectInfo = this.getSelectInfo();
        char[][] shape = selectInfo.getCells();

        for (int y = 0; y < shape.length; y += 1) {
            for (int x = 0; x < shape[y].length; x += 1) {
                if (shape[y][x] == 1) {
                    final int newY = y + targetY;
                    final int newX = x + targetX;

                    // はみ出てたらダメ！
                    if (BOARD_HEIGHT <= newY) {
                        return false;
                    }
                    if (BOARD_WIDTH <= newX) {
                        return false;
                    }

                    // すでにあってもダメ！
                    if (boardInfo[newY][newX].color > 0) {
                        return false;
                    }

                    // 右隣が同じ色ならダメ
                    if (newX < boardInfo[newY].length - 1 &&
                            boardInfo[newY][newX + 1].color == color
                    ) {
                        return false;
                    }
                    // 左隣が同じ色ならダメ
                    if (newX > 0 && boardInfo[newY][newX - 1].color == color) {
                        return false;
                    }
                    // 下隣が同じ色ならダメ
                    if (newY < boardInfo.length - 1 &&
                            boardInfo[newY + 1][newX].color == color
                    ) {
                        return false;
                    }
                    // 上隣が同じ色ならダメ
                    if (newY > 0 && boardInfo[newY - 1][newX].color == color) {
                        return false;
                    }

                    // 右上が同じ色ならOK
                    if (newY > 0 &&
                            newX < boardInfo[newY].length - 1 &&
                            boardInfo[newY - 1][newX + 1].color == color
                    ) {
                        isCheck = true;
                    }

                    // 左上が同じ色ならOK
                    if (newY > 0 &&
                            newX > 0 &&
                            boardInfo[newY - 1][newX - 1].color == color
                    ) {
                        isCheck = true;
                    }

                    // 右下が同じ色ならOK
                    if (newY < boardInfo.length - 1 &&
                            newX < boardInfo[newY].length - 1 &&
                            boardInfo[newY + 1][newX + 1].color == color
                    ) {
                        isCheck = true;
                    }

                    // 左下が同じ色ならOK
                    if (newY < boardInfo.length - 1 &&
                                    newX > 0 &&
                                    boardInfo[newY + 1][newX - 1].color == color
                    ) {
                        isCheck = true;
                    }

                    // 四角を踏んでたらOK
                    if ((newX == 0 && newY == 0) ||
                                    (newX == 0 && newY == boardInfo[newY].length - 1) ||
                                    (newX == boardInfo.length - 1 && newY == 0) ||
                                    (newX == boardInfo.length - 1 && newY == boardInfo[newY].length - 1)
                    ) {
                        isCheck = true;
                    }
                }
            }
        }
        return isCheck;

    }
}
