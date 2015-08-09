package com.liyu.widget;

import com.liyu.superprogresswheel.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/** 
 * Created by liyu on 2015-8-6
 */
public class SuperProgressWheel extends View {
	
    /** 
     * ���ʶ�������� 
     */  
    private Paint paint;  
      
    /** 
     * Բ������ɫ 
     */  
    private int roundColor;  
      
    /** 
     * Բ�����ȵ���ɫ 
     */  
    private int roundProgressColor;  
      
    /** 
     * �м���Ȱٷֱȵ��ַ�������ɫ 
     */  
    private int textColor;  
      
    /** 
     * �м���Ȱٷֱȵ��ַ��������� 
     */  
    private float textSize;  
      
    /** 
     * Բ���Ŀ�� 
     */  
    private float roundWidth;  
      
    /** 
     * ������ 
     */  
    private int maxProgress;  
      
    /** 
     * ��ǰ���� 
     */  
    private int progress = 0; 
    
    /**
     * Բ���м�������ʾ���ͣ�0�ޣ�1�������֣�2ͼƬ
     */
    private int displayStyle;
    
    /**
     * Բ���м�������ʾͼƬ��ID
     */
    private int displayDrawableResId;
    
    /**
     * ������
     */
    private Context context;
    
    /**
     * ÿ����ת�ĽǶȣ���λ�Ƕȣ�Ҳ�������Ϊ��ת����
     */
    private int rotateDegree = -5;
    
    /**
     * ״̬�ص��ӿڼ�����
     */
    private onProgressListener mProgressListener;
    
    /**
     * bitmap:ԭʼͼƬ
     * resBitmap�������matrix��ת���Ժ��ͼƬ
     * matrix��ʵ��ͼƬ��ת
     */
    private Bitmap bitmap,resBitmap;
    private Matrix matrix = new Matrix();
    private boolean startDrawableAnim = false;
    
	public SuperProgressWheel(Context context) {
		this(context, null);
	}

	public SuperProgressWheel(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SuperProgressWheel(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		paint = new Paint();
		
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.SuperProgressWheel);
		
		roundColor = mTypedArray.getColor(R.styleable.SuperProgressWheel_roundColor, Color.BLUE);
		roundProgressColor = mTypedArray.getColor(R.styleable.SuperProgressWheel_roundProgressColor, Color.GRAY);
		textColor = mTypedArray.getColor(R.styleable.SuperProgressWheel_textColor, Color.BLACK);
		textSize = mTypedArray.getDimension(R.styleable.SuperProgressWheel_textSize, 45);
		roundWidth = mTypedArray.getDimension(R.styleable.SuperProgressWheel_roundWidth, 35);
		maxProgress = mTypedArray.getInteger(R.styleable.SuperProgressWheel_maxProgress, 100);
		displayDrawableResId = mTypedArray.getResourceId(R.styleable.SuperProgressWheel_displayDrawable, R.drawable.ic_launcher);
		displayStyle = mTypedArray.getInt(R.styleable.SuperProgressWheel_displayStyle, 0);
		
		mTypedArray.recycle();
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/** 
         * ����ʼ�Ĵ�Բ�� 
         */  
        int centre = getWidth()/2; //��ȡԲ�ĵ�x����  
        int radius = (int) (centre - roundWidth/2); //Բ���İ뾶  
        paint.setColor(roundColor); //����Բ������ɫ  
        paint.setStyle(Paint.Style.STROKE); //���û������� 
        paint.setStrokeWidth(roundWidth); //����Բ���Ŀ��  
        paint.setAntiAlias(true);  //�������   
        RectF oval = new RectF(centre - radius, centre - radius, centre  
                + radius, centre + radius);  //���ڶ����Բ������״�ʹ�С�Ľ���  
        
        float startAngle=-90,sweepAngle=(float)360/(float)(2*maxProgress);
        for(int i =0;i<100;i++){
            canvas.drawArc(oval,startAngle,sweepAngle, false, paint);
            startAngle = sweepAngle*2*(i+1)-90;
        }
        
        /**
         * ������Բ��
         */
        paint.setColor(roundProgressColor);
        float ProgressStartAngle = -90;
        for(int i =0;i<progress;i++){
            canvas.drawArc(oval,ProgressStartAngle,sweepAngle, false, paint);
            ProgressStartAngle = sweepAngle*2*(i+1)-90;
        }
        
        /**
         * �ж��м�������ʾ���ͣ�0�ޣ�1�������֣�2ͼƬ
         */
        switch(displayStyle){
            case 0:
                break;
            case 1:
        	    paint.setStrokeWidth(0);   
                paint.setColor(textColor);  
                paint.setTextSize(textSize);  
                paint.setTypeface(Typeface.DEFAULT_BOLD); //��������
                int percent = (int)(((float)progress / (float)maxProgress) * 100);  //�м�Ľ��Ȱٷֱȣ���ת����float�ڽ��г������㣬��Ȼ��Ϊ0  
                float textWidth = paint.measureText(percent + "%");   //���������ȣ�������Ҫ��������Ŀ��������Բ���м�  
                canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize/2, paint); //�������Ȱٷֱ�  
        	    break;
            case 2:
        	    bitmap = BitmapFactory.decodeResource(context.getResources(),displayDrawableResId);
                matrix.postRotate(rotateDegree,centre,centre);//��centreΪԲ�������ת
                resBitmap = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);//����Ϊ��bitmap
                canvas.drawBitmap(resBitmap, centre-(int)(resBitmap.getWidth()/2), centre-(int)(resBitmap.getHeight()/2), null);
                if(startDrawableAnim)
                	invalidate();//Ϊ��ʵ����ת�Ķ���Ч�����Ͳ�ͣ��ˢ��view
        	    break;
        }
       
	}
	
	public synchronized int getProgress() {
		return progress;
	}

	public synchronized void setProgress(int progress) {
		if(progress<maxProgress){
		    this.progress = progress;
		    postInvalidate();
		}
		else{
			this.progress = maxProgress;
			postInvalidate();
			mHandler.sendEmptyMessage(1);
		}
	}
	
	public int getRoundColor() {
		return roundColor;
	}

	public void setRoundColor(int roundColor) {
		this.roundColor = roundColor;
	}

	public int getRoundProgressColor() {
		return roundProgressColor;
	}

	public void setRoundProgressColor(int roundProgressColor) {
		this.roundProgressColor = roundProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public int getDisplayDrawableResId() {
		return displayDrawableResId;
	}

	public void setDisplayDrawableResId(int displayDrawableResId) {
		this.displayDrawableResId = displayDrawableResId;
	}

	public int getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(int displayStyle) {
		this.displayStyle = displayStyle;
	}

	public void setOnProgressListener(onProgressListener mProgressListener){
		this.mProgressListener = mProgressListener;
	}

    public interface onProgressListener{
    	void onCompleted(View v);
    }

	public boolean isStartDrawableAnim() {
		return startDrawableAnim;
	}

	public void setStartDrawableAnim(boolean startDrawableAnim) {
		this.startDrawableAnim = startDrawableAnim;
	};
	
	public Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
                case 1:  
                	mProgressListener.onCompleted(SuperProgressWheel.this);
                    break;    
            }  
        }  
    }; 

}
