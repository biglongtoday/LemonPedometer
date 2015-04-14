/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.bagi.levente.pedometer;




import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.example.lemon.pedometer.ui.HistoryFragment;
import com.example.lemon.pedometer.ui.MineFragment;
import com.example.lemon.pedometer.ui.PKFragment;
import com.example.lemon.pedometer.ui.PagerSlidingTabStrip;
import com.example.lemon.pedometer.ui.TodayFragment;



public class Pedometer extends Activity {
	private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    
    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy

    public static String uName;
    
    private PagerSlidingTabStrip tabs;
    private ViewPager slidepager;
    private MyPagerAdapter adapter;
    private ActionBar mTabBar;
    
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
    private boolean isServiceBind;
    
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[ACTIVITY] onCreate");
        
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uName = intent.getStringExtra("uName");
     
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        //tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        slidepager = (ViewPager) findViewById(R.id.slidepager);
        adapter = new MyPagerAdapter(this,slidepager);
        mTabBar = getActionBar();
        
        adapter.addTab(mTabBar.newTab().setText("今天"),TodayFragment.class,null);
        adapter.addTab(mTabBar.newTab().setText("历史数据"),HistoryFragment.class,null);
        adapter.addTab(mTabBar.newTab().setText("我"),MineFragment.class,null);
        adapter.addTab(mTabBar.newTab().setText("PK"),PKFragment.class,null);
        slidepager.setAdapter(adapter);
       
       // tabs.setIndicatorHeight(5);
       // tabs.setViewPager(slidepager);
        
        int position = slidepager.getCurrentItem();
        if(position == 0){
			 if (getIntent() != null) {
		            final Bundle extras = getIntent().getExtras();
		            if (extras != null) {
		                final int theme = extras.getInt("theme");
		                if (theme != 0) {
		                    setTheme(theme);
		                    Log.v("theme","set theme");
		                    System.out.println("fuck");
		                }
		            }
		        }
		}
        mStepValue = 0;
        mPaceValue = 0;
        
     
        mUtils = Utils.getInstance();
    }
    
    
    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
        startStepService();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mPedometerSettings.clearServiceRunning();
    }
    
   
    
    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }

        super.onPause();
        savePaceSetting();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }
    
    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onDestroy();
    }

  
   
 
    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(Pedometer.this,
                    StepService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        isServiceBind = bindService(new Intent(Pedometer.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        if(isServiceBind)
        	unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(Pedometer.this,
                  StepService.class));
        }
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
    
    
 
    
	/* Creates the menu items */
    
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
            .setIcon(android.R.drawable.ic_media_pause)
            .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
            .setIcon(android.R.drawable.ic_media_play)
            .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setShortcut('8', 's')
        .setIntent(new Intent(this, Settings.class));
        menu.add(0, MENU_QUIT, 0, R.string.quit)
        .setIcon(android.R.drawable.ic_lock_power_off)
        .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                resetValues(true);
                return true;
            case MENU_QUIT:
                resetValues(false);
                unbindStepService();
                stopStepService();
                mQuitting = true;
                finish();
                return true;
        }
        return false;
    }
    

    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) { 
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
    public static class MyPagerAdapter extends FragmentPagerAdapter
    		implements TabListener,OnPageChangeListener{
    	
    	private static Context mContext;
    	private ActionBar mActionBar = null;
    	private ViewPager mViewPager = null;
    	private ArrayList<TabInfo> mTabList = new ArrayList<TabInfo>();
    	public MyPagerAdapter(Activity activity,ViewPager viewPager){
    		super(activity.getFragmentManager());
    		mContext = activity;
    		mActionBar = activity.getActionBar();
    		
    		mActionBar.setDisplayOptions(mActionBar.getDisplayOptions()
    				^ ActionBar.DISPLAY_SHOW_HOME
    				^ ActionBar.DISPLAY_SHOW_TITLE,ActionBar.DISPLAY_SHOW_HOME
    				| ActionBar.DISPLAY_SHOW_TITLE);
    		
    		mActionBar.setDisplayShowHomeEnabled(false);
    		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    		mViewPager = viewPager;
    		mViewPager.setAdapter(this);
    		mViewPager.setOnPageChangeListener(this);
    	}
    	
    	
    	@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public static Context getContext(){
			return mContext;
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mActionBar.setSelectedNavigationItem(position);
		}


		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Object tag = tab.getTag();
			for(int i = 0; i< mTabList.size();i++){
				if(mTabList.get(i)  == tag){
					mViewPager.setCurrentItem(i);
				}
			}
		}


		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
		public Fragment getItem(int position){
			TabInfo tab = mTabList.get(position);
			if(tab.fragment == null){
				tab.fragment = Fragment.instantiate(mContext, tab.clazz.getName(),tab.bundle);
			}
			return tab.fragment;
		}
		
		public int getCount(){
			return mTabList.size();
		}
		
		  /** Override FragmentPagerAdapter end **/  
		/*  
        private View getCustomView() {  
            LayoutInflater inflater = (LayoutInflater) mContext  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
           // return inflater.inflate(R.layout.title_panel_layout, null);
            return inflater.inflate(null,null);
        }  
		 */
        public void addTab(Tab tab, Class<?> clazz, Bundle bundle) {  
            TabInfo tabInfo = new TabInfo(clazz, bundle);  
            tab.setTag(tabInfo);  
            tab.setTabListener(this);  
            mTabList.add(tabInfo);  
            mActionBar.addTab(tab);  
            notifyDataSetChanged();  
        }  
  
        private static final class TabInfo {  
            private final Class<?> clazz;  
            private final Bundle bundle;  
            Fragment fragment;  
  
            TabInfo(Class<?> clazz, Bundle bundle) {  
                this.clazz = clazz;  
                this.bundle = bundle;  
            }  
        }  
		


    }
    

}