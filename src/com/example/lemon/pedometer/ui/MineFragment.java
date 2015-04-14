package com.example.lemon.pedometer.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import name.bagi.levente.pedometer.Pedometer;
import name.bagi.levente.pedometer.Pedometer.MyPagerAdapter;
import name.bagi.levente.pedometer.R;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.pedometer.db.DBManager;

public class MineFragment extends Fragment implements OnClickListener{

	private View view;
	
	private Uri imageUri;
	
	private ImageView headView;
	private TextView userName;
	private RadioButton male;
	private RadioButton female;
	private TextView weight;
	private TextView sensitivity; 
	private TextView stepLength;
	
	private Uri originalUri;
	private ToRoundBitmap toRoundBitmap;
	private Context context;
	private AlertDialog.Builder dialog;
	private NumberPicker numberPicker;
	
	private DBManager dbm;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.mine, null);
		init();
		context = MyPagerAdapter.getContext();
		dbm = DBManager.getInstance(context);
		return  view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}
	
	public void init(){
		headView = (ImageView) view.findViewById(R.id.head);
		userName = (TextView) view.findViewById(R.id.username);
		userName.setText(Pedometer.uName);
		male = (RadioButton) view.findViewById(R.id.male);
		female = (RadioButton) view.findViewById(R.id.female);
		weight = (TextView) view.findViewById(R.id.weight_);
		sensitivity = (TextView) view.findViewById(R.id.sensitivity_);
		stepLength = (TextView) view.findViewById(R.id.length_step_);
		
		headView.setOnClickListener(this);
		male.setOnClickListener(this);
		female.setOnClickListener(this);
		weight.setOnClickListener(this);
		sensitivity.setOnClickListener(this);
		stepLength.setOnClickListener(this);
	}

	public static final int TAKE_PHOTO = 0;
	public static final int TAKE_PHOTO1 = 1;
	public static final int CROP_PHOTO = 2;
	public static final int CROP_PHOTO1 = 3;
	
	@Override
	public void onClick(View view){
		switch(view.getId()){
		case R.id.head:
			dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("图片来源");
			dialog.setNegativeButton("取消", null);
			dialog.setItems(new String[] { "拍照", "相册" },
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							switch (arg1) {
							case 0:
								File outputImage = new File(Environment
										.getExternalStorageDirectory(),
										"picture.jpg");
								try {
									if (outputImage.exists()) {
										outputImage.delete();
									}
									outputImage.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
								imageUri = Uri.fromFile(outputImage);
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);

								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										imageUri);
								startActivityForResult(intent, TAKE_PHOTO1);

								break;
							case 1:

								Intent intent1 = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								// intent1.putExtra(MediaStore.EXTRA_OUTPUT,
								// imageUri);
								startActivityForResult(intent1, CROP_PHOTO1);

								break;
							}
							// 照片的原始资源地址

						}
					});
			dialog.show();
			break;
		case R.id.male:
			//male.setF
			break;
		case R.id.female:
			break;
		case R.id.weight_:
			dialog = new AlertDialog.Builder(getActivity());
			numberPicker = new NumberPicker(getActivity());
			numberPicker.setFocusable(true);
			numberPicker.setFocusableInTouchMode(true);
			numberPicker.setMaxValue(200);
			numberPicker.setValue(Integer.parseInt(weight.getText()
					.toString()));
			numberPicker.setMinValue(30);
			dialog.setView(numberPicker);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							weight.setText(numberPicker.getValue() + "");
							//user.setWeight(numberPicker.getValue());
						}
					});
			dialog.show();
			break;
		case R.id.sensitivity_:
			dialog = new AlertDialog.Builder(getActivity());
			numberPicker = new NumberPicker(getActivity());
			numberPicker.setFocusable(true);
			numberPicker.setFocusableInTouchMode(true);
			numberPicker.setMaxValue(10);
			numberPicker.setMinValue(1);
			dialog.setView(numberPicker);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//user.setSensitivity(numberPicker.getValue());
							//setSensitivity(numberPicker.getValue());

						}
					});
			dialog.show();
			break;
		case R.id.length_step_:
			dialog = new AlertDialog.Builder(getActivity());
			numberPicker = new NumberPicker(getActivity());
			numberPicker.setFocusable(true);
			numberPicker.setFocusableInTouchMode(true);
			numberPicker.setMaxValue(100);
			numberPicker.setValue(Integer.parseInt(stepLength.getText()
					.toString()));
			numberPicker.setMinValue(15);
			dialog.setView(numberPicker);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							stepLength.setText(numberPicker.getValue() + "");
							//user.setStep_length(numberPicker.getValue());
						}
					});
			dialog.show();
			break;
		}
	}
	
	private void setSensitivity(int value) {
		switch (value) {
		case 1:
			sensitivity.setText("一级");
			break;
		case 2:
			sensitivity.setText("二级");
			break;
		case 3:
			sensitivity.setText("三级");
			break;
		case 4:
			sensitivity.setText("四级");
			break;
		case 5:
			sensitivity.setText("五级");
			break;
		case 6:
			sensitivity.setText("六级");
			break;
		case 7:
			sensitivity.setText("七级");
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// this.pictureIntent = data;

		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == getActivity().RESULT_OK) {

				Bitmap bitmap;
				try {
					bitmap = BitmapFactory.decodeStream(getActivity()
							.getContentResolver().openInputStream(imageUri));
					headView.setImageBitmap(bitmap);
					//user.setPicture(PictureUtil.Bitmap2Byte(bitmap));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		case TAKE_PHOTO1:
			if (resultCode == getActivity().RESULT_OK) {

				Intent intent = new Intent("com.android.camera.action.CROP");

				intent.setDataAndType(imageUri, "image/*");
				intent.putExtra("scale", true);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, TAKE_PHOTO);

			}
			break;
		case CROP_PHOTO:
			if (resultCode == getActivity().RESULT_OK) {
				try {

					Bitmap bitmap = toRoundBitmap.toRoundBitmap(BitmapFactory
							.decodeStream(getActivity().getContentResolver()
									.openInputStream(originalUri)));
					headView.setImageBitmap(bitmap);
					//user.setPicture(PictureUtil.Bitmap2Byte(bitmap));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		case CROP_PHOTO1:
			if (resultCode == getActivity().RESULT_OK) {

				originalUri = data.getData();
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(originalUri, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra("crop", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
				startActivityForResult(intent, CROP_PHOTO);
			}
			break;
		default:
			break;
		}
	}		
	
}
