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



    private void testDBMethods(AgentListDB db){

        StringBuilder sb = new StringBuilder();

        // insert a task
        Agent agent = new Agent(1, "Sam", "", "Gamgee", "444-444-4444","C@D.com","Senior",1);
        long insertId = db.insertAgent(agent);
        if (insertId > 0) {
            sb.append("Row inserted! Insert Id: " + insertId + "\n");
        }

        // insert a second task
        Agent agent2 = new Agent(2, "Fred", "R", "Flintstone", "555-555-5555","A@B.com","Junior",1);;
        long insertId2 = db.insertAgent(agent2);
        if (insertId2 > 0) {
            sb.append("Row inserted! Insert Id: " + insertId2 + "\n");
        }

        // insert a third task
        Agent agent3 = new Agent(3, "Joe", "R", "Flintstone", "555-555-5555","A@B.com","Junior",1);;
        long insertId3 = db.insertAgent(agent3);
        if (insertId2 > 0) {
            sb.append("Row inserted! Insert Id: " + insertId2 + "\n");
        }
        // insert a fourth task
        Agent agent4 = new Agent(4, "nancy", "R", "Flintstone", "555-555-5555","A@B.com","Junior",1);;
        long insertId4 = db.insertAgent(agent4);
        if (insertId2 > 0) {
            sb.append("Row inserted! Insert Id: " + insertId2 + "\n");
        }

        // update a task
        agent.setAgentId((int) insertId);
        agent.setAgtFirstName("Update test***************");
        int updateCount = db.updateAgent(agent);
        if (updateCount == 1) {
            sb.append("Task updated! Update count: " + updateCount + "\n");
        }

        // delete a task
        int deleteCount = db.deleteAgent(3);
        if (deleteCount == 1) {
            sb.append("Task deleted! Delete count: " + deleteCount + "\n\n");
        }

        // delete old tasks (this may vary from system to system)
        db.deleteAgent(4);
    }
}
