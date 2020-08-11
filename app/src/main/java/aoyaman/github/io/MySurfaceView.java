package aoyaman.github.io;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import aoyaman.github.io.model.CellInfo;
import aoyaman.github.io.model.PlayerInfo;
import aoyaman.github.io.model.PlayerType;

import static android.graphics.BlendMode.COLOR;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MySurfaceView.class.getSimpleName();
    private Game game;

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

        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);

        int cellsHeight = canvas.getHeight();
        cellsHeight -= TOP_MARGIN;          // 上余白
        cellsHeight -= PLAYER_INFO_HEIGHT;  // プレイヤー情報
        cellsHeight -= BOTTOM_MARGIN;       // 下余白
        cellsHeight /= (game.BOARD_HEIGHT + game.KOUHO_HEIGHT);

        float baseY = TOP_MARGIN;
        int width = cellsHeight; // canvas.getWidth() / game.BOARD_WIDTH;
        int height = width;
        float baseX = (canvas.getWidth() - width * game.BOARD_WIDTH) / 2;
        for (int y = 0; y < game.BOARD_HEIGHT; y++) {
            for (int x = 0; x < game.BOARD_WIDTH; x++) {
                canvas.drawRect(baseX + x * width, baseY + y * height,
                        baseX + x*width + width, baseY + y*height + height, paint);
            }
        }

        baseY += game.BOARD_HEIGHT * height + PLAYER_INFO_HEIGHT;
        for (int y = 0; y < game.KOUHO_HEIGHT; y++) {
            for (int x = 0; x < game.KOUHO_WIDTH; x++) {
                canvas.drawRect(baseX + x * width, baseY + y * height,
                        baseX + x*width + width, baseY + y*height + height, paint);
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

            baseX = (float) ((canvas.getWidth() / Game.PLAYER_NUM) * (p + 0.5)) - nameWidth / 2;
            canvas.drawText(name, baseX, baseY, strPaint);

            Rect rect = new Rect();
            rect.left = (int) baseX;
            rect.right = (int) (baseX + nameWidth);
            rect.top = (int) (baseY - 20);
            rect.bottom = (int) (baseY);
            playersRect[p] = rect;

            if (game.getNowPlayer() == p) {
                Paint linePaint = new Paint();
                linePaint.setColor(playerInfo.getColor());
                linePaint.setStrokeWidth(5);
                canvas.drawLine(baseX, baseY + 5,
                        baseX + nameWidth, baseY + 5, linePaint);
            }
        }






//        canvas.drawColor(Color.BLACK);
//        canvas.drawCircle(100, 200, 50, paint);
        surfaceHolder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            for (int p = 0; p < Game.PLAYER_NUM; p++) {
                if (playersRect[p].left <= ev.getX() && ev.getX() <= playersRect[p].right) {
                    if (playersRect[p].top <= ev.getY() && ev.getY() <= playersRect[p].bottom) {
                        Log.d(TAG, "touch " + p);
                    }
                }
            }
        }
        return true;
    }
}
