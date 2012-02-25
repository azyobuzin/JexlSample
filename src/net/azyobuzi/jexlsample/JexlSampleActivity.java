package net.azyobuzi.jexlsample;

import java.util.ArrayList;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class JexlSampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sourceGroup = (RadioGroup)findViewById(R.id.source_group);
        sourcePreview = (ListView)findViewById(R.id.source_preview);
        txtQuery = (EditText)findViewById(R.id.txt_query);

        sourceGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				sourcePreview.setAdapter(arg1 == R.id.source_numbers ? createNumbersAdapter() : createAlphabetsAdapter());
			}
        });
        sourceGroup.check(R.id.source_numbers);
    }

    private RadioGroup sourceGroup;
    private ListView sourcePreview;
    private EditText txtQuery;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(R.string.run).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	run();
    	return true;
    }

    private ListAdapter createNumbersAdapter() {
    	ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1);
    	for (int i = 1; i <= 20; i++)
    		adapter.add(i);
    	return adapter;
    }

    private ListAdapter createAlphabetsAdapter() {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
    	for (Character c : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
    		adapter.add(c.toString());
    	return adapter;
    }
    
    private final OnClickListener emptyListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			
		}
    };

    private void run() {
    	try {
	    	String query = txtQuery.getText().toString();
	    	
	    	if (query.isEmpty())
	    		return;
	    	
	    	JexlEngine engine = new JexlEngine();
	    	Expression expr = engine.createExpression(query);
	    	
	    	ListAdapter adapter = sourcePreview.getAdapter();
	    	
	    	ArrayList<Object> resultItems = new ArrayList<Object>();
	    	
	    	for (int i = 0; i < adapter.getCount(); i++) {
	    		Object item = adapter.getItem(i);
	    		
	    		JexlContext ctx = new MapContext();
	    		ctx.set("item", item);
	    		
	    		Object result = expr.evaluate(ctx);
	    		
	    		if (result instanceof Boolean) {
	    			if ((Boolean)result)
	    				resultItems.add(item);
	    		} else {
	    			new AlertDialog.Builder(this)
	    				.setTitle(R.string.result)
	    				.setIcon(android.R.drawable.ic_dialog_alert)
	    				.setMessage(R.string.result_is_not_boolean)
	    				.setPositiveButton(android.R.string.ok, emptyListener)
	    				.show();
	    			return;
	    		}
	    	}
	    	
	    	String resultString = "";
	    	for (Object item : resultItems) {
	    		resultString += item.toString() + "\n";
	    	}
	    	
	    	new AlertDialog.Builder(this)
	    		.setTitle(R.string.result)
	    		.setIcon(android.R.drawable.ic_dialog_info)
	    		.setMessage(resultString)
	    		.setPositiveButton(android.R.string.ok, emptyListener)
	    		.show();
    	} catch (Exception ex) {
    		new AlertDialog.Builder(this)
	    		.setTitle(R.string.result)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(ex.getClass().getName() + "\n\n" + ex.getMessage())
				.setPositiveButton(android.R.string.ok, emptyListener)
				.show();
    	}
    }
}