package com.example.dkt_group_beta.activities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dkt_group_beta.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameBoard extends AppCompatActivity {
    private static final int NUMBER_OF_FIELDS = 32;
    private List<ImageView> imageViews;

    HashMap<Integer, Float> XKoordinates = new HashMap();
    HashMap<Integer, Float> YKoordinates = new HashMap();

    ImageView character;
    ImageView character2;

    static int currentplace = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        character = findViewById(R.id.character);
        character2 = findViewById(R.id.character2);

        this.imageViews = new ArrayList<>();
        runOnUiThread(() -> {
            for (int i = 1; i <= NUMBER_OF_FIELDS; i++) {
                int resourceId = this.getResources()
                        .getIdentifier("field" + i, "id", this.getPackageName());
                imageViews.add(findViewById(resourceId));

                resourceId = this.getResources()
                        .getIdentifier("field" + i, "drawable", this.getPackageName());


                ImageView imageView = imageViews.get(i - 1);
                if (imageView != null && resourceId != 0) {
                    imageView.setImageBitmap(
                            decodeSampledBitmapFromResource(getResources(), resourceId, 200, 200));
                }
            }
        });


        getPositions(); //Positionen der Felder speichern
        setStartPosition(1, character);
        animation(character, 18);
      character.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hier kommt der Code, der nach der Verzögerung ausgeführt werden soll
                animation(character, 1);
            }
        }, 2000); // Verzögerung von 2000 Millisekunden (2 Sekunden)



        currentplace = 0;
        setStartPosition(1, character2);
        animation(character2, 17);


    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public void getPositions() {

        ConstraintLayout constraintLayout = findViewById(R.id.gameboard);
        constraintLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Hier wird der Code ausgeführt, wenn das Layout vollständig erstellt wurde


                for (int i = 1; i <= 30; i++) {

                    String fieldName = "field" + i; // Erstelle den dynamischen ID-Namen
                    int resourceId = getResources().getIdentifier(fieldName, "id", getPackageName());

                    ImageView imageView = findViewById(resourceId);

                    XKoordinates.put(i, imageView.getX());
                    YKoordinates.put(i, imageView.getY());
                }

                // Entfernen des Listeners, um Memory-Leaks zu vermeiden
                constraintLayout.removeOnLayoutChangeListener(this);
            }
        });


    }


    public void setStartPosition(int start, ImageView characterImageView) {

        ImageView field = findViewById(getResources().getIdentifier("field" + start, "id", getPackageName()));
        field.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float x = XKoordinates.get(start);
                characterImageView.setX(XKoordinates.get(start));
                characterImageView.setY(YKoordinates.get(start));
                currentplace = start;
                field.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Warten für 2 Sekunden, bevor die Animation startet
                /*character.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animation(character, 5); // Weiterführende Animation nach dem Warten
                    }
                }, 2000); // 2000 Millisekunden = 2 Sekunden*/
            }
        });


    }


    public void setPosition(ImageView characterView, int field) {

        int newPosition = field; //fährt nicht exakt - überarbeiten
        characterView.setX(XKoordinates.get(newPosition));
        characterView.setY(YKoordinates.get(newPosition));
    }

    public void animation(ImageView characterImageView, int repetition) {


        Log.d("AUFRUF", "animation aufgerufen" + String.valueOf(currentplace));


        float endX = characterImageView.getX();
        float endY = characterImageView.getY();

        if(currentplace < 11) { //movement for fields 1 to 11

            Log.d("NUMBER", String.valueOf(currentplace) + "smaller than 11");

            Log.d("NUMBER", "animation aufgerufen" + String.valueOf(currentplace));
            Animation animation = AnimationUtils.loadAnimation(characterImageView.getContext(), R.anim.animator_horizontal_bottom);
            animation.setRepeatCount(repetition);
            characterImageView.startAnimation(animation);


            animation.setAnimationListener(new Animation.AnimationListener() {

                float startX, startY;

                @Override
                public void onAnimationStart(Animation animation) {
                    // Animation startet
                    startX = characterImageView.getX();
                    startY = characterImageView.getY();

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Aktuelle Position nach der Animation erhalten

                    currentplace += repetition;

                    Log.d("PLACE", String.valueOf(currentplace));

                    setPosition(characterImageView, currentplace);


                }

                @Override
                public void onAnimationRepeat(Animation animation) {


                }
            });


        } else if (currentplace >= 11 && currentplace < 15) { //movement field 12 to 15

            Log.d("NUMBER", String.valueOf(currentplace) + "between 11 and <15");
            Animation animation_left = AnimationUtils.loadAnimation(characterImageView.getContext(), R.anim.animator_vertical_left);
            animation_left.setRepeatCount(repetition);
            characterImageView.startAnimation(animation_left);

            animation_left.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentplace += repetition;

                    setPosition(characterImageView, currentplace);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });





        } else if (currentplace >= 15  && currentplace < 27) {



            Log.d("NUMBER", String.valueOf(currentplace) + "between 15 and <27");

            Animation animation_top = AnimationUtils.loadAnimation(characterImageView.getContext(), R.anim.animator_horizontal_top);
            animation_top.setRepeatCount(repetition);
            characterImageView.startAnimation(animation_top);



            animation_top.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentplace += repetition;


                    setPosition(characterImageView, currentplace );
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });



        }else{

            Log.d("NUMBER", String.valueOf(currentplace) + ">= 27");

            Animation animation_right = AnimationUtils.loadAnimation(characterImageView.getContext(), R.anim.animator_vertical_right);
            animation_right.setRepeatCount(repetition);
            characterImageView.startAnimation(animation_right);

            animation_right.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentplace += repetition;
                    setPosition(characterImageView, currentplace);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });




        }



    }











}





