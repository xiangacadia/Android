/**@author Jiang, X.
 * Tested on 	Sumsang S5670	: succeed
 * 		xiaomi		: failure
 */

package com.android.rhtunning;

import cn.waps.AppConnect;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * A <code>TunningActivity</code> object represents the main activity of the
 * Erhu Tunning activity.
 * 
 */
public class TunningActivity extends Activity {

    /**
     * Standard frequency of the inside string.
     */
    final int standardInside = 293;
    
    /**
     * Standard frequency of the outside string.
     */
    final int standardOutside = 442;

    /**
     * Surface view where the graph of frequency will be drawn.
     */
    SurfaceView sfv;
    
    /**
     * Surface holder which is to hold the surface view.
     */
    SurfaceHolder sfh;
    
    /**
     * The DrawImage object is used to draw the current frequency on the canvas with a new thread.
     */
    DrawImage myDrawImage;

    /**
     * The positions in the lines of the basic degree scales to draw.
     */
    int[] lines2Draw;
    
    /**
     * The offset between the standard frequency and the current frequency
     * ranging from -60 to 60.
     */
    int offset = -60;
    
    /**
     * The real-time frequency.
     */
    int currentFrequency = 0;

    /**
     * The width of the current screen.
     */
    int xlen;
    
    /**
     * The height of the current screen.
     */
    int ylen;

    /**
     * If isDrawingAllowed is true, it means that the canvas is ready to draw.
     * It is also used in the control of multi-threading.
     */
    boolean isDrawingAllowed = false;
    
    /**
     * If isAudioAvaliable is true, it means that the AudioRecorder is ready to record.
     * It is also used in the control of multi-threading.
     */
    boolean isAudioAvaliable = false;

    /**
     * Object to get the audio from microphone input.
     */
    AudioRecord record;

    /**
     * The sample rate to read from the audio recorder.
     */
    int sampleRate = 8000;
    
    /**
     * The min bytes of the audio recorder can read in one time.
     */
    int minBytes = 0;
    
    /**
     * Used to store the audio samples from microphone input.
     */
    short[] audioSamples;

    /**
     * Timing for ads showing.
     */
    Handler handler;

    /**
     * onCreate is where you initialize your activity. Here it will initialize
     * the surface, audio, as well as the ads.
     * 
     * @param <em>savedInstanceState</em> If the activity is being
     *        re-initialized after previously being shut down then this Bundle
     *        contains the data it most recently supplied in
     *        onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_tunning);

	//通过AndroidManifest文件读取WAPS_ID和WAPS_PID
	AppConnect.getInstance(this); // 必须确保AndroidManifest文件内配置了WAPS_ID

	handler = new Handler();

	handler.postDelayed(runnable, 60000);// 每两秒执行一次runnable

	initSurface();

	initAudio();

	log("loading ads");

	AppConnect.getInstance(this).initPopAd(this);

    }

    /**
     * Initialize the contents of the Activity's standard options menu. 
     * You should place your menu items in to menu.
     * This is only called once, the first time the options menu is displayed. 
     * To update the menu every time it is displayed, see onPrepareOptionsMenu(Menu).
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_tunning, menu);

	return true;
    }

    
    /**
     * Called when a key was pressed down and not handled by any of the views inside of the activity. 
     * 
     * When the BACK key is triggered, the threads of the activity will stop,
     * remove the timer and release all kinds of resources.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isDrawingAllowed = false;
	    isAudioAvaliable = false;

	    handler.removeCallbacks(runnable);

	    // 以下方法将用于释放SDK占用的系统资源
	    AppConnect.getInstance(this).finalize();

	    System.exit(0);
	}

	if (keyCode == KeyEvent.KEYCODE_MENU) {
	    AppConnect.getInstance(this).showPopAd(this);
	}

	return false;
    }

    /**
     * Initialize the configuration of the Audio Record.
     * Get min bytes of it which is pow of 2.
     */
    private void initAudio() {
	sampleRate = 8000;
	minBytes = AudioRecord.getMinBufferSize(sampleRate,
		AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

	// MIC: Microphone audio source
	record = new AudioRecord(android.media.MediaRecorder.AudioSource.MIC,
		sampleRate, AudioFormat.CHANNEL_IN_MONO,
		AudioFormat.ENCODING_PCM_16BIT, minBytes);

	if (record == null) {
	    log("record == null");
	    return;
	}

	if (record.getState() == AudioRecord.STATE_INITIALIZED) {
	    log("Audio recorder initialised at " + record.getSampleRate());
	    isAudioAvaliable = true;
	} else {
	    log("Audio recorder failed to initialise");
	    record.release();
	    return;
	}

	record.startRecording();

	log("record.startRecording");

	audioSamples = new short[minBytes];

	// recal minBytes
	record.read(audioSamples, 0, minBytes);
	int pow = 1;
	for (int i = audioSamples.length - 1; i > 0; i--) {
	    if (audioSamples[i] != 0) {
		minBytes = i;

		while (pow < minBytes) {
		    pow *= 2;

		}

		minBytes = pow / 2;

		break;
	    }

	}

	if (pow == 1) {

	    while (audioSamples.length >= pow) {
		pow *= 2;
	    }
	    minBytes = pow / 2;
	}

	// log("minBytes " + Integer.toString(minBytes));
    }

    /**
     * Initialize surface view, surface holder and add callback.
     */
    private void initSurface() {

	sfv = (SurfaceView) this.findViewById(R.id.surfaceView);

	sfh = sfv.getHolder();
	sfh.addCallback(new MyCallBack());// 自动运行surfaceCreated以及surfaceChangedinitLines();
    }

    /**
     * Initialize the standard degree scale of the current screen.
     */
    private void initLines() {
	lines2Draw = new int[4 * 21];
	int mycita, xpos1, xpos2, ypos1, ypos2;
	xlen = sfv.getWidth();
	ylen = sfv.getHeight();
	int counter = 0;

	for (int cita = -60; cita <= 60; cita += 6) {

	    if (cita < 0) {
		mycita = -cita;

		xpos1 = (int) (xlen / 2 - 0.4 * xlen
			* Math.sin((double) mycita / 180 * 3.14));
		xpos2 = (int) (xlen / 2 - 0.5 * xlen
			* Math.sin((double) mycita / 180 * 3.14));

	    } else {
		mycita = cita;
		xpos1 = (int) (xlen / 2 + 0.4 * xlen
			* Math.sin((double) mycita / 180 * 3.14));
		xpos2 = (int) (xlen / 2 + 0.5 * xlen
			* Math.sin((double) mycita / 180 * 3.14));

	    }

	    ypos1 = (int) (ylen * 2 / 3 - 0.4 * xlen
		    * Math.cos((double) mycita / 180 * 3.14));
	    ypos2 = (int) (ylen * 2 / 3 - 0.5 * xlen
		    * Math.cos((double) mycita / 180 * 3.14));

	    lines2Draw[counter * 4] = xpos1;
	    lines2Draw[counter * 4 + 1] = ypos1;
	    lines2Draw[counter * 4 + 2] = xpos2;
	    lines2Draw[counter * 4 + 3] = ypos2;

	    counter++;

	}

    }

    /**
     * A <code>MyCallBack</code> object is used to start the DrawImage thread
     * and the GetFrequency thread after the surface is created.
     */
    class MyCallBack implements SurfaceHolder.Callback {

	/**
	 * Triggered when the surface is changed.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
	    Log.i("Surface:", "-johnny-Change");
	}

	/**
	 * Triggered when the surface is created.
	 * Some operations on the canvas can only be done after the surface is created.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    Log.i("Surface:", "-johnny-Create");

	    initLines();
	    
	    isDrawingAllowed = true;
	    //log(Boolean.toString(isDrawingAllowed));

	    myDrawImage = new DrawImage();
	    myDrawImage.start();

	    GetFrequency myGetFrequency = new GetFrequency();
	    myGetFrequency.start();
	}

	/**
	 * Triggered when the surface is Destroyed.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    Log.i("Surface:", "-johnny-Destroy");
	}
    }

    /**
     * The <code>GetFrequency</code> object is used to get the current frequency
     * of the input sound in a thread.
     */
    class GetFrequency extends Thread {

	public void run() {

	    startRecording();

	}

	public int abs(int x) {
	    if (x >= 0) {
		return x;
	    } else {
		return -x;
	    }
	}

	private void startRecording() {

	    log("startRecording");

	    while (isDrawingAllowed && isAudioAvaliable) {

		// log("upperBound:" + Integer.toString(upperBound));

		// Get input from microphone
		record.read(audioSamples, 0, minBytes);

		// log("minBytes " + Integer.toString(minBytes));

		// decrease noise

		for (int i = 0 + 2; i < minBytes - 2; i++) {
		    audioSamples[i] = (short) ((audioSamples[i - 2]
			    + audioSamples[i - 1] + audioSamples[i]
			    + audioSamples[i + 1] + audioSamples[i + 2]) / 5);
		    // DEBUG
		    // log("audioSamples[i]," + Integer.toString(i) + ',' +
		    // Integer.toString(audioSamples[i]));

		}

		// fft
		double[] real;
		double[] img = new double[minBytes];

		real = Short2Double(audioSamples);
		FFT myFFT = new FFT(minBytes);
		myFFT.fft(real, img);

		double[] absFFTResult = new double[minBytes];

		for (int i = 0; i < minBytes; i++) {
		    absFFTResult[i] = real[i] * real[i] + img[i] * img[i];

		}

		int index = 0;
		// get frequency
		for (int i = minBytes / 50; i < /* upperBound */minBytes / 2; i++) {
		    if (absFFTResult[i] > absFFTResult[index]) {
			index = i;
		    }
		}

		currentFrequency = index * sampleRate / minBytes;

		offset = (currentFrequency - standardOutside) / 5;

		if (offset < -60) {
		    offset = -60;
		} else {
		    if (offset > 60) {
			offset = 60;
		    }
		}

	    }// end while (isDrawingAllowed)

	    // log("A");
	    if (isAudioAvaliable) {
		record.stop();
		record.release();
	    }
	}

	private double[] Short2Double(short[] data) {
	    double[] result = new double[data.length];
	    for (int i = 0; i < data.length; i++) {
		result[i] = data[i];
	    }
	    return result;
	}

    }

    /**
     * The <code>FFT</code> class is used to get the result of Fast Fourier
     * Transfer.
     * 
     */
    public class FFT {

	int n, m;

	// Lookup tables. Only need to recompute when size of FFT changes.
	double[] cos;
	double[] sin;

	public FFT(int n) {
	    this.n = n;
	    this.m = (int) (Math.log(n) / Math.log(2));

	    // Make sure n is a power of 2
	    if (n != (1 << m))
		throw new RuntimeException("FFT length must be power of 2");

	    // precompute tables
	    cos = new double[n / 2];
	    sin = new double[n / 2];

	    for (int i = 0; i < n / 2; i++) {
		cos[i] = Math.cos(-2 * Math.PI * i / n);
		sin[i] = Math.sin(-2 * Math.PI * i / n);
	    }

	}

	public void fft(double[] x, double[] y) {
	    int i, j, k, n1, n2, a;
	    double c, s, t1, t2;

	    // Bit-reverse
	    j = 0;
	    n2 = n / 2;
	    for (i = 1; i < n - 1; i++) {
		n1 = n2;
		while (j >= n1) {
		    j = j - n1;
		    n1 = n1 / 2;
		}
		j = j + n1;

		if (i < j) {
		    t1 = x[i];
		    x[i] = x[j];
		    x[j] = t1;
		    t1 = y[i];
		    y[i] = y[j];
		    y[j] = t1;
		}
	    }

	    // FFT
	    n1 = 0;
	    n2 = 1;

	    for (i = 0; i < m; i++) {
		n1 = n2;
		n2 = n2 + n2;
		a = 0;

		for (j = 0; j < n1; j++) {
		    c = cos[a];
		    s = sin[a];
		    a += 1 << (m - i - 1);

		    for (k = j; k < n; k = k + n2) {
			t1 = c * x[k + n1] - s * y[k + n1];
			t2 = s * x[k + n1] + c * y[k + n1];
			x[k + n1] = x[k] - t1;
			y[k + n1] = y[k] - t2;
			x[k] = x[k] + t1;
			y[k] = y[k] + t2;
		    }
		}
	    }
	}
    }

    /**
     * The <code>DrawImage</code> object is used to draw the current frequency
     * on the canvas with a new thread.
     * 
     */
    class DrawImage extends Thread {
	
	

	int x = 0;
	int y = 0;

	int pntX1, pntX2, pntY1, pntY2;

	public void run() {
	    
	    log("start drawing");

	    pntX1 = xlen / 2;
	    pntY1 = ylen * 2 / 3;

	    Paint thinPaint = new Paint();
	    thinPaint.setColor(Color.BLUE);
	    thinPaint.setStrokeWidth(xlen / 100);
	    thinPaint.setAntiAlias(true);

	    Paint boldPaint = new Paint();
	    boldPaint.setColor(Color.RED);
	    boldPaint.setStrokeWidth(xlen / 50);

	    boldPaint.setAntiAlias(true);

	    Paint pointerPaint = new Paint();
	    pointerPaint.setColor(Color.BLACK);
	    pointerPaint.setStrokeWidth(xlen / 100);
	    pointerPaint.setAntiAlias(true);

	    Paint textPaint = new Paint();
	    textPaint.setColor(Color.BLACK);
	    textPaint.setTextSize(xlen / 20);
	    textPaint.setTextAlign(Paint.Align.LEFT);
	    textPaint.setAntiAlias(true);

	    Paint centralPaint = new Paint();
	    centralPaint.setColor(Color.GREEN);
	    centralPaint.setStrokeWidth(xlen / 25);
	    centralPaint.setAntiAlias(true);

	    Paint myPaint;

	    while (isDrawingAllowed) {
		
		//log(Boolean.toString(isDrawingAllowed));

		Canvas canvas = sfh.lockCanvas(new Rect(0, 0, sfv.getWidth(),
			sfv.getHeight()));

		canvas.drawColor(Color.WHITE);

		if (isAudioAvaliable) {
		    canvas.drawText(
			    "当前频率为:" + Integer.toString(currentFrequency)
				    + "Hz", xlen / 3, ylen * 3 / 4, textPaint);
		} else {
		    canvas.drawText("读取音频信号失败！", xlen / 3, ylen * 3 / 4,
			    textPaint);
		}

		
		
		
		for (int i = 0; i < 21; i++) {
		    if (i == 10) {
			myPaint = centralPaint;
		    } else if (i % 5 == 0) {
			myPaint = boldPaint;
		    } else {
			myPaint = thinPaint;
		    }

		    canvas.drawLine(lines2Draw[i * 4], lines2Draw[i * 4 + 1],
			    lines2Draw[i * 4 + 2], lines2Draw[i * 4 + 3],
			    myPaint);

		    if (Math.abs(offset) > 60) {
			continue;
		    }
		}

		if (offset < 0) {

		    pntX2 = (int) (xlen / 2 - 0.4 * xlen
			    * Math.sin((double) (-offset) / 180 * 3.14));
		    
		} else {
		    pntX2 = (int) (xlen / 2 + 0.4 * xlen
			    * Math.sin((double) offset / 180 * 3.14));
		}

		pntY2 = (int) (ylen * 2 / 3 - 0.5 * xlen
			* Math.cos((double) offset / 180 * 3.14));

		canvas.drawLine(pntX1, pntY1, pntX2, pntY2, pointerPaint);

		sfh.unlockCanvasAndPost(canvas);
	    }
	}
    }

    // Send log info to Logcat
    protected void log(String str) {
	System.out.println("-johnny-" + str);

    }

    protected void showAds() {
	AppConnect.getInstance(this).showPopAd(this);
    }

    Runnable runnable = new Runnable() {
	@Override
	public void run() {

	    log("showing ads");
	    showAds();
	    handler.postDelayed(this, 30000);
	}
    };
}