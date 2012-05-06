package info.liuqy.adc.happynewyear;

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
	
	}

}
