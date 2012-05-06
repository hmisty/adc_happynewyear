package info.liuqy.adc.happynewyear;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SendListActivity extends ListActivity {
	
    static final String KEY_TO = "TO";
    static final String KEY_SMS = "SMS";

    static final String SENT_ACTION = "SMS_SENT_ACTION";
    static final String DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
    static final String EXTRA_IDX = "contact_adapter_idx";
    static final String EXTRA_TONUMBER = "sms_to_number";
    static final String EXTRA_SMS = "sms_content";
    
    private static final int HAPPYNEWYEAR_ID = 1;

    private static final String DB_NAME = "data";
    private static final int DB_VERSION = 2;
    
    private static final String TBL_NAME = "sms";
    static final String FIELD_TO = "to";
    static final String FIELD_SMS = "sms";
    static final String KEY_ROWID = "_id";
    
    //[<TO, number>,<SMS, sms>]
    List<Map<String, String>> smslist = new LinkedList<Map<String, String>>();
    SimpleAdapter adapter;

    static BroadcastReceiver smsSentReceiver = null;
	static BroadcastReceiver smsDeliveredReceiver = null;
    
    SQLiteOpenHelper dbHelper = null;
    SQLiteDatabase db = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendlist);
        
        initdb();
        createReceivers();
        
        adapter = new SimpleAdapter(this, smslist,
                android.R.layout.simple_list_item_2,
                new String[]{KEY_TO, KEY_SMS},
                new int[]{android.R.id.text1, android.R.id.text2});
        this.setListAdapter(adapter);
        handleIntent();
        
        if (smslist.size() == 0)  //FIXME need a better judge if from notification
            loadFromDatabase();
    }
	
	public void handleIntent() {
        Bundle data = this.getIntent().getExtras();
        if (data != null) {
            Bundle sendlist = data.getParcelable(HappyNewYearActivity.SENDLIST);
            
            String cc = data.getString(HappyNewYearActivity.CUSTOMER_CARER);
            String tmpl = data.getString(HappyNewYearActivity.SMS_TEMPLATE);
            
            tmpl = tmpl.replaceAll("\\{FROM\\}", cc);
            
            for (String n : sendlist.keySet()) {
                String sms = tmpl.replaceAll("\\{TO\\}", sendlist.getString(n));
                Map<String, String> rec = new Hashtable<String, String>();
                rec.put(KEY_TO, n);
                rec.put(KEY_SMS, sms);
                smslist.add(rec);
                adapter.notifyDataSetChanged();
            }
        }

	}

	public void sendSms(View v) {
        SmsManager sender = SmsManager.getDefault();
        if (sender == null) {
            // TODO toast error msg
        }

        for (int idx = 0; idx < smslist.size(); idx++) {
            Map<String, String> rec = smslist.get(idx);
            String toNumber = rec.get(KEY_TO);
            String sms = rec.get(KEY_SMS);

            // SMS sent pending intent
            Intent sentActionIntent = new Intent(SENT_ACTION);
            sentActionIntent.putExtra(EXTRA_IDX, idx);
            sentActionIntent.putExtra(EXTRA_TONUMBER, toNumber);
            sentActionIntent.putExtra(EXTRA_SMS, sms);
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(
                    this, 0, sentActionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // SMS delivered pending intent
            Intent deliveredActionIntent = new Intent(DELIVERED_ACTION);
            deliveredActionIntent.putExtra(EXTRA_IDX, idx);
            deliveredActionIntent.putExtra(EXTRA_TONUMBER, toNumber);
            deliveredActionIntent.putExtra(EXTRA_SMS, sms);
            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(
                    this, 0, deliveredActionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            //send
            sender.sendTextMessage(toNumber, null, sms, sentPendingIntent,
                    deliveredPendingIntent);
        }
    }

	@Override
	protected void onStart() {
		super.onStart();
		// Question for you: where is the right place to register receivers?
		registerReceivers();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// Question for you: where is the right place to unregister receivers?
		unregisterReceivers();
	}
	
	protected void createReceivers() {
		if (smsSentReceiver == null)
			smsSentReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					int idx = intent.getIntExtra(EXTRA_IDX, -1);
					String toNum = intent.getStringExtra(EXTRA_TONUMBER);
					String sms = intent.getStringExtra(EXTRA_SMS);
					int succ = getResultCode();
					if (succ == Activity.RESULT_OK) {
						// TODO better notification
						Toast.makeText(SendListActivity.this,
								"Sent to " + toNum + " OK!", Toast.LENGTH_SHORT)
								.show();
					} else {
						// TODO
					}
				}
			};

		if (smsDeliveredReceiver == null)
			smsDeliveredReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					int idx = intent.getIntExtra(EXTRA_IDX, -1);
					String toNum = intent.getStringExtra(EXTRA_TONUMBER);
					String sms = intent.getStringExtra(EXTRA_SMS);
					int succ = getResultCode();
					if (succ == Activity.RESULT_OK) {
						// TODO better notification
						//Toast.makeText(SendListActivity.this, "Delivered to " + toNum + " OK!", Toast.LENGTH_SHORT).show();
						notifySuccessfulDelivery("Delivered to " + toNum + " OK!", sms);
					} else {
						// TODO
					}
				}
			};
	}

	protected void registerReceivers() {
		this.registerReceiver(smsSentReceiver, new IntentFilter(SENT_ACTION));
		this.registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED_ACTION));
	}
	
	protected void unregisterReceivers() {
		this.unregisterReceiver(smsSentReceiver);
		this.unregisterReceiver(smsDeliveredReceiver);
	}
	
    public void notifySuccessfulDelivery(String title, String text) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "HappyNewYear";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        
        Context context = getApplicationContext();
        CharSequence contentTitle = title;
        CharSequence contentText = text;
        Intent notificationIntent = new Intent(this, SendListActivity.class); //if click, then open SendListActivity
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        mNotificationManager.notify(HAPPYNEWYEAR_ID, notification);
    }

    protected void initdb() {
        dbHelper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("create table sms (_id integer primary key autoincrement, " +
                        "to_number text not null, sms text not null)");
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
                //TODO on DB upgrade
            }
            
        };
        
        db = dbHelper.getWritableDatabase();
    }
    
    protected void loadFromDatabase() {
        Cursor cur = db.query(TBL_NAME, new String[]{KEY_ROWID, FIELD_TO, FIELD_SMS},
                null, null, null, null, null);

        while (cur.moveToNext()) {
            String toNumber = cur.getString(cur.getColumnIndex(FIELD_TO));
            String sms = cur.getString(cur.getColumnIndex(FIELD_SMS));
            Map<String, String> rec = new Hashtable<String, String>();
            rec.put(KEY_TO, toNumber);
            rec.put(KEY_SMS, sms);
            smslist.add(rec);
        }
        
        cur.close();
        
        adapter.notifyDataSetChanged();
    }
}
