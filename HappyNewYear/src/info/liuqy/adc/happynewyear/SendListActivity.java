package info.liuqy.adc.happynewyear;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class SendListActivity extends ListActivity {
	
    static final String KEY_TO = "TO";
    static final String KEY_SMS = "SMS";
    
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

}
