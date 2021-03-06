package ca.umontreal.iro.hurtubin.rebound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;


public abstract class AnimationView extends SurfaceView {

    protected Paint paint;
    protected SurfaceHolder holder;
    protected AnimationThread thread;

    public AnimationView(Context context, AttributeSet attributeSet) {
        this(context);
    }

    public AnimationView(final Context context) {
        super(context);

        // Objet paint utilisé pour dessiner sur le canvas
        paint = new Paint();

        holder = getHolder();

        // À la création de la surface, on lance le Thread pour l'animation
        holder.addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("WrongCall")
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                thread = new AnimationThread(AnimationView.this);
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                thread.run = false;

                while(true) {
                    try {
                        thread.join();
                        break;
                    } catch (InterruptedException e) {}
                }
            }
        });
    }

    public class AnimationThread extends Thread {
        private AnimationView view;
        private boolean run = true;

        public AnimationThread(AnimationView view) {
            this.view = view;
        }

        @Override
        public void run() {
            while (run) {
                try {
                    Canvas canvas = holder.lockCanvas();

                    if (canvas != null) {
                        synchronized (view.getHolder()) {
                            view.draw(canvas);
                        }
                    }

                    holder.unlockCanvasAndPost(canvas);
                } catch (IllegalArgumentException e) {
                    // Évite de faire planter l'application lors d'un instant run
                }

                try {
                    sleep(10);
                } catch (InterruptedException fire) {
                    fire.printStackTrace();
                }
            }
        }
    }

    // Fonction à étendre dans une classe personnalisée
    abstract public void draw(Canvas canvas);
}
