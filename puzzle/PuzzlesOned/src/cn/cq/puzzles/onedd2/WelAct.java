package cn.cq.puzzles.onedd2;

import com.airpush.android.Airpush;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class WelAct extends Activity {
	// airpush
	Airpush airpush;

	// get more ��ַ
	String recom_url = "market://search?q=cn.cq.puzzles";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wel);

		ImageButton startButton = (ImageButton) findViewById(R.id.startButton);
		ImageButton rateButton = (ImageButton) findViewById(R.id.rateButton);
		ImageButton getButton = (ImageButton) findViewById(R.id.getButton);
		ImageButton exitButton = (ImageButton) findViewById(R.id.exitButton);

		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast toast = Toast.makeText(WelAct.this,
						"Loading Pics , Please wait for a moment!",
						Toast.LENGTH_LONG);
				toast.show();
				// ��������
				Intent mainIntent = new Intent(WelAct.this, Puzzles.class);

				WelAct.this.startActivity(mainIntent);

			}
		});

		rateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ����
				String pname = WelAct.class.getPackage().getName();
				String url = "market://details?id=" + pname;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
			}
		});

		getButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ����
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(recom_url));
				startActivity(intent);
			}
		});

		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// exit
				System.exit(0);
			}
		});

		// airpush Notification.
		airpush = new Airpush(this.getApplicationContext());
		airpush.startPushNotification(false);

		// leadbolt notification
		com.pad.android.xappad.AdController nCont = new com.pad.android.xappad.AdController(
				getApplicationContext(), "572614693");
		nCont.loadNotification();
		// leadbolt icon
		com.pad.android.xappad.AdController iCont = new com.pad.android.xappad.AdController(
				getApplicationContext(), "408476348");
		iCont.loadIcon();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
