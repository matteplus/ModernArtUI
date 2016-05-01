package coursera.mobile.modernartui;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class ModernArtActivity extends Activity implements OnSeekBarChangeListener {
	
	private DialogFragment mDialog;
	private SeekBar mSeekBar;
	private View topLeft, topRight, bottomLeft, bottomRight;
	private static final String TAG = "ModernArtActivity";
	
	private List<Tile> tiles;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modernart_layout);
		mSeekBar = (SeekBar) findViewById(R.id.sBar);
		mSeekBar.setOnSeekBarChangeListener(this);
		
		topLeft = findViewById(R.id.top_left_tile);
		topRight = findViewById(R.id.top_right_tile);
		bottomLeft = findViewById(R.id.bottom_left_tile);
		bottomRight = findViewById(R.id.bottom_right_tile);
		
		tiles = Arrays.asList(
				new Tile(topLeft),
				new Tile(topRight),
				new Tile(bottomLeft),
				new Tile(bottomRight));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.modern_art, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.more_info:
			showMoreInfoDialog();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showMoreInfoDialog(){
		mDialog = InfoDialogFragment.newInstance();
		mDialog.show(getFragmentManager(), "Credits");
	}
	
	public static class InfoDialogFragment extends DialogFragment {
		
		public static InfoDialogFragment newInstance() {
			return new InfoDialogFragment();
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState){
			return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK)
			        .setMessage(R.string.credits)
			        .setCancelable(true)
			        .setPositiveButton(R.string.visit_moma, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent browserIntent = new Intent(Intent.ACTION_VIEW,
									Uri.parse(getString(R.string.moma_url)));
							try {
								startActivity(browserIntent);
							} catch (ActivityNotFoundException e) {
								// TODO: handle exception
								Toast.makeText(getActivity(), "No application can handle this request."
								        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
								    e.printStackTrace();
								
							}
						}
					})
					.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							((ModernArtActivity) getActivity()).mDialog.dismiss();
						}
					})
					.create();
		}
	}
	
	private class Tile {
		private View mView;
		private int color;
		private float hue, saturation;
		
		public Tile(View view){
			this.mView = view;
			this.color = getTileColorFromView(view);
			
			float[] hsv = new float[3];
			Color.colorToHSV(color, hsv);
			
			this.hue = hsv[0];
			this.saturation = hsv[1];
		}
		
		private int getTileColorFromView(View view) {
			
			int color = Color.TRANSPARENT;
			Drawable background = view.getBackground();
			if (background instanceof ColorDrawable){
				color = ((ColorDrawable) background).getColor();
			}
			
			return color;
		}
		
		public void setBackgroundColor(int color) {
			this.mView.setBackgroundColor(color);
		}
		
		public void setNewColor(int progress) {
			int newColor = this.color;
			float newHue = this.hue;
			float newSaturation = this.saturation;
			int alpha = Color.alpha(this.color);
			
			if (newHue < 30){
				newHue += ((float) progress/100 * 30);
			} else if (newHue > 230) {
				newHue -= ((float) progress/100 * 230);
			} else if (newHue < 180) {
				newHue += ((float) progress/100 * 50);
			} else {
				newHue -= ((float) progress/100 * 10);
			}
			
		    newSaturation = (float) 1.0 - ((float) progress/250);
			
			float[] hsv = { newHue, newSaturation, (float) 1.0 };
			
			newColor = Color.HSVToColor(alpha, hsv);
			setBackgroundColor(newColor);
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Called onProgressChanged: " + progress);
		
		for (Tile mTile: tiles) {
			
			mTile.setNewColor(progress);
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}
