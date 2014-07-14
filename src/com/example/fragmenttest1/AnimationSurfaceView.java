package com.example.fragmenttest1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

	static final long FPS = 60;
	static final long FRAME_TIME = 1000 / FPS;
	static int RECT_SIDE;
	SurfaceHolder surfaceHolder;
	Thread thread;
	Rect[] panel = new Rect[9];
	int[] isTouched = new int[9];
	int screen_width, screen_height;
	MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fsan1);
	Context myContext;
	Fragment myFragment;
	SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	int spid;
	long time, remaining, offset = -0;
	long score = 0, score_now = 0;
	int mPointerID1 = -1, mPointerID2 = -1;
	int pointerId;
	float x1 = 0.0f;
	float y1 = 0.0f;
	float x2 = 0.0f;
	float y2 = 0.0f;

	public AnimationSurfaceView(Context context, Fragment fragment) {
		super(context);
		myContext = context;
		myFragment = fragment;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	@Override
	public void run() {

		for (int i = 0; i < 9; i++) {
			isTouched[i] = -1;
		}

		Canvas canvas = null;
		Paint paint = new Paint();
		Paint bgPaint = new Paint();
		Paint rectPaint = new Paint();
		Paint rectPaint2 = new Paint();

		// Background
		bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(Color.WHITE);
		// Rectangle
		rectPaint.setStyle(Style.STROKE);
		rectPaint.setColor(Color.RED);
		// Rectangle(Touched)
		rectPaint2.setStyle(Style.FILL);
		rectPaint2.setColor(Color.RED);

		long loopCount = 0;
		long waitTime = 0;
		long startTime = System.currentTimeMillis();

		spid = sp.load(myContext, R.raw.button, 1);

		Resources res = this.getResources();
		InputStream is = res.openRawResource(R.raw.fsan1_n);
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, "SJIS");
		} catch (UnsupportedEncodingException e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		}
		BufferedReader br = new BufferedReader(isr);
		ArrayList<long[]> notes = new ArrayList<long[]>();
		String temp0;
		String[] temp1;
		long[] temp2 = new long[3];
		try {
			while ((temp0 = br.readLine()) != null) {
				temp1 = temp0.split(" ", 0);
				temp2[0] = Long.valueOf(temp1[0]);
				System.out.println(temp2[0]);
				temp2[1] = Long.valueOf(temp1[1]);
				System.out.println(temp2[1]);
				temp2[2] = 0;
				notes.add(temp2.clone());
			}
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		for (Iterator<long[]> itr = notes.iterator(); itr.hasNext();) {
			System.out.println(itr.next()[0]);
		}

		mp.start();
		long start = System.currentTimeMillis();

		while (thread != null) {

			try {
				loopCount++;
				canvas = surfaceHolder.lockCanvas();

				canvas.drawRect(0, 0, screen_width, screen_height, bgPaint);
				RECT_SIDE = screen_width / 5;

				for (Iterator<long[]> itr = notes.iterator(); itr.hasNext();) {
					long[] temp = itr.next();
					remaining = temp[0] - time + offset;
					int difficulty = 600;

					if (-200 < remaining && remaining < difficulty && temp[2] == 0) {
						for (int i = 0; i < 9; i++) {
							if (isTouched[i] >= 0) {
								if (i == temp[1]) {
									temp[2] = 1;
									score_now = (long) (1000 * (1 - Math.abs(remaining) / (double) difficulty));
									score += score_now;
									break;
								}
							}
						}
						if (temp[2] == 1)
							continue;
						int x0 = (int) (screen_width / 2 - RECT_SIDE * 2 + RECT_SIDE * 1.5 * (temp[1] % 3) + remaining
								/ (difficulty / (double) 45));
						int x1 = (int) (x0 + RECT_SIDE - remaining / (difficulty / (double) 90));
						int y0 = (int) (screen_height / 2 - RECT_SIDE * 2 + RECT_SIDE * 1.5 * (temp[1] / 3) + remaining
								/ (difficulty / (double) 45));
						int y1 = (int) (y0 + RECT_SIDE - remaining / (difficulty / (double) 90));
						canvas.drawRect(new Rect(x0, y0, x1, y1), rectPaint);
					}
				}

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						int x = (int) (screen_width / 2 - RECT_SIDE * 2 + RECT_SIDE * 1.5 * j);
						int y = (int) (screen_height / 2 - RECT_SIDE * 2 + RECT_SIDE * 1.5 * i);
						panel[i * 3 + j] = new Rect(x, y, x + RECT_SIDE, y + RECT_SIDE);
						if (isTouched[i * 3 + j] >= 0) {
							canvas.drawRect(panel[i * 3 + j], rectPaint2);
						} else {
							canvas.drawRect(panel[i * 3 + j], rectPaint);
						}
					}
				}

				time = System.currentTimeMillis() - start;

				canvas.drawText(String.valueOf(time), 10, 10, paint);
				canvas.drawText(String.valueOf(score), 10, 30, paint);
				canvas.drawText(String.valueOf(score_now), 10, 50, paint);
				canvas.drawText(String.valueOf(pointerId), 10, 70, paint);
				canvas.drawText(
						String.valueOf(x1) + " " + String.valueOf(y1) + " " + String.valueOf(x2) + " "
								+ String.valueOf(y2), 10, 90, paint);
				canvas.drawText(String.valueOf(mPointerID1) + " " + String.valueOf(mPointerID2), 10, 110, paint);
				surfaceHolder.unlockCanvasAndPost(canvas);

				if (System.currentTimeMillis() - start > mp.getDuration()) {
					thread = null;
					FragmentTransaction ft = myFragment.getFragmentManager().beginTransaction();
					Fragment next = new Fragment2();
					ft.replace(R.id.container, next);
					ft.commit();
				}

				waitTime = (loopCount * FRAME_TIME) - (System.currentTimeMillis() - startTime);

				if (waitTime > 0) {
					Thread.sleep(waitTime);
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventAction = event.getActionMasked();
		int pointerIndex = event.getActionIndex();
		pointerId = event.getPointerId(pointerIndex);

		switch (eventAction) {
		case MotionEvent.ACTION_DOWN:
			mPointerID1 = pointerId;
			mPointerID2 = -1;
			for (int i = 0; i < 9; i++) {
				if (panel[i].left <= event.getX() && event.getX() <= panel[i].right && panel[i].top <= event.getY()
						&& event.getY() <= panel[i].bottom) {
					isTouched[i] = pointerId;
					sp.play(spid, 1.0F, 1.0F, 0, 0, 1.0F);
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (mPointerID2 == -1) {
				mPointerID2 = pointerId;
			} else if (mPointerID1 == -1) {
				mPointerID1 = pointerId;
			}
			if (mPointerID1 >= 0) {
				int ptrIndex = event.findPointerIndex(mPointerID1);
				x1 = event.getX(ptrIndex);
				y1 = event.getY(ptrIndex);
			}
			if (mPointerID2 >= 0) {
				int ptrIndex = event.findPointerIndex(mPointerID2);
				x2 = event.getX(ptrIndex);
				y2 = event.getY(ptrIndex);
			}
			for (int i = 0; i < 9; i++) {
				if ((panel[i].left <= x1 && x1 <= panel[i].right && panel[i].top <= y1 && y1 <= panel[i].bottom)
						|| (panel[i].left <= x2 && x2 <= panel[i].right && panel[i].top <= y2 && y2 <= panel[i].bottom)) {
					isTouched[i] = pointerId;
					sp.play(spid, 1.0F, 1.0F, 0, 0, 1.0F);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mPointerID1 = -1;
			mPointerID2 = -1;
			for (int i = 0; i < 9; i++) {
				isTouched[i] = -1;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (mPointerID1 == pointerId) {
				mPointerID1 = -1;
			} else if (mPointerID2 == pointerId) {
				mPointerID2 = -1;
			}
			for (int i = 0; i < 9; i++) {
				if (isTouched[i] == pointerId)
					isTouched[i] = -1;
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		screen_width = width;
		screen_height = height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
		mp.stop();
		sp.release();
	}

}