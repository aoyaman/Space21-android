package aoyaman.github.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.Date;

import aoyaman.github.io.model.CellInfo;
import aoyaman.github.io.model.GameStatus;
import aoyaman.github.io.model.PlayerInfo;
import aoyaman.github.io.model.PlayerType;
import aoyaman.github.io.model.SelectInfo;
import aoyaman.github.io.model.SpaceInfo;

import static android.graphics.BlendMode.COLOR;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MySurfaceView.class.getSimpleName();
    private Game game;

    private Bitmap offscreen;
    private Canvas offCanvas;


    private float boardX;
    private float boardY;
    private float kouhoX;
    private float kouhoY;
    private float dragX;
    private float dragY;
    private float lastMoveX;
    private float lastMoveY;
    private Rect closeButtonRect = new Rect();
    private Rect rotateButtonRect = new Rect();
    private Rect flipButtonRect = new Rect();
    private Rect okButtonRect = new Rect();

    private float cellWidth;
    private float cellHeight;
    private Rect[] playersRect;

    private final static int TOP_MARGIN = 10;
    private final static int BOTTOM_MARGIN = 10;
    private final static int PLAYER_INFO_HEIGHT = 100;

    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        PlayerType[] players = new PlayerType[Game.PLAYER_NUM];
        players[0] = PlayerType.HUMAN;
        players[1] = PlayerType.CPU;
        players[2] = PlayerType.CPU;
        players[3] = PlayerType.CPU;

        game = new Game(players);
        playersRect = new Rect[Game.PLAYER_NUM];

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);
//        paint.setStyle(Paint.Style.FILL);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12);

        Canvas mainCanvas = surfaceHolder.lockCanvas();
        if (offscreen == null) {


            // オフスクリーン描画用のキャンバス作成
            offscreen = Bitmap.createBitmap(mainCanvas.getWidth(),
                    mainCanvas.getHeight(),
                    Bitmap.Config.ARGB_8888);

            offCanvas = new Canvas(offscreen);
            offCanvas.setBitmap(offscreen);

            offCanvas.drawColor(Color.WHITE);

            int cellsHeight = offCanvas.getHeight();
            cellsHeight -= TOP_MARGIN;          // 上余白
            cellsHeight -= PLAYER_INFO_HEIGHT;  // プレイヤー情報
            cellsHeight -= BOTTOM_MARGIN;       // 下余白
            cellsHeight /= (game.BOARD_HEIGHT + game.KOUHO_HEIGHT);

            float baseY = TOP_MARGIN;
            this.cellWidth = cellsHeight; // canvas.getWidth() / game.BOARD_WIDTH;
            this.cellHeight = this.cellWidth;
            float baseX = (offCanvas.getWidth() - this.cellWidth * game.BOARD_WIDTH) / 2;
            boardX = baseX;
            boardY = baseY;
            for (int y = 0; y < game.BOARD_HEIGHT; y++) {
                for (int x = 0; x < game.BOARD_WIDTH; x++) {
                    offCanvas.drawRect(baseX + x * this.cellWidth, baseY + y * this.cellHeight,
                            baseX + x*this.cellWidth + this.cellWidth, baseY + y*this.cellHeight + this.cellHeight, paint);
                }
            }

            baseY += game.BOARD_HEIGHT * this.cellHeight + PLAYER_INFO_HEIGHT;
            kouhoX = baseX;
            kouhoY = baseY;
            for (int y = 0; y < game.KOUHO_HEIGHT; y++) {
                for (int x = 0; x < game.KOUHO_WIDTH; x++) {
                    offCanvas.drawRect(baseX + x * this.cellWidth, baseY + y * this.cellHeight,
                            baseX + x*this.cellWidth + this.cellWidth, baseY + y*this.cellHeight + this.cellHeight, paint);
                }
            }

            baseY -= 30;
            for (int p = 0; p < Game.PLAYER_NUM; p++) {
                PlayerInfo playerInfo = game.getPlayerInfo(p);
                String name = playerInfo.getName();
                Paint strPaint = new Paint();
                strPaint.setTextSize(20);
                strPaint.setColor(playerInfo.getColor());

                float nameWidth = strPaint.measureText(name);

                baseX = (float) ((offCanvas.getWidth() / Game.PLAYER_NUM) * (p + 0.5)) - nameWidth / 2;
                offCanvas.drawText(name, baseX, baseY, strPaint);

                Rect rect = new Rect();
                rect.left = (int) baseX;
                rect.right = (int) (baseX + nameWidth);
                rect.top = (int) (baseY - 20);
                rect.bottom = (int) (baseY);
                playersRect[p] = rect;

                for (SpaceInfo spaceInfo : playerInfo.getSpaces()) {
                    if (spaceInfo.isSet == false && game.getNowPlayer() == p) {
                        char[][] cells = spaceInfo.type.shapes[0];
                        for (int y = 0; y < cells.length; y++) {
                            for (int x = 0; x < cells[y].length; x++) {
                                if (cells[y][x] == 1) {
                                    Paint paintSpace = new Paint(Paint.ANTI_ALIAS_FLAG);
                                    paintSpace.setColor(playerInfo.getColor());
                                    paintSpace.setStyle(Paint.Style.FILL);
                                    final float cellLeft = kouhoX + (spaceInfo.type.tegomaPosition.x + x) * this.cellWidth;
                                    final float cellTop = kouhoY + (spaceInfo.type.tegomaPosition.y + y) * this.cellHeight;
                                    offCanvas.drawRect(
                                            cellLeft,
                                            cellTop,
                                            cellLeft + this.cellWidth,
                                            cellTop + this.cellHeight,
                                            paintSpace);
                                }
                            }
                        }
                    }
                }

                if (game.getNowPlayer() == p) {

                    // 名前の下にアンダーラインを引く
                    Paint linePaint = new Paint();
                    linePaint.setColor(playerInfo.getColor());
                    linePaint.setStrokeWidth(5);
                    offCanvas.drawLine(baseX, baseY + 5,
                            baseX + nameWidth, baseY + 5, linePaint);


                }
            }



        }
        mainCanvas.drawBitmap(offscreen, 0, 0, null);




//        canvas.drawColor(Color.BLACK);
//        canvas.drawCircle(100, 200, 50, paint);
        surfaceHolder.unlockCanvasAndPost(mainCanvas);

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void onDrawDrag(float touchX, float touchY) {
//        Log.d(TAG, "onDrawDrag()...x=" + touchX + ", y=" + touchY);

        this.lastMoveX = touchX;
        this.lastMoveY = touchY;

        // オフスクリーンキャンバスの内容を描画してからドラッグするスペースを描画する

        Canvas canvas = getHolder().lockCanvas();
        canvas.drawBitmap(offscreen, 0, 0, null);

        Paint paintSpace = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSpace.setColor(game.getNowPlayerColor());
        paintSpace.setStyle(Paint.Style.FILL);

        final SelectInfo selectInfo = game.getSelectInfo();
        char[][] cells = selectInfo.getCells();

        // 幅と高さを取得
        int maxX = 0, maxY = 0;
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                if (cells[y][x] == 1) {
                    if (maxX < x) {
                        maxX = x;
                    }
                    if (maxY < y) {
                        maxY = y;
                    }
                }
            }
        }
        // 描画位置
        dragX = touchX - (maxX * this.cellWidth) / 2; // 幅の中央寄せにする
        dragY = touchY - (maxY * this.cellHeight) - 100; // 指に隠れないように上にずらす

        boolean isOkButton = false;

        // ボードの範囲内だったら
        if (boardX <= dragX && dragX <= (boardX + cellWidth * Game.BOARD_WIDTH)
            && boardY <= dragY && dragY <= (boardY + cellHeight * Game.BOARD_HEIGHT)) {

            // ボードの枠にぴったりハマるように調整する
            int xxx = (int) ((dragX - boardX) / cellWidth);
            int yyy = (int) ((dragY - boardY) / cellHeight);
            dragX = boardX + xxx * cellWidth;
            dragY = boardY + yyy * cellHeight;

            if (game.check(xxx,yyy)) {
                isOkButton = true;
            }
        }

        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                if (cells[y][x] == 1) {

                    canvas.drawRect(
                            dragX + x * this.cellWidth,
                            dragY  + y * this.cellHeight,
                            dragX + x * this.cellWidth + this.cellWidth,
                            dragY  + y * this.cellHeight + this.cellHeight,
                            paintSpace);

                }
            }
        }

        // 閉じるボタンを描画
        Bitmap bmpClose = BitmapFactory.decodeResource(getResources(), R.drawable.close_button);
        Rect srcClose = new Rect();
        srcClose.right = bmpClose.getWidth();
        srcClose.bottom = bmpClose.getHeight();
        closeButtonRect.right = (int) dragX;
        closeButtonRect.bottom = (int) touchY;
        closeButtonRect.left = closeButtonRect.right - 50;
        closeButtonRect.top = closeButtonRect.bottom - 50;
        canvas.drawBitmap(bmpClose, srcClose, closeButtonRect, new Paint());

        // 回転ボタンを描画
        Bitmap bmpRotate = BitmapFactory.decodeResource(getResources(), R.drawable.rotate_button);
        Rect srcRotate = new Rect();
        srcRotate.right = bmpRotate.getWidth();
        srcRotate.bottom = bmpRotate.getHeight();
        rotateButtonRect.right = (int) dragX + 70;
        rotateButtonRect.bottom = (int) touchY;
        rotateButtonRect.left = rotateButtonRect.right - 50;
        rotateButtonRect.top = rotateButtonRect.bottom - 50;
        canvas.drawBitmap(bmpRotate, srcRotate, rotateButtonRect, new Paint());

        // 左右反転ボタンを描画
        Bitmap bmpFilp = BitmapFactory.decodeResource(getResources(), R.drawable.flip_button);
        Rect srcFlip = new Rect();
        srcFlip.right = bmpFilp.getWidth();
        srcFlip.bottom = bmpFilp.getHeight();
        flipButtonRect.right = (int) dragX + 70 * 2;
        flipButtonRect.bottom = (int) touchY;
        flipButtonRect.left = flipButtonRect.right - 50;
        flipButtonRect.top = flipButtonRect.bottom - 50;
        canvas.drawBitmap(bmpFilp, srcFlip, flipButtonRect, new Paint());

        // OKボタンを描画
        if (isOkButton) {
            Bitmap bmpOk = BitmapFactory.decodeResource(getResources(), R.drawable.ok_button);
            Rect srcOk = new Rect();
            srcOk.right = bmpOk.getWidth();
            srcOk.bottom = bmpOk.getHeight();
            okButtonRect.right = (int) dragX + 70 * 3;
            okButtonRect.bottom = (int) touchY;
            okButtonRect.left = okButtonRect.right - 50;
            okButtonRect.top = okButtonRect.bottom - 50;
            canvas.drawBitmap(bmpOk, srcOk, okButtonRect, new Paint());
        }

        getHolder().unlockCanvasAndPost(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            // ドラッグ中に指を離したら「選択中状態」に変更
            if (game.getStatus() == GameStatus.USER_DRAGGING) {
                game.setStatus(GameStatus.USER_SELECTED);
                return true;
            }

            // 選択中状態
            if (game.getStatus() == GameStatus.USER_SELECTED) {
                // 閉じるボタン押下
                if (closeButtonRect.left <= ev.getX() && ev.getX() <= closeButtonRect.right
                    && closeButtonRect.top <= ev.getY() && ev.getY() <= closeButtonRect.bottom) {
                    game.setStatus(GameStatus.WAIT_USER);
                    Canvas canvas = getHolder().lockCanvas();
                    canvas.drawBitmap(offscreen, 0, 0, null);
                    getHolder().unlockCanvasAndPost(canvas);
                    return true;
                }

                // 回転ボタン押下
                if (rotateButtonRect.left <= ev.getX() && ev.getX() <= rotateButtonRect.right
                        && flipButtonRect.top <= ev.getY() && ev.getY() <= rotateButtonRect.bottom) {

                    game.doRotate();

                    onDrawDrag(this.lastMoveX, this.lastMoveY);
                    return true;
                }

                // 左右反転ボタン押下
                if (flipButtonRect.left <= ev.getX() && ev.getX() <= flipButtonRect.right
                        && flipButtonRect.top <= ev.getY() && ev.getY() <= flipButtonRect.bottom) {

                    game.doFlip();

                    onDrawDrag(this.lastMoveX, this.lastMoveY);
                    return true;
                }
            }

            // プレイヤー名のタッチを検知
            for (int p = 0; p < Game.PLAYER_NUM; p++) {
                if (playersRect[p].left <= ev.getX() && ev.getX() <= playersRect[p].right) {
                    if (playersRect[p].top <= ev.getY() && ev.getY() <= playersRect[p].bottom) {
                        Log.d(TAG, "touch " + p);
                        return true;
                    }
                }
            }
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && game.getStatus() == GameStatus.WAIT_USER) {

            // 候補のタッチを検知
            PlayerInfo playerInfo = game.getPlayerInfo(game.getNowPlayer());
            for (SpaceInfo spaceInfo : playerInfo.getSpaces()) {
                if (spaceInfo.isSet == false) {
                    char[][] cells = spaceInfo.type.shapes[0];
                    for (int y = 0; y < cells.length; y++) {
                        for (int x = 0; x < cells[y].length; x++) {
                            if (cells[y][x] == 1) {
                                final float cellLeft = kouhoX + (spaceInfo.type.tegomaPosition.x + x) * this.cellWidth;
                                final float cellTop = kouhoY + (spaceInfo.type.tegomaPosition.y + y) * this.cellHeight;
                                final float cellRight = cellLeft + this.cellWidth;
                                final float cellBottom = cellTop + this.cellHeight;
                                if (cellLeft <= ev.getX() && ev.getX() <= cellRight &&
                                     cellTop <= ev.getY() && ev.getY() <= cellBottom ) {

                                    game.setStatus(GameStatus.USER_DRAGGING);

                                    game.onSelect(spaceInfo);


                                    onDrawDrag(ev.getX(), ev.getY());
                                    return true;


                                }
                            }
                        }
                    }
                }
            }


        } else if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && game.getStatus() == GameStatus.USER_SELECTED) {

            final SelectInfo selectInfo = game.getSelectInfo();
            char[][] cells = selectInfo.getCells();
            for (int y = 0; y < cells.length; y++) {
                for (int x = 0; x < cells[y].length; x++) {
                    if (cells[y][x] == 1) {
                        final float cellLeft = dragX + x * this.cellWidth;
                        final float cellTop = dragY + y * this.cellHeight;
                        final float cellRight = cellLeft + this.cellWidth;
                        final float cellBottom = cellTop + this.cellHeight;
                        if (cellLeft <= ev.getX() && ev.getX() <= cellRight &&
                                cellTop <= ev.getY() && ev.getY() <= cellBottom ) {

                            game.setStatus(GameStatus.USER_DRAGGING);

                            onDrawDrag(ev.getX(), ev.getY());
                            return true;


                        }
                    }
                }
            }


        } else if (ev.getAction() == MotionEvent.ACTION_MOVE && game.getStatus() == GameStatus.USER_DRAGGING) {
            onDrawDrag(ev.getX(), ev.getY());
        }
        return true;
    }
}
