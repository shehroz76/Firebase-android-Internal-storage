package android.msk.com.internalstorage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

   private Button mUploadButton;
    private Button mSubmitButton;
    private CircleImageView mcircleImageViewRetrivePic;
    private CircleImageView mcircleImageViewProfilePic;
    private static final int Gallery_Request = 1 ;
    private Uri mImageUri1 = null;
    private ProgressDialog mProgressDialog;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Images");

        mProgressDialog = new ProgressDialog(this);

        mUploadButton = (Button) findViewById(R.id.upload_button);
        mSubmitButton =(Button) findViewById(R.id.submit_button);
        mcircleImageViewProfilePic = (CircleImageView) findViewById(R.id.profile_image_main);
        mcircleImageViewRetrivePic = (CircleImageView) findViewById(R.id.retrive_image);

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile_gallerY_intent = new Intent(Intent.ACTION_GET_CONTENT);
                profile_gallerY_intent.setType("image/*");
                startActivityForResult(profile_gallerY_intent , Gallery_Request);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitImage();
            }
        });


    }

    private void SubmitImage() {


        if(mImageUri1 != null){

            mProgressDialog.setMessage("uploading..");
            mProgressDialog.show();

            StorageReference filepath = mStorageReference.child("Images").child(mImageUri1.getLastPathSegment());
            filepath.putFile(mImageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String imageURL = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseReference.push().setValue(imageURL);

                    mProgressDialog.dismiss();

                    Toast.makeText(MainActivity.this , "Updloded" , Toast.LENGTH_SHORT).show();





                }
            });



        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Gallery_Request && resultCode == RESULT_OK) {

            Uri ImageUri1 = data.getData();

            CropImage.activity(ImageUri1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1 , 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri1 = result.getUri();

                mcircleImageViewProfilePic.setImageURI(mImageUri1);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
