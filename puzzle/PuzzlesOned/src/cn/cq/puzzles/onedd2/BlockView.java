package cn.cq.puzzles.onedd2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// import org.loon.framework.game.helper.ImageHelper;

public class BlockView extends View {

	private static final int GRID_BG_COLOR = Color.argb(0xff, 0x0, 0x0, 0x0);

	private int delayMillis = 30;

	private Context context = null;

	private Bitmap backImage;

	private Bitmap screen;

	private int[] blocks;

	private boolean isEvent = false;

	private float _width;

	private float _height;

	private static final int ROWS = 4;

	private static final int COLS = 4;

	private int blockWidth;

	private int blockHeight;

	private int imgsCounts;

	private Resources r = null;

	private Paint paint = new Paint();

	private Canvas backCanvas = null;

	private Canvas screenCanvas = null;

	private float rale;

	private boolean running = false;

	private boolean inited = false;

	private int xOffset, yOffset;

	private Bitmap overImage;

	private Rect bufSrcRect = null;

	private RectF bufTarRect = null;

	private int[] gridCols = new int[(COLS + 1) * 4];

	private int[] gridRows = new int[(ROWS + 1) * 4];

	private int c_drawable = R.drawable.backimage;

	// add by cq
	private int init_w;
	private int init_h;

	public BlockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public BlockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (!inited) {
			this.init_w = w;
			this.init_h = h;
			r = context.getResources();
			init(w, h);
			int i = 0;
			// ��ʼ����������
			for (i = 0; i <= COLS; i++) {
				gridCols[i * COLS + 0] = xOffset + i * blockWidth;
				gridCols[i * COLS + 1] = yOffset;
				gridCols[i * COLS + 2] = xOffset + i * blockWidth;
				gridCols[i * COLS + 3] = ROWS * blockHeight + yOffset;
			}

			for (i = 0; i <= ROWS; i++) {
				gridRows[i * ROWS + 0] = xOffset;
				gridRows[i * ROWS + 1] = i * blockHeight + yOffset;
				gridRows[i * ROWS + 2] = COLS * blockWidth + xOffset;
				gridRows[i * ROWS + 3] = i * blockHeight + yOffset;
			}

			bufSrcRect = new Rect(0, 0, (int) _width, (int) _height);

			bufTarRect = new RectF(0, 0, _width, _height);

			inited = true;
			update();
		}
	}

	/**
	 * ��ʼ��ƴͼ������
	 * 
	 * @param bImage
	 * @param overImage
	 * @param cs
	 * @param rs
	 */
	public void init(int w, int h) {
		setFocusable(true);
		// ��ȡ����ͼƬ�� Drawable ��Դ
		Drawable blockImage = r.getDrawable(c_drawable);
		// ���ʵ�ʴ����+
		_width = blockImage.getMinimumWidth();
		// ���ʵ�ʴ���ߡ�+
		_height = blockImage.getMinimumHeight();
		rale = Math.min(w / _width, h / _height);
		_width = (int) (_width * rale);
		_height = (int) (_height * rale);

		xOffset = (w - (int) _width) >> 1;
		yOffset = (h - (int) _height) >> 1;
		// ��õ���ͼ���
		blockWidth = (int) (_width / COLS);
		// ��õ���ͼ��ߡ�
		blockHeight = (int) (_height / ROWS);

		// ������ֱ��ʹ��backimage��һ��ͼ�����򻺳�ѡ�������ʵ�ʱ���ͼ���=ͼ�θ�+����ͼ��ߡ�

		getBackImage(r.getDrawable(c_drawable));
		getScreenImage();
		getOverImage(r.getDrawable(R.drawable.over));

		// ��õ�ͬͼƬ���������飬Ҫ���һ�к�һ������ͼ����ʱ�����
		imgsCounts = COLS * ROWS;

		blocks = new int[imgsCounts];
		// ��ʼ��Ϊ�ǵ����
		isEvent = false;

		// ��ʼ��ͼ�����������ͼ����������
		for (int i = 0; i < imgsCounts; i++) {
			blocks[i] = i;
		}
		// �������ͼ��������ݡ�
		randomPannel();

	}

	private void getBackImage(Drawable src) {
		// ����ͼƬ��һ��
		Bitmap bitmap = Bitmap.createBitmap((int) _width, (int) _height
				+ blockHeight, Bitmap.Config.ARGB_8888);
		backCanvas = new Canvas();
		backCanvas.setBitmap(bitmap);
		src.setBounds(0, 0, (int) _width, (int) _height);
		src.draw(backCanvas);
		backImage = bitmap;
	}

	private void getOverImage(Drawable src) {

		Bitmap bitmap = Bitmap.createBitmap((int) _width, (int) _height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap);
		src.setBounds(0, 0, (int) _width, (int) _height);
		src.draw(canvas);
		overImage = bitmap;
	}

	private void getScreenImage() {
		// ����ͼƬ��һ��
		Bitmap bitmap = Bitmap.createBitmap((int) _width, (int) _height,
				Bitmap.Config.ARGB_8888);
		screenCanvas = new Canvas();
		screenCanvas.setBitmap(bitmap);

		screen = bitmap;
	}

	/**
	 * ����������ͼƬ��
	 * 
	 */
	private void randomPannel() {
		// ��ԴͼƬ��������ͼƬ��
		// ��ȡӦ�ó�����Դ����
		// �ǲ��Ǵ��ҵĴ�������Щ����˵7��ϴ�ƣ�ϴ������
		for (int i = 0; i < imgsCounts + (double) COLS * Math.random(); i++) {
			// ��������໥������СͼƬ�ĸ��Ե�λ��
			int firstPicColNum = (int) ((double) COLS * Math.random());
			int firstPicRowNum = (int) ((double) ROWS * Math.random());
			int anotherPicColNum = (int) ((double) COLS * Math.random());
			int anotherPicRowNum = (int) ((double) ROWS * Math.random());

			// ������֣��������������СͼƬ����λ�á�
			copyPic(firstPicColNum, firstPicRowNum, 0, ROWS);
			copyPic(anotherPicColNum, anotherPicRowNum, firstPicColNum,
					firstPicRowNum);
			copyPic(0, ROWS, anotherPicColNum, anotherPicRowNum);

			// ��blocks���飨��¼СͼƬ��˳�򣬶����˺ţ��м�¼���ҵ�λ�á�
			int indexOfFirstPic = blocks[firstPicRowNum * COLS + firstPicColNum];// �ҵ���ʼ���ƶ���СͼƬ
			blocks[firstPicRowNum * COLS + firstPicColNum] = blocks[anotherPicRowNum
					* COLS + anotherPicColNum];
			blocks[anotherPicRowNum * COLS + anotherPicColNum] = indexOfFirstPic;
		}

	}

	/**
	 * ��ס����λͼ�Ĳ������������λͼ�����꣬���ڻ��Ƶ�ʱ�����������Ļ������
	 * һ��Bitmap�������һ��Canvas���������Canvas���ǿ��������Bitmap���滭 ���������������ͼƬ��Դ�������ȡ�
	 */
	private void doBufferedImage() {
		// ���ײ㱳����
		screenCanvas.drawBitmap(backImage, bufSrcRect, bufTarRect, null);
		if (isEvent) {
			// ������ͼƬ���Ƶ�Screen��
			screenCanvas.drawBitmap(overImage, null, bufTarRect, null);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int c = paint.getColor();
		// ���Ʊ�������
		canvas.drawBitmap(screen, xOffset, yOffset, null);
		c = paint.getColor();
		if (!isEvent) {
			// ѭ������С�����ڱ��������С�
			paint.setColor(GRID_BG_COLOR);
			int i = 0;
			int row_col_count = 0;
			for (i = 0; i <= COLS; i++) {
				row_col_count = i * COLS;
				canvas.drawLine(gridCols[row_col_count],
						gridCols[row_col_count + 1],
						gridCols[row_col_count + 2],
						gridCols[row_col_count + 3], paint);
			}
			row_col_count = 0;
			for (i = 0; i <= ROWS; i++) {
				row_col_count = i * ROWS;
				canvas.drawLine(gridRows[row_col_count],
						gridRows[row_col_count + 1],
						gridRows[row_col_count + 2],
						gridRows[row_col_count + 3], paint);
			}

		}
		paint.setColor(c);
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return true;
		}

		if (isEvent)
			return true;

		int k = (int) ((event.getX() - xOffset) / blockWidth);
		int l = (int) ((event.getY() - yOffset) / blockHeight);

		if (k >= 4)
			k = 3;
		if (l >= 4)
			l = 3;

		// ע������Ľ��������м�סhight �� ����أ���y��ء�width��x��أ��������
		copyPic(0, 0, 0, ROWS);
		copyPic(k, l, 0, 0);
		copyPic(0, ROWS, k, l);

		int i1 = blocks[0];
		// ����ѡ��ͼƬ�洢����
		blocks[0] = blocks[l * COLS + k];
		blocks[l * COLS + k] = i1;

		// ����Ƿ�
		for (int j1 = 0; j1 < imgsCounts; j1++) {
			if (blocks[j1] != j1)
				break;
			if (j1 == imgsCounts - 1)
				isEvent = true;

		}

		invalidate();
		return true;
	}

	/**
	 * copy������ͼ������
	 * 
	 * @param i
	 *            Դͼ����ӵ�xλ��
	 * @param j
	 *            Դͼ����ӵ�yλ��
	 * @param k
	 *            Ŀ���xλ��
	 * @param l
	 *            Ŀ���yλ��
	 */
	void copyPic(int srcCol, int srcRow, int distCol, int distRow) {
		// ע���������������1��2��ΪԴͼ���λ�ã�3��4ΪԴͼ��Ŀ�͸ߣ�5��6Ϊ���Ƶľ��루�����Դͼ���λ�ã�
		RectF dist = new RectF(srcCol * blockWidth + (distCol - srcCol)
				* blockWidth, srcRow * blockHeight + (distRow - srcRow)
				* blockHeight, srcCol * blockWidth + (distCol - srcCol)
				* blockWidth + blockWidth, srcRow * blockHeight
				+ (distRow - srcRow) * blockHeight + blockHeight);

		try {
			// Bitmap.createBitmap ���ƶ���Bitmap�ϵ�һ���ֳַ�������Ϊһ��Bitmap��
			Bitmap subCopy = Bitmap.createBitmap(backImage,
					srcCol * blockWidth, srcRow * blockHeight, blockWidth,
					blockHeight);
			backCanvas.drawBitmap(subCopy, null, dist, null);

			subCopy.recycle(); // ��ʾBitmap������Ӧ����ʾ���ͷ�

		} catch (OutOfMemoryError ex) {
			// ��������γ������ڴ治���쳣�����return ԭʼ��bitmap����.

		}
	}

	/**
	 * �¼�����״̬��
	 * 
	 * @return
	 */
	public boolean isEvent() {
		return isEvent;
	}

	public void setEvent(boolean isEvent) {
		this.isEvent = isEvent;
	}

	private RefreshHandler mRedrawHandler = new RefreshHandler();

	// ���Handlerֱ��������UI���߳�
	class RefreshHandler extends Handler {

		public void handleMessage(Message msg) {
			BlockView.this.invalidate(); // �����ػ�
			BlockView.this.update();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			// �����Ϣ��һ���Ǽ�ʱ���͵ģ��Ժ����ӳ�
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	}

	public void update() {
		if (running) {
			doBufferedImage();
			mRedrawHandler.sleep(delayMillis);
		}
	}

	public void setUpdateRunningAfterInited(boolean running) {
		this.running = running;
		if (inited) {
			update();
		}
	}

	// ��ͼ
	public void change(int drawable) {
		this.c_drawable = drawable;
		this.inited = false;
		this.init(init_w, init_h);
		this.update();
	}
}
