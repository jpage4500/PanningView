package com.maelstrom.panning.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.maelstrom.panning.lib.PanningImageView;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);

		final PanningImageView panningImageView = (PanningImageView) findViewById (R.id.panningView);

		findViewById(R.id.btn).setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {
				if(((Button) v).getText().toString ().equals ("Start")) {
					panningImageView.startPanning ();
					((Button) v).setText ("Pause");
				}
				else {
					panningImageView.stopPanning ();
					((Button) v).setText ("Start");
				}
			}
		});

	}
}
