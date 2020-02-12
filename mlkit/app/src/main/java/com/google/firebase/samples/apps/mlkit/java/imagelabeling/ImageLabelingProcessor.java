// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.firebase.samples.apps.mlkit.java.imagelabeling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.common.CameraImageGraphic;
import com.google.firebase.samples.apps.mlkit.common.DatabaseLite;
import com.google.firebase.samples.apps.mlkit.common.FrameMetadata;
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.java.Product;
import com.google.firebase.samples.apps.mlkit.java.VisionProcessorBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Custom Image Classifier Demo.
 */
public class ImageLabelingProcessor extends VisionProcessorBase<List<FirebaseVisionImageLabel>> {
    public Activity activity;
    private static final String TAG = "ImageLabelingProcessor";

    private final FirebaseVisionImageLabeler detector;

    public int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public ImageLabelingProcessor() {
        detector = FirebaseVision.getInstance().getOnDeviceImageLabeler();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();

                mIcon11 = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), 300, 300, false) ;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionImageLabel>> detectInImage(FirebaseVisionImage image) {
        return detector.processImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionImageLabel> labels,
            @NonNull FrameMetadata frameMetadata,
            @NonNull final Activity activity) {
        TextView resultLabel = (TextView)activity.findViewById(R.id.resultLabel);

        LinearLayout lyl = activity.findViewById(R.id.resultLayout);
        lyl.removeAllViews();

//        TableLayout resultTable = (TableLayout)activity.findViewById(R.id.resultTable);
//        resultTable.removeAllViews();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(originalCameraImage);

        }
        if(!labels.isEmpty())
        {
            String label = new String();
            String[] labelsArray = new String[labels.size()];
            Integer a=0;
            for (FirebaseVisionImageLabel lab: labels)
            {
                labelsArray[a]=lab.getText();
                a++;
            }
            //labels.toArray(labelsArray);
            List<Product> products = DatabaseLite.DBaseQc.QueryLabel(labelsArray);
            for (FirebaseVisionImageLabel label1:
                    labels) {
                label += label1.getText() + "\n";
            }
            resultLabel.setText(label);

            for (final Product p:
                 products) {

                Bitmap image = Bitmap.createBitmap(300 , 300, Bitmap.Config.ARGB_8888);
                image.eraseColor(Color.BLACK);

                ImageView img = new ImageView(activity);
                img.setImageBitmap(image);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                new DownloadImageTask(img).execute(p.Image);


//                Button btn = new Button(activity);
//                btn.setText(p.Desc);
//                btn.setOnClickListener(new View.OnClickListener(){
//
//
//                    @Override
//                    public void onClick(View v) {
//                        Uri uriUrl = Uri.parse(p.Link);
//                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//                        activity.startActivity(launchBrowser);
//
//                    }
//                });
////
                TextView txt = new TextView(activity);
                txt.setText(p.Desc);
                txt.setClickable(true);
                txt.setMovementMethod(LinkMovementMethod.getInstance());
               LinearLayout.LayoutParams paramLy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

                txt.setGravity(Gravity.CENTER);
                txt.setLayoutParams(paramLy);

txt.setOnClickListener(new View.OnClickListener(){


    @Override
    public void onClick(View v) {
        Uri uriUrl = Uri.parse(p.Link);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        activity.startActivity(launchBrowser);

    }
});



                LinearLayout lylrow = new LinearLayout(activity);
                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params4.setMargins(50,50,50,0);
                lylrow.setLayoutParams(params4);
//                txt.getLayoutParams().width = (int) (getScreenWidth(activity) * 0.8);

//                TableRow row = new TableRow(activity);
//                TableLayout.LayoutParams param3 = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,300);
//                param3.setMargins(50,50,50,0);
////                param.weight1.0f;
//                row.setLayoutParams(param3);
//                row.addView(img);
//                row.addView(txt);

               // resultTable.addView(row);
                lylrow.addView(img);
                lylrow.addView(txt);
                lyl.addView(lylrow);
            }

        }
}

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Label detection failed." + e);
    }
}
