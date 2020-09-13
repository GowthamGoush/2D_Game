package com.example.park_the_car;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GameEndDialog.GameEndListener {

    private GameView gameView;
    private ArrayList<CarCoordinate> carCoordinates;
    private int i = 0;
    //private CarCoordinate crashIndex;
    private boolean continueMove = true;
    private boolean coin1Touched = false;
    private boolean coin2Touched = false;
    private boolean volumeOn = true;

    private Button soundBtn;

    private MediaPlayer mediaCoin, mediaCrash, mediaBG, mediaNoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.mGameView);

        carCoordinates = new ArrayList<>();

        soundBtn = findViewById(R.id.soundBtn);

        //crashIndex = null;

        mediaCoin = MediaPlayer.create(this, R.raw.sound_coin);
        mediaCrash = MediaPlayer.create(this, R.raw.sound_crash);
        mediaBG = MediaPlayer.create(this, R.raw.sound_background);
        mediaNoice = MediaPlayer.create(this, R.raw.sound_noice);

        mediaBG.setLooping(true);
        mediaBG.start();

        gameView.setCoordinatesListener(new GameView.OnCoordinateUpdate() {
            @Override
            public void onUpdate(float X, float Y, boolean isMove, boolean notFinish) {

                carCoordinates.add(new CarCoordinate(X, Y));

                /*if (X > (gameView.canvasWidth() / 2) - gameView.treeWidth() - 50 && X < (gameView.canvasWidth() / 2) - 50 && Y > (gameView.canvasHeight() / 2) - 400 && Y < (gameView.canvasHeight() / 2) + gameView.treeHeight() - 400 && crashIndex == null && carCoordinates.size()>15) {
                    crashIndex = new CarCoordinate(carCoordinates.get(carCoordinates.size()-15).getCoordinateX(),carCoordinates.get(carCoordinates.size()-6).getCoordinateY());
                }

                if (X > (gameView.canvasWidth() / 2) + 50 && X < (gameView.canvasWidth() / 2) + gameView.treeWidth() + 50 && Y > (gameView.canvasHeight() / 2) + 400 && Y < (gameView.canvasHeight() / 2) + gameView.treeHeight() + 400 && crashIndex == null && carCoordinates.size()>15) {
                    crashIndex = new CarCoordinate(carCoordinates.get(carCoordinates.size()-15).getCoordinateX(),carCoordinates.get(carCoordinates.size()-6).getCoordinateY());
                }*/

                if (notFinish) {
                    carCoordinates.clear();
                }

                if (isMove && !notFinish) {

                    CountDownTimer countDownTimer = new CountDownTimer(carCoordinates.size() * 50, 50) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (continueMove) {
                                float cordX = carCoordinates.get(i).getCoordinateX();
                                float cordY = carCoordinates.get(i).getCoordinateY();

                                if (cordX > 150 && cordX < 150 + gameView.coinWidth() && cordY > (gameView.canvasHeight() / 2) - 200 && cordY < (gameView.canvasHeight() / 2) - 200 + gameView.coinHeight() && !coin1Touched) {
                                    gameView.setCoin1Touched();
                                    mediaCoin.start();
                                    coin1Touched = true;
                                }

                                if (cordX > gameView.canvasWidth() - gameView.coinWidth() - 150 && cordX < gameView.canvasWidth() - 150 && cordY > (gameView.canvasHeight() / 2) + 200 && cordY < (gameView.canvasHeight() / 2) + 200 + gameView.coinHeight() && !coin2Touched) {
                                    gameView.setCoin2Touched();
                                    mediaCoin.start();
                                    coin2Touched = true;
                                }

                                /*if(crashIndex.getCoordinateX() == cordX && crashIndex.getCoordinateY() == cordY){
                                    mediaCrash.start();
                                }*/

                                gameView.setPathLine(cordX, cordY);
                                i += 1;

                                if (cordX > (gameView.canvasWidth() / 2) - gameView.treeWidth() - 50 && cordX < (gameView.canvasWidth() / 2) - 50 && cordY > (gameView.canvasHeight() / 2) - 400 && cordY < (gameView.canvasHeight() / 2) + gameView.treeHeight() - 400) {
                                    gameView.setTreeTouched();
                                    mediaCrash.start();

                                    continueMove = false;
                                }

                                if (cordX > (gameView.canvasWidth() / 2) + 50 && cordX < (gameView.canvasWidth() / 2) + gameView.treeWidth() + 50 && cordY > (gameView.canvasHeight() / 2) + 400 && cordY < (gameView.canvasHeight() / 2) + gameView.treeHeight() + 400) {
                                    gameView.setTreeTouched();
                                    mediaCrash.start();
                                    continueMove = false;
                                }
                            }
                        }

                        @Override
                        public void onFinish() {
                            gameView.setPathLine(gameView.canvasWidth() / 2, 0);
                            openDialog();
                            if (continueMove) {
                                mediaNoice.start();
                            }
                        }
                    }.start();
                }
            }
        });

        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (volumeOn) {
                    soundBtn.setBackgroundResource(R.drawable.ic_volumeoff);
                    mediaBG.pause();
                    volumeOn = false;
                } else {
                    soundBtn.setBackgroundResource(R.drawable.ic_volumeon);
                    mediaBG.start();
                    volumeOn = true;
                }
            }
        });
    }

    public void openDialog() {
        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                GameEndDialog gameEndDialog = new GameEndDialog();
                gameEndDialog.show(getSupportFragmentManager(), "Game Over");
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaBG.stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaBG.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaBG.start();
    }

    @Override
    public void YesClicked() {
        gameView.resetGame();
        i = 0;
        carCoordinates.clear();
        continueMove = true;
        coin1Touched = false;
        coin2Touched = false;
    }
}