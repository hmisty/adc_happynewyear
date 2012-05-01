package info.liuqy.adc.happynewyear;

import android.app.Activity;
import android.os.Bundle;

public class HappyNewYearActivity extends Activity {
    enum Market {
        NORTH, SOUTH;
        @Override public String toString() {
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
        @Override public String toString() {
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
     * Return all number ~ nickname pairs according to the rule.
     * Be careful: the same numbers will be in only one pair.
     * @return <number, nickname>s
     */
    public Bundle readContacts(Market market, Language lang) {
        Bundle sendlist = new Bundle();
               
        return sendlist;
    }
    
}