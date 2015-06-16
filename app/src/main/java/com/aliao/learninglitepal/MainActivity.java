package com.aliao.learninglitepal;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aliao.learninglitepal.entity.SurveyInfo;
import com.aliao.litepal.tablemanager.Connector;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        TextView title = (TextView) findViewById(R.id.tvTitle);
        title.setText("test db create");
        handleDB();
    }

    private void handleDB() {
        save();

    }

    private void save() {
        //存储一条问卷信息
        SurveyInfo surveyInfo = new SurveyInfo();
        surveyInfo.setSurveyId("1000");
        surveyInfo.setTitle("租户满意度调查");
        surveyInfo.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
