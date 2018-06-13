
package net.codecanyon.trimax.wordsearch.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;


import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.activity.DifficultyActivity;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;

import android.animation.AnimatorSet;

public class WSLayout extends LinearLayout{


	public static boolean enableLetterAnim = true;
	public boolean enableSelectionStroke = true;
	public GridAppearAnimCallBack gridAppearAnimCallBack;
	private Integer pencilOffset;
	private Rect pencilOffsetRect;
	private Integer selectionCount;
	private Direction direction;
	private Paint defaultPaint, correctPaint, strokePaint;
	private List<View> previousWord;
	public Set<Word> foundWords;
	private Bitmap memory;
	private int colWidth;
	public int cols, rows;
	private int marchingAntIndex = 0;
	private float pencilRadius;
	private final float density = getResources().getDisplayMetrics().density;
	private final float delta = (int) (5.0 * density + 0.5);

	private OnWordHighlightedListener onWordHighlightedListener;
	private View[][] letters;
	private TouchListener touchListener;
	private boolean clearUnselectFlag;
	private int selectionWidth;

	private int lastColor_r;
	private int lastColor_g;
	private int lastColor_b;
	private double dist;

	private boolean roundRect = true;

	private int[][] lineColors = new int[][]{
			new int[]{0xc1,0x03,0x00},
			new int[]{0x76,0x68,0x00},
			new int[]{0x33,0x76,0x00},
			new int[]{0x00,0x76,0x57},
			new int[]{0x00,0x55,0x76},
			new int[]{0x00,0x04,0x94},
			new int[]{0x66,0x00,0x80},
			new int[]{0xff,0x00,0xbb},
			new int[]{0xff,0x92,0x97},
			new int[]{0xa3,0x76,0x3f},
			new int[]{0xbc,0xa4,0x00},
			new int[]{0x2c,0xcf,0xb7},
			new int[]{0xa9,0xa3,0x91}
	};

	private int colorIndex;


	public WSLayout(Context context) {
		super(context);
	}

	public WSLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		int selectedDifficulty = Settings.getIntValue(context, DifficultyActivity.SELECTED_DIFFICULTY, DifficultyActivity.DIFFICULTY_DEFAULT);

		switch (selectedDifficulty){

			case DifficultyActivity.DIFFICULTY_EXTREMELY_EASY:
				cols = DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_VERY_EASY:
				cols = DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_EASY:
				cols = DifficultyActivity.DIFFICULTY_EASY_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_MEDIUM:
				cols = DifficultyActivity.DIFFICULTY_MEDIUM_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_DIFFICULT:
				cols = DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_VERY_DIFFICULT:
				cols = DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT:
				cols = DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT:
				cols = DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE;
				break;
			case DifficultyActivity.DIFFICULTY_SUPREMEMELY_DIFFICULT:
				cols = DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE;
				break;
		}

		rows = cols;
		init();


		shuffleColors(lineColors);

	}



	void shuffleColors(int[][] ar){

		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--){
			int index = rnd.nextInt(i + 1);
			int[] a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}



	public void showGrid(boolean show){

		boolean firstBox = letters[0][0].findViewById(R.id.line1).getVisibility() == View.VISIBLE;

		if(show && firstBox)
			return;

		for(int i=0;i<letters.length;i++){
			for(int j=0;j<letters[i].length;j++){
                if(j > 0)
				    letters[i][j].findViewById(R.id.line1).setVisibility(show?View.VISIBLE:View.GONE);

                if(i < cols - 1)
				    letters[i][j].findViewById(R.id.line2).setVisibility(show?View.VISIBLE:View.GONE);
			}
		}
	}



	private void clearSelection() {
		boolean correct = false;
		if (direction != null && selectionCount != null) {

			int row = (int) Math.floor((double) pencilOffset / (double) cols);
			int col = pencilOffset % cols;
			correct = onWordHighlightedListener.wordHighlighted(findCoordinatesUnderPencil(direction, pencilOffset, selectionCount), direction, col, row);
		}

		if(correct){
			pencilOffset = null;
			selectionCount = null;
			direction = null;
			clearUnselectFlag = false;
		}else{
			clearUnselectFlag = true;
		}

		postInvalidate();
	}



	private View findChildByPosition(int index) {
		int row = (int) Math.floor((double) index / (double) cols);
		int col = index % cols;
		LinearLayout rowView = (LinearLayout) getChildAt(row);
		return rowView.getChildAt(col);
	}




	private List<Integer> findCoordinatesUnderPencil(Direction direction, int startPosition, int steps) {
		List<Integer> positions = new ArrayList<>();
		int curRow = startPosition / cols;
		int curCol = startPosition % cols;

		for (int i = 0; i < steps; i++) {
			positions.add((curRow * cols) + curCol);

			if (direction.isUp()) {
				curRow -= 1;
			} else if (direction.isDown()) {
				curRow += 1;
			}

			if (direction.isLeft()) {
				curCol -= 1;
			} else if (direction.isRight()) {
				curCol += 1;
			}

			if (curRow < 0 || curCol < 0 || curRow >= rows || curCol >= cols) {
				break;
			}

		}
		return positions;
	}





	public void clear() {
		if(letters.length > 0) {
			if (previousWord != null) {
				previousWord.clear();
			}

			foundWords.clear();
			pencilOffset = null;
			pencilOffsetRect = null;
			//pencilEndRect = null;
			direction = null;
			if(memory != null)memory.recycle();
			memory = null;
			selectionCount = null;
			postInvalidate();
		}
	}




	private void isNewSelection(float xPos, float yPos) {

		if (pencilOffset == null) {
			int position = point2XAxis((int) xPos, (int) yPos);
			if (position >= 0) {

				View item = findChildByPosition(position);
				pencilOffsetRect = getLetterBounds(item);
				pencilOffset = position;
			}
			postInvalidate();

		} else {
			float xDelta = xPos - pencilOffsetRect.centerX();
			float yDelta = (yPos - pencilOffsetRect.centerY()) * -1;

			direction = Direction.getDirection((float) Math.atan2(yDelta, xDelta));

			double distance = Math.hypot(xDelta, yDelta);
			dist = distance;
			distance += getExtraWidth(direction.isAngle());

			if (isInTouchMode() && distance < delta) {
				return;
			}

			Direction previousDirection = direction;
			Integer previousSteps = selectionCount;

			float stepSize = direction.isAngle() ? (float) Math.hypot(colWidth, colWidth) : colWidth;
			selectionCount = (int)Math.ceil(distance/stepSize);

			if (selectionCount == 0) {
				selectionCount = null;
			}

			if (direction != previousDirection || selectionCount != previousSteps) {
				List<View> selectedViews = getSelectedLetters();

				if (selectedViews == null) {
					return;
				}

				if(enableLetterAnim) {

				    View letter = selectedViews.get(selectedViews.size() - 1).findViewById(R.id.letter);

				    if(direction == Direction.EAST) {
                        ObjectAnimator.ofFloat(letter, "translationX", 5, 10, 5, 0, -5, -10, -5, 0).start();
					}else if(direction == Direction.WEST){
                        ObjectAnimator.ofFloat(letter, "translationX", -5, -10, -5, 0, 5, 10, 5, 0).start();
					}else if(direction == Direction.NORTH) {
						ObjectAnimator.ofFloat(letter, "translationY", -5, -10, -5, 0, 5, 10, 5, 0).start();
					}else if(direction == Direction.SOUTH){
						ObjectAnimator.ofFloat(letter, "translationY", 5, 10, 5, 0, -5, -10, -5, 0).start();
					}else if(direction == Direction.NORTH_WEST){
						AnimatorSet as = new AnimatorSet();
						as.playTogether(ObjectAnimator.ofFloat(letter, "translationX", -5, -10, -5, 0, 5, 10, 5, 0),
										ObjectAnimator.ofFloat(letter, "translationY", -5, -10, -5, 0, 5, 10, 5, 0));
						as.start();
					}else if(direction == Direction.SOUTH_EAST){
						AnimatorSet as = new AnimatorSet();
						as.playTogether(ObjectAnimator.ofFloat(letter, "translationX", 5, 10, 5, 0, -5, -10, -5, 0),
										ObjectAnimator.ofFloat(letter, "translationY", 5, 10, 5, 0, -5, -10, -5, 0));
						as.start();
					}else if(direction == Direction.SOUTH_WEST){
						AnimatorSet as = new AnimatorSet();
						as.playTogether(ObjectAnimator.ofFloat(letter, "translationY", 5, 10, 5, 0, -5, -10, -5, 0),
										ObjectAnimator.ofFloat(letter, "translationX", -5, -10, -5, 0, 5, 10, 5, 0)
										);
						as.start();
					}else if(direction == Direction.NORTH_EAST){
						AnimatorSet as = new AnimatorSet();
						as.playTogether(ObjectAnimator.ofFloat(letter, "translationY", -5, -10, -5, 0, 5, 10, 5, 0),
										ObjectAnimator.ofFloat(letter, "translationX", 5, 10, 5, 0, -5, -10, -5, 0)
						);
						as.start();
					}

                }

				if (previousWord != null && !previousWord.isEmpty()) {
					List<View> oldViews = new ArrayList<>(previousWord);
					oldViews.removeAll(selectedViews);
				}

				previousWord = selectedViews;

				postInvalidate();
			}
		}
	}



	public void populateBoard(char[][] board) {
		float size = 18;

		Resources r = getResources();

		switch (cols){

			case DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE:
				size = r.getDimension(R.dimen.letter_size_5);
				break;
			case DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE:
			case DifficultyActivity.DIFFICULTY_EASY_SIZE:
				size = r.getDimension(R.dimen.letter_size_a);
				break;
			case DifficultyActivity.DIFFICULTY_MEDIUM_SIZE:
			case DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE:
				size = r.getDimension(R.dimen.letter_size_b);
				break;
			case DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE:
			case DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE:
				size = r.getDimension(R.dimen.letter_size_c);
				break;
			case DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE:
				size = r.getDimension(R.dimen.letter_size_d);
				break;
			case DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE:
				size = r.getDimension(R.dimen.letter_size_e);
				break;
		}

		boolean gridOn = Settings.getBooleanValue(getContext(), Constants.GRID_ON, false);
		boolean night = Settings.getBooleanValue(getContext(), Constants.NIGHT_MODE_ON, false);

		int line;
		int textColor;

		if(night) {
			line = ContextCompat.getColor(getContext(), R.color.grid_color_n);
			textColor = ContextCompat.getColor(getContext(), R.color.text_color_n);
		}else{
			line = ContextCompat.getColor(getContext(), R.color.app_bg);
			textColor = ContextCompat.getColor(getContext(), R.color.grid_letter_color);
		}

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				View v = findChildByPosition(i * cols + j);
				AlphaAnimation a = new AlphaAnimation(1f, 0f);
				a.setFillAfter(true);
				v.startAnimation(a);
				if(gridOn){
                    if(j > 0) {
                        v.findViewById(R.id.line1).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.line1).setBackgroundColor(line);
                    }
                    if(i<cols - 1) {
                        v.findViewById(R.id.line2).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.line2).setBackgroundColor(line);
                    }
				}

				TextView tv = v.findViewById(R.id.letter);
				tv.setText("" + board[i][j]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
				tv.setTextColor(textColor);
			}
		}
	}



	public void appearAnim(){

	    final List<View> list = new ArrayList<>();
		int t = 0;

        for( int k = 0 ; k < letters.length * 2 ; k++ ) {
            for( int j = 0 ; j <= k ; j++ ) {
                int i = k - j;
                if( i < letters.length && j < letters.length ) {
                    View view = letters[i][j];
                    view.setTag(t);
                    list.add(view);
                }
            }
            t++;
        }

        new android.os.Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				long time = 100;

				switch (rows){
					case DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE:
						time = 110;
						break;
					case DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE:
						time = 100;
						break;
					case DifficultyActivity.DIFFICULTY_EASY_SIZE:
						time = 90;
						break;
					case DifficultyActivity.DIFFICULTY_MEDIUM_SIZE:
						time = 80;
						break;
					case DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE:
						time = 70;
						break;
					case DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE:
						time = 60;
						break;
					case DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE:
						time = 50;
						break;
					case DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE:
						time = 40;
						break;
					case DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE:
						time = 30;

				}

				for(int i=0;i<list.size();i++){
					View view = list.get(i);
					AlphaAnimation a = new AlphaAnimation(0f, 1f);
					a.setDuration(300);
					long offset = view.getTag() != null ? (Integer.valueOf(view.getTag().toString()) * time) : 0;
					a.setStartOffset(offset);
					view.setTag(null);
					a.setFillAfter(true);
					view.startAnimation(a);

					if(i==list.size() - 1){
						new android.os.Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								gridAppearAnimCallBack.animComplete();
							}
						}, offset + 300);

					}
				}
			}
		}, 100);

	}



	public interface GridAppearAnimCallBack{
		void animComplete();
	}



	public void update(char[][] board){

		boolean night = Settings.getBooleanValue(getContext(), Constants.NIGHT_MODE_ON, false);

		if(night){
			strokePaint.setColor(Color.GRAY);
		}else{
			strokePaint.setColor(Color.BLACK);
		}

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				View v = findChildByPosition(i * cols + j);

				TextView tv = v.findViewById(R.id.letter);

				if(night){
					tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color_n));
					int line = ContextCompat.getColor(getContext(), R.color.grid_color_n);
					v.findViewById(R.id.line1).setBackgroundColor(line);
					v.findViewById(R.id.line2).setBackgroundColor(line);
				}else{
					tv.setTextColor(ContextCompat.getColor(getContext(), R.color.grid_letter_color));
					int line = ContextCompat.getColor(getContext(), R.color.app_bg);
					v.findViewById(R.id.line1).setBackgroundColor(line);
					v.findViewById(R.id.line2).setBackgroundColor(line);
				}
			}
		}
		if(memory != null)memory.recycle();
		memory = null;
		setColor();
		invalidate();
	}


	public void setOnWordHighlightedListener(OnWordHighlightedListener onWordSelectedListener) {
		onWordHighlightedListener = onWordSelectedListener;
	}



	public void goal(Word word) {
		if (!foundWords.contains(word)) {
			colorIndex++;
			colorIndex %= lineColors.length;
			foundWords.add(word);
			if (memory == null) {
				memory = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			}
			Canvas foundCanvas = new Canvas(memory);
			correctPaint.setARGB(0xFF, lastColor_r, lastColor_g, lastColor_b);

			boolean night = Settings.getBooleanValue(getContext(), Constants.NIGHT_MODE_ON, false);

			if(night){
				word.setColor(Color.argb(0xFF, lineColors[colorIndex][0], lineColors[colorIndex][1], lineColors[colorIndex][2]));
			}else {
				word.setColor(correctPaint.getColor());
			}

			paintGenericSelection(word, foundCanvas, correctPaint);
			postInvalidate();
		}
	}



	@Override
	protected void onDraw(Canvas canvas) {

		if (memory == null) {
			drawAll();
		}

		canvas.drawBitmap(memory, 0f, 0f, correctPaint);

		if (direction != null && selectionCount != null && pencilOffset != null && !clearUnselectFlag) {
			paintCurrentSelection(canvas);
		}

		if(clearUnselectFlag) {
			unpaintCurrentSelection(canvas);
		}
	}



	private void drawAll(){
        if(memory != null)memory.recycle();
		memory = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas foundCanvas = new Canvas(memory);
		for (Word word : foundWords) {
			paintGenericSelection(word, foundCanvas, correctPaint);
		}
	}




	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getResources().getDisplayMetrics().widthPixels > getResources().getDisplayMetrics().heightPixels) {
			super.onMeasure(heightMeasureSpec, heightMeasureSpec);
			setMeasuredDimension(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		} else {
			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
			setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
		}
		colWidth = (int) Math.ceil((float) getMeasuredWidth() / (float) cols);
		pencilRadius = 22 - cols;
	}



	private void unpaintCurrentSelection(Canvas canvas) {

		if(direction == null){
			return;
		}

		float pad = colWidth / 3.2f;
		RectF superRect = new RectF(-pad, -pad, pad, pad);
		superRect.right += selectionWidth;

		selectionWidth -= (cols * 5);

		canvas.save();
		canvas.translate(pencilOffsetRect.centerX(), pencilOffsetRect.centerY());
		canvas.rotate(direction.getAngleDegree());
		if(roundRect)
		    canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, defaultPaint);
		else
		    canvas.drawRect(superRect, defaultPaint);

		if(enableSelectionStroke) {
		    if(roundRect)
                canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, strokePaint);
		    else
                canvas.drawRect(superRect, strokePaint);
        }

		canvas.restore();

		if(superRect.right <= 1){
			selectionWidth = 0;
			clearUnselectFlag = false;
			pencilOffset = null;
			selectionCount = null;
			direction = null;
			postInvalidate();
			return;
		}
		postInvalidateDelayed(1000 / 100);
	}



	private float getExtraWidth(boolean diagonal){
		return (colWidth * (diagonal ? 0.3f : 0.2f));
	}



	private void paintCurrentSelection(Canvas canvas) {

		float pad = colWidth / 3.2f;
		RectF superRect = new RectF(-pad, -pad, pad, pad);
		double distance = dist;
		superRect.right += distance;
		selectionWidth = new Double(distance).intValue();
		canvas.save();
		canvas.translate(pencilOffsetRect.centerX(), pencilOffsetRect.centerY());
		canvas.rotate(direction.getAngleDegree());

		if(roundRect)
		    canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, defaultPaint);
		else
		    canvas.drawRect(superRect, defaultPaint);

		if(enableSelectionStroke) {
			marchingAntIndex = marchingAntIndex > 10 ? 0 : marchingAntIndex;
			strokePaint.setPathEffect(new DashPathEffect(new float[]{6f, 5f}, marchingAntIndex));
			if(roundRect)
			    canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, strokePaint);
			else
			    canvas.drawRect(superRect, strokePaint);
			marchingAntIndex++;
		}

		canvas.restore();
		postInvalidateDelayed(100);
	}



	private void paintGenericSelection(Word word, Canvas canvas, Paint paint) {

		float angleStep = (float) Math.hypot(colWidth, colWidth);
		float pad = colWidth / 3.2f;
		float distance = (word.getDirection().isAngle() ? angleStep : colWidth) * (word.getText().length() - 1);

		RectF superRect = new RectF(-pad, -pad, pad, pad);
		superRect.right += distance;

		View v = findChildByPosition((word.getY() * cols) + word.getX());
		Rect viewRect = getLetterBounds(v);

		boolean night = Settings.getBooleanValue(getContext(), Constants.NIGHT_MODE_ON, false);

		if(night){
			paint.setARGB(0xA0, 0xFF, 0x00, 0x00);
		}else{
			int savedColor = word.getColor();
			if(savedColor != 0){
				paint.setARGB(0xA0, Color.red(savedColor), Color.green(savedColor), Color.blue(savedColor));
			}
		}

		canvas.save();
		canvas.translate(viewRect.centerX(), viewRect.centerY());
		canvas.rotate(word.getDirection().getAngleDegree());
		if(roundRect)
		    canvas.drawRoundRect(superRect, pencilRadius, pencilRadius, paint);
		else
		    canvas.drawRect(superRect, paint);
		canvas.restore();
	}



	public void setTouchListener(TouchListener listener){
		touchListener = listener;
	}



	public interface TouchListener{
		void touchStarted();
		void dragging(String partialWord);
		void touchEnded();
	}



	private List<View> getSelectedLetters() {

		if (pencilOffset == null || selectionCount == null || direction == null) {
			return null;
		}

		List<View> views = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (Integer position : findCoordinatesUnderPencil(direction, pencilOffset, selectionCount)) {
			View v = findChildByPosition(position);
			views.add(v);
			sb.append(((TextView)v.findViewById(R.id.letter)).getText().toString());

		}

		if(touchListener != null) {
			touchListener.dragging(sb.toString());
		}
		return views;
	}



	private Rect getLetterBounds(View v) {
		Rect viewRect = new Rect();
		v.getDrawingRect(viewRect);
		viewRect.offset(v.getLeft(), ((ViewGroup) v.getParent()).getTop());
		return viewRect;
	}



	private void init() {
		setWillNotDraw(false);
		setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		letters = new View[cols][cols];

		lp.weight = 1.0f;
		for (int i = 0; i < cols; i++) {
			LinearLayout row = new LinearLayout(getContext());
			row.setOrientation(LinearLayout.HORIZONTAL);
			for (int j = 0; j < cols; j++) {
				View view = LayoutInflater.from(getContext()).inflate(R.layout.wordsearch_grid_cell, null);
				view.setFocusable(true);
				row.addView(view, lp);
				letters[i][j] = view;
			}
			addView(row, lp);
		}

		setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, final MotionEvent e) {

				if (isEnabled()) {

					switch (e.getAction()) {
						case MotionEvent.ACTION_DOWN:
							setColor();

							if (touchListener != null) {
								touchListener.touchStarted();
								isNewSelection(e.getX(), e.getY());
							}
							break;
						case MotionEvent.ACTION_MOVE:
							isNewSelection(e.getX(), e.getY());

							break;
						case MotionEvent.ACTION_CANCEL:
						case MotionEvent.ACTION_UP:
							clearSelection();

							if (touchListener != null)
								touchListener.touchEnded();
							break;
					}

				}
				return true;

			}
		});


		foundWords = new HashSet<>();

		int color = 0x0099cc;

		defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		if(enableSelectionStroke) {
			strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			strokePaint.setStyle(Paint.Style.STROKE);
			boolean night = Settings.getBooleanValue(getContext(), Constants.NIGHT_MODE_ON, false);

			if(night){
				strokePaint.setColor(Color.GRAY);
			}else{
				strokePaint.setColor(Color.BLACK);
			}
			strokePaint.setPathEffect(new DashPathEffect(new float[]{6f, 5f}, 0));
		}

		correctPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		correctPaint.setARGB(0xA0, Color.red(color), Color.green(color), Color.blue(color));
		enableLetterAnim = Settings.getBooleanValue(getContext(), Constants.LETTER_ANIMATION, true);
	}



	private void setColor(){

		boolean night = Settings.getBooleanValue(getContext(), Constants.NIGHT_MODE_ON, false);
		int r,g,b;

		if(night){
			r = 0xFF;
			g = 0x00;
			b = 0x00;
		}else {
			r = lineColors[colorIndex][0];
			g = lineColors[colorIndex][1];
			b = lineColors[colorIndex][2];
		}

		defaultPaint.setARGB(150, r, g, b);
		lastColor_r = r;
		lastColor_g = g;
		lastColor_b = b;
	}
	


	private boolean containsPoint(float x, float y, View view) {
		Rect rect = new Rect();
		view.getDrawingRect(rect);
		rect.offset(view.getLeft(), ((ViewGroup) view.getParent()).getTop());
		return rect.contains((int) x, (int) y);
	}



	private int point2XAxis(float x, float y) {
		for (int i = 0; i < cols * cols; i++) {
			if (containsPoint(x, y, findChildByPosition(i))) {
				return i;
			}
		}
		return -1;
	}



	public View getLetterByXY(int x, int y){
		LinearLayout rowView = (LinearLayout) getChildAt(y);
		return rowView.getChildAt(x);
	}



	public interface OnWordHighlightedListener {
		boolean wordHighlighted(List<Integer> positions, Direction userDirection, int startX, int startY);
	}

}
