package info.liuqy.adc.happynewyear;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;

public class HappyNewYearActivity extends Activity {
	enum Market {
		NORTH, SOUTH;
		@Override
		public String toString() {
			switch (this) {
			case NORTH:
				return "NC";
			case SOUTH:
				return "SC";
			default:
				return super.toString();
			}
		}
	};

	enum Language {
		CHINESE, ENGLISH;
		@Override
		public String toString() {
			switch (this) {
			case CHINESE:
				return "CN";
			case ENGLISH:
				return "EN";
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
				ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);

		while (cur.moveToNext()) {
			String contactId = cur.getString(cur.getColumnIndex(Contacts._ID));
			
			//retrieve phone numbers
			int phoneCount = cur.getInt(cur.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
			
			//only process contacts with phone numbers
			if (phoneCount > 0) {
			
				//retrieve nickname
			
				//retrieve note
				
			}
			
		}
		
		return sendlist;
	}

}