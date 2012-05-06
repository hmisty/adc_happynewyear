package info.liuqy.adc.happynewyear;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.SimpleAdapter;

public class SendListActivity extends ListActivity {
	
    static final String KEY_TO = "TO";
    static final String KEY_SMS = "SMS";

    static final String SENT_ACTION = "SMS_SENT_ACTION";
    static final String DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
    static final String EXTRA_IDX = "contact_adapter_idx";
    static final String EXTRA_TONUMBER = "sms_to_number";
    static final String EXTRA_SMS = "sms_content";
    
    //[<TO, number>,<SMS, sms>]
    List<Map<String, String>> smslist = new LinkedList<Map<String, String>>();
    SimpleAdapter adapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendlist);
        
        adapter = new SimpleAdapter(this, smslist,
                android.R.layout.simple_list_item_2,
                new String[]{KEY_TO, KEY_SMS},
                new int[]{android.R.id.text1, android.R.id.text2});
        this.setListAdapter(adapter);
        handleIntent();
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

	
}
