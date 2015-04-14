package com.example.lemon.pedometer.ui;


import java.util.Calendar;
import java.util.List;

import name.bagi.levente.pedometer.Pedometer;
import name.bagi.levente.pedometer.Pedometer.MyPagerAdapter;
import name.bagi.levente.pedometer.PedometerSettings;
import name.bagi.levente.pedometer.R;
import name.bagi.levente.pedometer.StepService;
import name.bagi.levente.pedometer.Utils;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.example.pedometer.db.DBManager;
import com.example.pedometer.fragment.onekeyshare.OnekeyShare;
import com.example.pedometer.model.PedometerData;
import com.example.pedometer.model.User;
import com.example.pedometer.model.UserPedometer;
//import com.example.pedometer.db.DBManager;
//import com.example.pedometer.model.UserPedometer;

public class TodayFragment extends Fragment{

	private View view;

	private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    
    TextView mDesiredPaceView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;

    private boolean mIsMetric;
  // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
    
    private boolean mIsRunning;
    private boolean isReStart;
    

	private HoloCircularProgressBar pacesProgressbar;
	private ObjectAnimator mProgressBarAnimator;
	private static Context context;
	//private DBManager dbm;

	
	
	private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    private FButton startButton;
    private FButton pauseButton;
    private FButton stopButton;
    private ImageView shareView;
    private DBManager dbm;
    
    private boolean isServiceBind = false;
    
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.today, container,false);	
		return view; 		
	}
	
	private void init(){
		pacesProgressbar = (HoloCircularProgressBar) view.findViewById(R.id.display_paces);
		//起点归位
        animate(pacesProgressbar, null, 0f, 0);
        pacesProgressbar.setMarkerProgress(0f);
        //pacesProgressbar.setProgress(1f);
        mStepValueView     = (TextView) view.findViewById(R.id.step_value);
        mPaceValueView     = (TextView) view.findViewById(R.id.pace_value);
        mDistanceValueView = (TextView) view.findViewById(R.id.distance_value);
        mSpeedValueView    = (TextView) view.findViewById(R.id.speed_value);
        mCaloriesValueView = (TextView) view.findViewById(R.id.calories_value);
        
        startButton = (FButton) view.findViewById(R.id.start);
        startButton.setOnClickListener(new MyOnClickListener(START));
        
        pauseButton = (FButton) view.findViewById(R.id.pause);
        pauseButton.setOnClickListener(new MyOnClickListener(PAUSE));
        stopButton = (FButton) view.findViewById(R.id.stop);
        stopButton.setOnClickListener(new MyOnClickListener(STOP));
        
        
        
		//数据最后保存到数据库，一时的数据在SharedPreferences中
		mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        mPedometerSettings = new PedometerSettings(mSettings);
        
        shareView = (ImageView) view.findViewById(R.id.shareView);
        ShareSDK.initSDK(context);
        shareView.setOnClickListener(new MyOnClickListener(SHARE));
        
        mPedometerSettings.clearServiceRunning();
        mIsMetric = mPedometerSettings.isMetric();
       
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = MyPagerAdapter.getContext();
		//dbm = new DBManager(context);
		dbm = DBManager.getInstance(context);
		init();
		
	}
	
	//以下是响应界面的按钮事件
	
	private final static int START = 0;
	private final static int PAUSE = 1;
	private final static int STOP = 2;
	private final static int SHARE = 3;
	
	public class MyOnClickListener implements View.OnClickListener{
		
		private int tag;
		
		public MyOnClickListener(int tag){
			this.tag = tag;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(tag){
			case START:
				startButton.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
				stopButton.setVisibility(View.VISIBLE);
				
				  // Start the service if this is considered to be an application start (last onPause was long ago)
		        if (!mIsRunning && mPedometerSettings.isNewStart()) {
		            startStepService();
		            bindStepService();
		        }
		        else if (mIsRunning) {
		            bindStepService();
		        }
				break;
			case PAUSE:
				if(!isReStart){
					isReStart = true;
					 unbindStepService();
		             stopStepService();
		             Toast.makeText(context, "Pedometer paused", 2000);
		             pauseButton.setText("继续");
				}else{
					isReStart = false;
					pauseButton.setText("暂停");
					startStepService();
		            bindStepService();
		            Toast.makeText(context, "Pedometer resume", 2000);
				}
				break;
			case STOP:
				startButton.setVisibility(View.VISIBLE);
				pauseButton.setVisibility(View.GONE);
				stopButton.setVisibility(View.GONE);
				unbindStepService();
				stopStepService();
				
		 
				//write the data to the db
				Calendar c = Calendar.getInstance();
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				String walkDate = month + "月" + day + "日";
				
				int uid = 0;
				List<User> users = dbm.queryUser();
				for(User u:users){
					if(u.getName().equals(Pedometer.uName))
						uid = u.getUID();
				}
				UserPedometer up = new UserPedometer();
				up.setUID(uid);
				up.setPaces(mStepValue);
				up.setKilometers(mDistanceValue);
				up.setCalories(mCaloriesValue);
				up.setDate(walkDate);
				dbm.addUserPedometer(up);
				
				break;
			case SHARE:
				OnekeyShare oks = new OnekeyShare();
				oks.setNotification(R.drawable.icon, "ShareSDK notification content");
				oks.setText("您今天已经走了" + mStepValue + "步");
				oks.setSilent(false);
				oks.disableSSOWhenAuthorize();
				//Toast.makeText(context, "share clicked", 2000).show();
				oks.show(getActivity()); 
				break;
			default:
					break;
			}
			
		}
		
	}
	
	 private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener,
	            final float progress, final int duration) {

	        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
	        mProgressBarAnimator.setDuration(duration);

	        mProgressBarAnimator.addListener(new AnimatorListener() {

	            @Override
	            public void onAnimationCancel(final Animator animation) {
	            }

	            @Override
	            public void onAnimationEnd(final Animator animation) {
	                progressBar.setProgress(progress);
	            }

	            @Override
	            public void onAnimationRepeat(final Animator animation) {
	            }

	            @Override
	            public void onAnimationStart(final Animator animation) {
	            }
	        });
	        if (listener != null) {
	            mProgressBarAnimator.addListener(listener);
	        }
	        mProgressBarAnimator.reverse();
	        mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

	            @Override
	            public void onAnimationUpdate(final ValueAnimator animation) {
	                progressBar.setProgress((Float) animation.getAnimatedValue());
	            }
	        });
	        progressBar.setMarkerProgress(progress);
	        mProgressBarAnimator.start();
	    }

	 private void animate(final HoloCircularProgressBar progressBar,
	            final AnimatorListener listener) {
	        //final float progress = (float) (Math.random() * 2);
	    	final float progress = 1;  //progressbar旋转的圈数
	        int duration = 10000;
	        animate(progressBar, listener, progress, duration);
	    }
	
	
	 
	 private void startStepService() {
	        if (! mIsRunning) {
	        	//Log.i(TAG, "[SERVICE] Start");
	            mIsRunning = true;
	            context.startService(new Intent(context,
	                    StepService.class));
	        }
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
		    //the progress bar rotate degree
		    private float degree = 0;
		    private float degreePerPace = 1f;    //default 5000 steps    
		    
		    private Handler mHandler = new Handler() {
		        @Override public void handleMessage(Message msg) {
		            switch (msg.what) {
		                case STEPS_MSG:
		                    mStepValue = (int)msg.arg1;
		                    mStepValueView.setText("" + mStepValue);
	                        pacesProgressbar.setProgress((float)(mStepValue * 0.0006));
	                        pacesProgressbar.invalidate();
		                   // animate(pacesProgressbar, null, 0.2f, 1000);
		                   // pacesProgressbar.setMarkerProgress(2f);
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
		    
	    private void bindStepService() {
	        //Log.i(TAG, "[SERVICE] Bind");
	       isServiceBind = context.bindService(new Intent(context, 
	                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	    }

	    @Override
		public void onResume() {
			// TODO Auto-generated method stub
	    	//bindStepService();
			super.onResume();
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			if(isServiceBind){
				unbindStepService();
				isServiceBind = false;
			}
			super.onPause();
		}

		private void unbindStepService() {
	        //Log.i(TAG, "[SERVICE] Unbind");
			if(isServiceBind)
				context.unbindService(mConnection);
	    }
	    
	    
	    @Override
		public void onDestroy() {
			// TODO Auto-generated method stub
	    	unbindStepService();
	    	dbm.closeDB();
			super.onDestroy();
			
			
		}

		private void stopStepService() {
	        //Log.i(TAG, "[SERVICE] Stop");
	        if (mService != null) {
	            //Log.i(TAG, "[SERVICE] stopService");
	        	if(isServiceBind)
	            context.stopService(new Intent(context,
	                  StepService.class));
	        }
	        mIsRunning = false;
	    }
	



}
