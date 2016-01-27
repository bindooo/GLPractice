package com.faveoffate.glpractice;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.faveoffate.glpractice.framework.Game;
import com.faveoffate.glpractice.framework.Input;
import com.faveoffate.glpractice.framework.Screen;
import com.faveoffate.glpractice.thegame.GLGame;
import com.faveoffate.glpractice.thegame.GLGraphics;
import com.faveoffate.glpractice.thegame.Vector2;
import com.faveoffate.glpractice.thegame.Vertices;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends ListActivity {

    String tests[] = { "CannonTest", "CannonGravityTest", "CollisionTest", "Camera2DTest",
            "TextureAtlasTest", "SpriteBatcherTest", "AnimationTest" };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, tests));
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position,
                                   long id) {
        super.onListItemClick(list, view, position, id);
        String testName = tests[position];
        try {
            Class clazz = Class
                    .forName("com.faveoffate.glpractice." + testName);
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
