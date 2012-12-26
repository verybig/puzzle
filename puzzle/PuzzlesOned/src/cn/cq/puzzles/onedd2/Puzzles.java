package cn.cq.puzzles.onedd2;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.pad.android.iappad.AdController;
import com.umeng.analytics.MobclickAgent;

public class Puzzles extends Activity {
	/** Called when the activity is first created. */

	private BlockView draw = null;
	private ArrayList<Integer> imgSourceIdlist = null;
	int tag = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��������ź�����Activity��Title�����ص����ɣ�����Title���Ĵ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// �����ź���
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		draw = (BlockView) findViewById(R.id.draw);

		// ������ť����һ������һ��
		final ImageButton lastButton = (ImageButton) findViewById(R.id.lastButton);
		final ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);

		// ����
		lastButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showLastPic(draw);
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showNextPic(draw);
			}
		});

		// ��ʼ��ͼƬ
		loadPicSource();

		// leadbolt banner
		AdController adCont = new AdController(this, "214458594");
		adCont.loadAd();
	}

	/**
	 * װ��ͼƬ��Դ������
	 */
	public void loadPicSource() {
		// �÷���װ��ͼƬ����
		Class cc = R.drawable.class;
		Field[] fields = cc.getFields();
		imgSourceIdlist = new ArrayList<Integer>();
		try {
			for (Field f : fields) {
				String s = f.getName();
				if (s.startsWith("puz")) {
					int i = f.getInt(cc);
					imgSourceIdlist.add(i);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("dataerror", "load data error in ImageData.loadLocalImage");
		}
	}

	// ��һ��
	public void showNextPic(BlockView v) {
		tag = ++tag % this.imgSourceIdlist.size();
		v.change(imgSourceIdlist.get(tag));
	}

	// ��һ��
	public void showLastPic(BlockView v) {
		if (tag == 0) {
			tag = this.imgSourceIdlist.size();
		}
		tag = --tag % this.imgSourceIdlist.size();
		v.change(imgSourceIdlist.get(tag));
	}

	@Override
	protected void onPause() {// ��ʵ���������onCreate֮ǰ����
		draw.setUpdateRunningAfterInited(false);
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		draw.setUpdateRunningAfterInited(true);
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	// ���˼�
	public void onBackPressed() {
		// �Ƿ�����
		new AlertDialog.Builder(this)
				.setTitle("Exit")
				.setMessage("thanks! Rate this APP?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								// ȷ����ȥ����
								String pname = WelAct.class.getPackage()
										.getName();
								String url = "market://details?id=" + pname;
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(url));
								startActivity(intent);
							}
						})
				.setNegativeButton("Exit",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								// �˳�
								System.exit(0);
							}
						}).show();

	}
}