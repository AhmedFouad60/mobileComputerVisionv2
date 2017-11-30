package com.example.foush.foushvision;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity   {
     ProgressDialog progress ;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.pic1)
    Button pic1;
    @BindView(R.id.pic2)
    Button pic2;
    @BindView(R.id.pic3)
    Button pic3;

    @BindView(R.id.uploadButton)
    Button uploadButton;
    private static final int flagPic1 = 1;
    private static final int flagPic2 = 2;
    private static final int flagPic3 = 3;
    private static final int REQUEST_STORAGE_PERMISSION = 90;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderv3";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Uri photoURI;
    private String mTempPhotoPath;

    private Bitmap mResultsBitmap;
    File photoFile = null;
    private Bitmap theBitmap = null;
    String file_path;

    //save the uri of the 3 photos taken
    private static List<String> photosUriList = new ArrayList<String>();
    private Bitmap tempBitmap;
    private static List<File> filesList = new ArrayList<File>();
    private static String FoushTest = "";
    private static int count = 0;
    final static String zipfile=BitmapUtils.storageDir + ".zip";
    private static String contnet_type;
    private static File f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
        new ProgressDialog(MainActivity.this);
    }

    @OnClick({R.id.pic1, R.id.pic2, R.id.pic3, R.id.uploadButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pic1:
                if (checkCameraPermission() == true) {
                    // Launch the camera if the permission exists

                    launchCamera(flagPic1);

                }

                break;
            case R.id.pic2:
                if (checkCameraPermission() == true) {
                    // Launch the camera if the permission exists

                    launchCamera(flagPic2);

                }
                break;
            case R.id.pic3:
                if (checkCameraPermission() == true) {
                    // Launch the camera if the permission exists

                    launchCamera(flagPic3);

                }
                break;
            case R.id.uploadButton:
                break;
        }
    }

    public boolean checkCameraPermission() {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            return false;
        } else {
            return true;
        }

    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private void launchCamera(int flag) {


        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go

            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();
                Log.d(TAG, "launchCamera: photo uri uri uri uri uri uri is  " + mTempPhotoPath);


                // Get the content URI for the image file
                photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, flag);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == flagPic1) {
            //using the first button (1) to pick first photo

            if (resultCode == RESULT_OK) {
                /** process the image and set it to the imageView*/

                processAndSetImage(flagPic1);


            } else {
            }


        } else if (requestCode == flagPic2) {
            //using the first button (2) to pick first photo
            if (resultCode == RESULT_OK) {
                /** process the image and set it to the imageView*/
                processAndSetImage(flagPic2);

            } else {
            }


        } else if (requestCode == flagPic3) {
            //using the first button (2) to pick first photo

            if (resultCode == RESULT_OK) {
                /** process the image and set it to the imageView*/
                processAndSetImage(flagPic3);

            } else {
            }


        } else {/**do nothing :) */}


    }

    /**
     * Method for processing the captured image and setting it to the ImageView.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processAndSetImage(int flag) {

        //Load the Image with it's uri
        Bitmap myBitmap = GlideBitmapFactory.decodeFile(mTempPhotoPath);
        //imageView.setImageBitmap(myBitmap);

        //create a paint object for drawing with
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);



        //create a canvas object for drawing on
        tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //create the face detector
        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
            return;

        }
        //Detect faces
        //Draw Rectangles on the faces


        //Detect the Faces
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        if (faces.size() == 0) {
            Toast.makeText(this, "No faces detected Please take the picture again", Toast.LENGTH_LONG).show();
            Noface(flag);

        } else {
            /**if there is a face in the image*/

            //Draw Rectangles on the Faces
            for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);






            }
            imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

            /**save the image and get the images path*/

            // Delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
            // Save the image
            String imagPath = BitmapUtils.saveImage(this, tempBitmap);
            Log.d(TAG, "processAndSetImage: image path is " + imagPath);


            //the life is successful for this button make it's visibility gone
            GonButtons(flag);
           // uploadZipToServer(imagPath);



           /* if (count == 3) {
                //zip all the photos in the file
                ZipUtil.pack(new File(String.valueOf(BitmapUtils.storageDir)), new File(String.valueOf(BitmapUtils.storageDir)+".zip"));
                ZipUtil.unexplode(new File( BitmapUtils.storageDir + ".zip"));
                // write function to send the ziped file to the server [moustafa]
                Log.d(TAG, "uploading zip file: welcome to the server  zip path is "+BitmapUtils.storageDir + ".zip");


            }*/




        }
    }

    private void GonButtons(int flag) {
        switch (flag) {

            case flagPic1:
                pic1.setVisibility(View.GONE);
                ++count;
                if (pic2.getVisibility() == View.GONE && pic3.getVisibility() == View.GONE) {
                    uploadButton.setVisibility(View.VISIBLE);

                }

                break;
            case flagPic2:
                pic2.setVisibility(View.GONE);
                ++count;

                if (pic3.getVisibility() == View.GONE && pic1.getVisibility() == View.GONE) {
                    uploadButton.setVisibility(View.VISIBLE);

                }

                break;
            case flagPic3:
                pic3.setVisibility(View.GONE);
                ++count;

                if (pic2.getVisibility() == View.GONE && pic1.getVisibility() == View.GONE) {
                    uploadButton.setVisibility(View.VISIBLE);



                }

                break;

        }


    }

    private void Noface(int flag) {

        switch (flag) {

            case flagPic1:
                launchCamera(flagPic1);
                break;
            case flagPic2:
                launchCamera(flagPic2);

                break;
            case flagPic3:
                launchCamera(flagPic3);

                break;

        }

    }

    /**the progress bar for uploading*/


    private void uploadZipToServer(final String zipfile) {
        final ProgressDialog progress;
        progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("uploading");
        progress.setMessage("please wait ...");
//        progress.setMax(100);
        progress.setCancelable(false);
        progress.show();


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
//                 progress.show();
                File f = new File(zipfile);
                String contnet_type = getMimeType(f.getPath());
                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(contnet_type), f);


                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", contnet_type)
                        .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("http://ahmedfouad.esy.es/computerVision/upload_file.php")
                        .post(request_body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();


                    if (!response.isSuccessful()) {
                        throw new IOException("Error : " + response);
                    }

                    progress.dismiss();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        t.start();
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }




}










