package com.example.mysqllitedbcopyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String DB_NAME2 = "TravelExpertsSqlLite.db";
    ListView lvAgents;
    ArrayAdapter<Agent> adapter;
    AgentListDB agentListDb;
    Button btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load agent data into view
        loadAgentData();
    }



    //loads data into list view control
    public void loadAgentData() {
        // get db
        agentListDb = new AgentListDB(this);

        ArrayList<Agent> agents = agentListDb.getAgents();

        //display agents in list view control
        lvAgents = findViewById(R.id.lvAgents);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,agents);
        lvAgents.setAdapter(adapter);

    }

}
