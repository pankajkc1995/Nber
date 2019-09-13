package aaronsoftech.in.nber.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import aaronsoftech.in.nber.App_Conteroller;
import aaronsoftech.in.nber.POJO.Response_Driver_vehicle;
import aaronsoftech.in.nber.POJO.Response_Vehicle_type;
import aaronsoftech.in.nber.POJO.Response_vehicle;
import aaronsoftech.in.nber.R;
import aaronsoftech.in.nber.Service.APIClient;
import aaronsoftech.in.nber.Utils.App_Utils;
import aaronsoftech.in.nber.Utils.SP_Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static aaronsoftech.in.nber.Utils.App_Utils.isNetworkAvailable;


public class Vehicle_reg extends AppCompatActivity {
    TextView btn_ok;
    ArrayList vehicle_list=new ArrayList();
    public static String PATH_PERMIT="",PATH_VEHICLE="",PATH_RC="",PATH_INSURENSE="",PATH_OTHER_DOC="";
    CircleImageView btn_vehicle;
    ImageView btn_permit,btn_vehicle_rc,btn_insurence_id,btn_other_doc;
    EditText ed_no;
    ProgressDialog progressDialog;
    String TAG="Vehicle_reg";
    Spinner vehicle_type;
    int get_vehicle_type=0;
    List<Response_Vehicle_type.Data_List> get_vehicle_type_list=new ArrayList<>();
    String get_Vehicle_id="";
    String refreshedToken;
    String Driver_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_reg);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG,"Token ID : onstart "+refreshedToken);
        Init();

        btn_permit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Vehicle_reg.this,Driver_doc_Image.class).
                        putExtra("type","11"));           }
        });

        btn_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Vehicle_reg.this,Driver_doc_Image.class).
                        putExtra("type","12"));            }
        });

        btn_vehicle_rc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Vehicle_reg.this,Driver_doc_Image.class).
                        putExtra("type","13"));            }
        });

        btn_insurence_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Vehicle_reg.this,Driver_doc_Image.class).
                        putExtra("type","14"));            }
        });

        btn_other_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Vehicle_reg.this,Driver_doc_Image.class).
                        putExtra("type","15"));            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PATH_PERMIT=="")
                {
                    Toast.makeText(Vehicle_reg.this, "Upload Permit", Toast.LENGTH_SHORT).show();
                }else if (PATH_VEHICLE=="")
                {
                    Toast.makeText(Vehicle_reg.this, "Upload Vehicle Image", Toast.LENGTH_SHORT).show();
                }else if (PATH_RC=="")
                {
                    Toast.makeText(Vehicle_reg.this, "Upload RC", Toast.LENGTH_SHORT).show();
                }else if (PATH_INSURENSE=="")
                {
                    Toast.makeText(Vehicle_reg.this, "Upload Insurense", Toast.LENGTH_SHORT).show();
                }else if (PATH_OTHER_DOC=="")
                {
                    Toast.makeText(Vehicle_reg.this, "Upload Other Doc", Toast.LENGTH_SHORT).show();
                }else if (ed_no.getText().toString().isEmpty())
                {
                    ed_no.setError("Enter vehicle no.");
                    ed_no.requestFocus();
                }else{
                    progressDialog=new ProgressDialog(Vehicle_reg.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Call_Api();
                }

            }
        });
        Call_Vihicle_Api();
        vehicle_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    get_Vehicle_id=get_vehicle_type_list.get(i).getId();
                }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        get_Vihicle_Api();
    }


    private void get_Vihicle_Api() {
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        try{
            HashMap map= new HashMap<>();
            map.put("driver_id",App_Conteroller.sharedpreferences.getString(SP_Utils.LOGIN_DRIVER_ID,""));
            if (isNetworkAvailable(Vehicle_reg.this))
            {
                Call<Response_Driver_vehicle> call= APIClient.getWebServiceMethod().get_Driver_Vehicle(map);
                call.enqueue(new Callback<Response_Driver_vehicle>() {
                    @Override
                    public void onResponse(Call<Response_Driver_vehicle> call, Response<Response_Driver_vehicle> response) {
                        progressDialog.dismiss();
                        try{
                            String status=response.body().getApi_status();
                            String msg=response.body().getApi_message();
                            if (status.equalsIgnoreCase("1") && msg.equalsIgnoreCase("success") )
                            {
                                List< Response_Driver_vehicle.Vehicle_Info> getlist=new ArrayList<>();
                                getlist=response.body().data;

                                try{
                                    get_vehicle_type=Integer.valueOf(getlist.get(0).getVehicle_type_id());

                                }catch (Exception e){e.printStackTrace();}

                                try{Picasso.with(Vehicle_reg.this).load(getlist.get(0).getPermit()).into(btn_permit);  }catch (Exception e){e.printStackTrace();}

                                try{Picasso.with(Vehicle_reg.this).load(getlist.get(0).getVehicle_other_doc()).into(btn_other_doc);  }catch (Exception e){e.printStackTrace();}

                                try{Picasso.with(Vehicle_reg.this).load(getlist.get(0).getVehicle_photo()).into(btn_vehicle);
                                    btn_ok.setVisibility(View.GONE);
                                }catch (Exception e){e.printStackTrace();}

                                try{Picasso.with(Vehicle_reg.this).load(getlist.get(0).getVehicle_rc()).into(btn_vehicle_rc);  }catch (Exception e){e.printStackTrace();}

                                try{Picasso.with(Vehicle_reg.this).load(getlist.get(0).getVehicle_insurance_id()).into(btn_insurence_id);  }catch (Exception e){e.printStackTrace();}

                                try{ ed_no.setText(getlist.get(0).getVehicle_number()); }catch (Exception e){e.printStackTrace();}

                            }
                        }catch (Exception e){
                            progressDialog.dismiss();
                            e.printStackTrace();}

                    }

                    @Override
                    public void onFailure(Call<Response_Driver_vehicle> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(Vehicle_reg.this, "Error : "+t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                progressDialog.dismiss();
                Toast.makeText(Vehicle_reg.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }catch (Exception e){
            progressDialog.dismiss();
            e.printStackTrace();}

    }


    private void Call_Vihicle_Api() {

        HashMap map= new HashMap<>();
        if (isNetworkAvailable(Vehicle_reg.this))
        {
            Call<Response_Vehicle_type> call= APIClient.getWebServiceMethod().get_All_vehicle_type(map);
            call.enqueue(new Callback<Response_Vehicle_type>() {
                @Override
                public void onResponse(Call<Response_Vehicle_type> call, Response<Response_Vehicle_type> response) {
                    String status=response.body().getApi_status();
                    String msg=response.body().getApi_message();
                    String txt_gender=App_Conteroller.sharedpreferences.getString(SP_Utils.LOGIN_GENDER,"");
                    if (status.equalsIgnoreCase("1") && msg.equalsIgnoreCase("success") )
                    {
                        for (int i=0;i<response.body().getData().size();i++)
                        {
                            if (response.body().getData().get(i).getVehicle_type().trim().equalsIgnoreCase("Scooty"))
                            {
                                if (txt_gender.equalsIgnoreCase("Female")){
                                    vehicle_list.add(response.body().getData().get(i).getVehicle_type());
                                    get_vehicle_type_list.add(response.body().getData().get(i));
                                }

                            }else{
                                vehicle_list.add(response.body().getData().get(i).getVehicle_type());
                                get_vehicle_type_list.add(response.body().getData().get(i));
                            }
                        }

                        ArrayAdapter aa=new ArrayAdapter(Vehicle_reg.this,R.layout.textview_address_show,vehicle_list);
                        vehicle_type.setAdapter(aa);

                    }else{

                        //   Toast.makeText(From_Location.this, "status "+status+"\n"+"msg "+msg, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Response_Vehicle_type> call, Throwable t) {
                    Toast.makeText(Vehicle_reg.this, "Error : "+t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(Vehicle_reg.this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }


    private void Call_Api() {

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (isNetworkAvailable(Vehicle_reg.this))
        {

            Log.i(TAG,"Token ID : call api "+refreshedToken);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String new_date=dateFormat.format(date);



            String driverid=App_Conteroller.sharedpreferences.getString(SP_Utils.LOGIN_DRIVER_ID,"");
            RequestBody body_driver_id =RequestBody.create(okhttp3.MultipartBody.FORM, ""+driverid);
            RequestBody body_status =RequestBody.create(okhttp3.MultipartBody.FORM, "Active");
            RequestBody body_type_id =RequestBody.create(okhttp3.MultipartBody.FORM, ""+get_Vehicle_id);
            RequestBody body_tokene =RequestBody.create(okhttp3.MultipartBody.FORM, ""+refreshedToken);
            RequestBody body_number =RequestBody.create(okhttp3.MultipartBody.FORM, ""+ed_no.getText().toString().trim());

            File file_permit = new File(PATH_PERMIT);
            File file_vehicle = new File(PATH_VEHICLE);
            File file_rc = new File(PATH_RC);
            File file_other_doc = new File(PATH_OTHER_DOC);
            File file_insurense = new File(PATH_INSURENSE);

            RequestBody request_file_permit = RequestBody.create(MediaType.parse("multipart/form-data"), file_permit);
            RequestBody request_file_vehicle = RequestBody.create(MediaType.parse("multipart/form-data"), file_vehicle);
            RequestBody request_file_rc = RequestBody.create(MediaType.parse("multipart/form-data"), file_rc);
            RequestBody request_file_other_doc = RequestBody.create(MediaType.parse("multipart/form-data"), file_other_doc);
            RequestBody request_file_insurense = RequestBody.create(MediaType.parse("multipart/form-data"), file_insurense);

            MultipartBody.Part body_request_permit = MultipartBody.Part.createFormData("permit", file_permit.getName(), request_file_permit);
            MultipartBody.Part body_request_vehicle = MultipartBody.Part.createFormData("vehicle_photo", file_vehicle.getName(), request_file_vehicle);
            MultipartBody.Part body_request_rc = MultipartBody.Part.createFormData("vehicle_rc", file_rc.getName(), request_file_rc);
            MultipartBody.Part body_request_other_doc = MultipartBody.Part.createFormData("vehicle_other_doc", file_other_doc.getName(), request_file_other_doc);
            MultipartBody.Part body_request_insurense = MultipartBody.Part.createFormData("vehicle_insurance_id", file_insurense.getName(), request_file_insurense);

            Call<Response_vehicle> call= APIClient.getWebServiceMethod().vehicle_register(body_tokene,body_driver_id,body_type_id,body_number,body_status,
                        body_request_permit,body_request_vehicle,body_request_rc,body_request_other_doc,body_request_insurense);

                call.enqueue(new Callback<Response_vehicle>() {
                    @Override
                    public void onResponse(Call<Response_vehicle> call, Response<Response_vehicle> response) {
                        progressDialog.dismiss();
                        try{
                            String status=response.body().getApi_status();
                            String msg=response.body().getApi_message();

                            if (status.equalsIgnoreCase("1") && msg.equalsIgnoreCase("success") )
                            {
                                Toast.makeText(Vehicle_reg.this, "successfully Add Your Vehicle", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Vehicle_reg.this, "status "+status+"\n"+"msg "+msg, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(Vehicle_reg.this, "Please retry..", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();}
                    }
                    @Override
                    public void onFailure(Call<Response_vehicle> call, Throwable t) {
                        Call_Api();
                       // Toast.makeText(Vehicle_reg.this, "Error: "+t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        }else{
            progressDialog.dismiss();
            Toast.makeText(Vehicle_reg.this, "No Internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void Init() {
        vehicle_type=findViewById(R.id.spiner_type);
        btn_ok=findViewById(R.id.btn_submit);
        btn_permit=findViewById(R.id.pic_permit);
        btn_other_doc=findViewById(R.id.pic_other);
        btn_vehicle=findViewById(R.id.pic_vehicle);
        btn_vehicle_rc=findViewById(R.id.pic_vehicle_rc);
        btn_insurence_id=findViewById(R.id.pic_insurece);
        ed_no=findViewById(R.id.vehicle_no);

        ImageView btn_back1=findViewById(R.id.btn_back);
        btn_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        Set_docu_pic();
        super.onResume();
    }

    private void Set_docu_pic() {

        if (PATH_PERMIT!=""){    App_Utils.loadProfileImage_c(Vehicle_reg.this,PATH_PERMIT,btn_permit);                }
        if (PATH_VEHICLE!=""){   App_Utils.loadProfileImage(Vehicle_reg.this,PATH_VEHICLE,btn_vehicle);              }
        if (PATH_RC!=""){        App_Utils.loadProfileImage_c(Vehicle_reg.this,PATH_RC,btn_vehicle_rc);                }
        if (PATH_INSURENSE!=""){ App_Utils.loadProfileImage_c(Vehicle_reg.this,PATH_INSURENSE,btn_insurence_id);       }
        if (PATH_OTHER_DOC!=""){ App_Utils.loadProfileImage_c(Vehicle_reg.this,PATH_OTHER_DOC,btn_other_doc);          }

    }

   /* private void Set_docu_pic() {

        if (PATH_PERMIT!="")      {     Picasso.with(Vehicle_reg.this).load(PATH_PERMIT).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(btn_permit);                }
        if (PATH_VEHICLE!="")     {     Picasso.with(Vehicle_reg.this).load(PATH_VEHICLE).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(btn_vehicle);             }
        if (PATH_RC!="")          {     Picasso.with(Vehicle_reg.this).load(PATH_RC).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(btn_vehicle_rc);              }
        if (PATH_INSURENSE!="")   {     Picasso.with(Vehicle_reg.this).load(PATH_INSURENSE).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(btn_insurence_id);      }
        if (PATH_OTHER_DOC!="")   {     Picasso.with(Vehicle_reg.this).load(PATH_OTHER_DOC).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(btn_other_doc);         }

    }*/

}
