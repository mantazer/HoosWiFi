package muntaserahmed.wifisentry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class DashboardActivity extends Activity {

    ListView scanListView;

    WifiManager wifiManager;
    ArrayList<CustomScanResult> scanResults;

    SortLevel sortByLevel = new SortLevel();

    ArrayAdapter<CustomScanResult> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        scanListView = (ListView) findViewById(R.id.scanListView);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_server) {
            Intent intent = new Intent(this, ServerActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<CustomScanResult> scan() throws IllegalStateException {
        boolean scanSuccess = wifiManager.startScan();
        if (scanSuccess) {
            ArrayList<ScanResult> scanResults = (ArrayList) wifiManager.getScanResults();
            ArrayList<CustomScanResult> sanitizedResults = sanitizeResults(scanResults);
            return sanitizedResults;
        }
        else {
            Log.d("EXCEPTION: ", "SCAN FAILED");
            throw new IllegalStateException();
        }
    }

    public ArrayList<CustomScanResult> sanitizeResults(ArrayList<ScanResult> scanResults) {

        ArrayList<CustomScanResult> customScanResults = new ArrayList<CustomScanResult>();

        for (ScanResult sr : scanResults) {
            CustomScanResult csr = new CustomScanResult(sr.SSID, sr.level);
            if (!csr.SSID.equals("")) {
                customScanResults.add(csr);
            }
        }

        HashSet noDupes = new HashSet();
        noDupes.addAll(customScanResults);
        customScanResults.clear();

        customScanResults.addAll(noDupes);
        Collections.sort(customScanResults, sortByLevel);

        return customScanResults;
    }

    public void refresh() {
        scanResults = scan();

        arrayAdapter = new ArrayAdapter<CustomScanResult>(
                this,
                android.R.layout.simple_list_item_1,
                scanResults
        );
        scanListView.setAdapter(arrayAdapter);
    }

}
