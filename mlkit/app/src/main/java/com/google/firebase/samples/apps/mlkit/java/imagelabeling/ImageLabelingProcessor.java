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
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
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

/**
 * Custom Image Classifier Demo.
 */
public class ImageLabelingProcessor extends VisionProcessorBase<List<FirebaseVisionImageLabel>> {
    public Activity activity;
    private static final String TAG = "ImageLabelingProcessor";

    private final FirebaseVisionImageLabeler detector;

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
            @NonNull Activity activity) {
        TextView resultLabel = (TextView)activity.findViewById(R.id.resultLabel);
        TableLayout resultTable = (TableLayout)activity.findViewById(R.id.resultTable);
        resultTable.removeAllViews();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(originalCameraImage);

        }
        if(!labels.isEmpty())
        {
            FirebaseVisionImageLabel label = labels.get(0);
            List<Product> products = DatabaseLite.DBaseQc.QueryLabel(label.getText());
            //TextView tt = (TextView)row.getChildAt(1);
            resultLabel.setText(label.getText()); //products.get(0).Desc);

            for (Product p:
                 products) {
                TextView txt = new TextView(activity);
                ImageView img = new ImageView(activity);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                new DownloadImageTask(img).execute(p.Image);
//                img.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
//                String urldisplay = p.Image;
//                try {
//                    InputStream in = new java.net.URL(urldisplay).openStream();
//                    mIcon11 = BitmapFactory.decodeStream(in);
//                } catch (Exception e) {
//                    Log.e("Error", e.getMessage());
//                    e.printStackTrace();
//                }
//                img.setImageBitmap(mIcon11);
//                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                txt.setText(p.Desc);

                TableRow row = new TableRow(activity);
//                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(10,10);
                row.setLayoutParams(new TableRow.LayoutParams(100,100));
                row.addView(img);
                row.addView(txt);
                resultTable.addView(row);
//                resultTable.requestLayout();//


            }

        }
        LabelGraphic labelGraphic = new LabelGraphic(labels);
//        graphicOverlay.add(labelGraphic);
//        graphicOverlay.postInvalidate();
}

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Label detection failed." + e);
    }
}
