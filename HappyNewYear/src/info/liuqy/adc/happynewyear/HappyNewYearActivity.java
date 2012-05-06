package info.liuqy.adc.happynewyear;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class HappyNewYearActivity extends Activity {
	enum Market {
		NORTH, SOUTH, ANY;
		@Override
		public String toString() {
			switch (this) {
			case NORTH:
				return "NC";
			case SOUTH:
				return "SC";
			case ANY:
				return "";
			default:
				return super.toString();
			}
		}
	};

	enum Language {
		CHINESE, ENGLISH, ANY;
		@Override
		public String toString() {
			switch (this) {
			case CHINESE:
				return "CN";
			case ENGLISH:
				return "EN";
			case ANY:
				return "";
			default:
				return super.toString();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	/**
	 * Return all number ~ nickname pairs according to the rule. Be careful: the
	 * same numbers will be in only one pair.
	 * 
	 * @return <number, nickname>s
	 */
	public Bundle readContacts(Market market, Language lang) {
		Bundle sendlist = new Bundle();

		/*
		 * ContactsContract defines an extensible database of contact-related
		 * information. Contact information is stored in a three-tier data
		 * model:
		 * 
		 * A row in the ContactsContract.Data table can store any kind of
		 * personal data, such as a phone number or email addresses. The set of
		 * data kinds that can be stored in this table is open-ended. There is a
		 * predefined set of common kinds, but any application can add its own
		 * data kinds.
		 * 
		 * A row in the ContactsContract.RawContacts table represents a set of
		 * data describing a person and associated with a single account (for
		 * example, one of the user's Gmail accounts).
		 * 
		 * A row in the ContactsContract.Contacts table represents an aggregate
		 * of one or more RawContacts presumably describing the same person.
		 * When data in or associated with the RawContacts table is changed, the
		 * affected aggregate contacts are updated as necessary.
		 * 
		 * In this program, what we want to get are the <phone number, nickname,
		 * note> data triplets. So let's go through the contacts.
		 */
		Cursor cur = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		// attributes for the contact
		Set<String> attrs = new HashSet<String>();
		
		while (cur.moveToNext()) {
			String contactId = cur.getString(cur.getColumnIndex(Contacts._ID));

			// retrieve phone numbers
			int phoneCount = cur.getInt(cur
					.getColumnIndex(Contacts.HAS_PHONE_NUMBER));

			// only process contacts with phone numbers
			if (phoneCount > 0) {

				Cursor nicknames = getContentResolver().query(
						Data.CONTENT_URI,
						new String[] { Data._ID, Nickname.NAME },
						Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
								+ Nickname.CONTENT_ITEM_TYPE + "'",
						new String[] { contactId }, null);

				// only process contacts with nickname (the first one)
				if (nicknames.moveToFirst()) {
					String nickname = nicknames.getString(nicknames  
                            .getColumnIndex(Nickname.NAME));
					
					Cursor notes = getContentResolver().query(  
	                        Data.CONTENT_URI,  
	                        new String[] { Data._ID, Note.NOTE },  
	                        Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"  
	                                + Note.CONTENT_ITEM_TYPE + "'",  
	                        new String[] { contactId }, null);
					
					// retrieve all attributes from all notes
					attrs.clear();
					while (notes.moveToNext()) {
						String noteinfo = notes.getString(notes  
                                .getColumnIndex(Note.NOTE));
						String[] fragments = noteinfo.toUpperCase().split(","); //FIXME better regex?
						for (String attr : fragments) {
							attrs.add(attr);
						}
					}
					
					notes.close();
					
					//set defaults
					if (!attrs.contains(Market.NORTH.toString())
							&& !attrs.contains(Market.SOUTH.toString()))
						attrs.add(Market.NORTH.toString());
					
					if (!attrs.contains(Language.CHINESE.toString())
							&& !attrs.contains(Language.ENGLISH.toString()))
						attrs.add(Language.CHINESE.toString());
					
					// only process contacts with the matching market & language
					if (attrs.contains("ADC") //FIXME for class demo only
							&& (market.equals(Market.ANY) || attrs.contains(market.toString())) 
							&& (lang.equals(Language.ANY) || attrs.contains(lang.toString()))) {
						
						Cursor phones = getContentResolver().query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null, Phone.CONTACT_ID + "=" + contactId, null, null);

						// process all phone numbers
						while (phones.moveToNext()) {
							String phoneNumber = phones.getString(phones
									.getColumnIndex(Phone.NUMBER));
							int phoneType = phones.getInt(phones
									.getColumnIndex(Phone.TYPE));
							
							if (isMobile(phoneNumber, phoneType)) {
								sendlist.putString(phoneNumber, nickname);
							}
						}
						
						phones.close();
					}
				}
				
				nicknames.close();
			}
		}
		
		cur.close();

		return sendlist;
	}

	// the tricky pattern for identifying Chinese mobile numbers
	static final Pattern MOBILE_PATTERN = Pattern.compile("(13|15|18)\\d{9}");

	public boolean isMobile(String number, int type) {
		if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
			Matcher m = MOBILE_PATTERN.matcher(number);
			
			if (m.find()) {
				return true;
			}
		}
		
		return false;
	}

    public static final String SENDLIST = "info.liuqy.adc.happynewyear.SENDLIST";
    public static final String CUSTOMER_CARER = "info.liuqy.adc.happynewyear.CUSTOMER_CARER";
    public static final String SMS_TEMPLATE = "info.liuqy.adc.happynewyear.SMS_TEMPLATE";   

    public void genSendlist(View v) {
        RadioGroup rg = (RadioGroup)this.findViewById(R.id.customer_group);
        int id = rg.getCheckedRadioButtonId();
        Market targetMarket = (id == R.id.btn_north) ? Market.NORTH : Market.SOUTH;

        rg = (RadioGroup)this.findViewById(R.id.customer_lang);
        id = rg.getCheckedRadioButtonId();
        Language targetLanguage = (id == R.id.btn_cn) ? Language.CHINESE : Language.ENGLISH;

        Spinner sp = (Spinner)this.findViewById(R.id.customer_carer);
        String cc = sp.getSelectedItem().toString();
        
        EditText et = (EditText)this.findViewById(R.id.sms_template);
        String tmpl = et.getText().toString();
        
        Bundle sendlist = readContacts(targetMarket, targetLanguage);
        
        Intent i = new Intent(this, SendListActivity.class);
        i.putExtra(SENDLIST, sendlist);
        i.putExtra(CUSTOMER_CARER, cc);
        i.putExtra(SMS_TEMPLATE, tmpl);
        startActivity(i);
    }

}