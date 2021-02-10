package com.example.fetchgituserdetailsretroandvolley

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.fetchgituserdetailsretroandvolley.Common.Constants
import com.example.fetchgituserdetailsretroandvolley.Volley.gitPojo
import com.example.fetchgituserdetailsretroandvolley.Volley.responseApiVolley
import com.example.fetchgituserdetailsretroandvolley.retroFit.RetrofitCallback
import com.example.fetchgituserdetailsretroandvolley.retroFit.gitBean
import com.example.fetchgituserdetailsretroandvolley.retroFit.responseAPIRetrofit
import com.venkat.toastmessager.ToastMessager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var edtusername: EditText
    lateinit var btnsearch: ImageView
    lateinit var btnclear: ImageView
    lateinit var profile: ImageView
    lateinit var text_bio: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtusername = findViewById(R.id.git_username) as EditText
        btnsearch = findViewById(R.id._search) as ImageView
        btnclear = findViewById(R.id.search_clear) as ImageView
        profile = findViewById(R.id._profile) as ImageView
        text_bio = findViewById(R.id.text_bio) as TextView

        btnsearch.setOnClickListener {
            if (!TextUtils.isEmpty(edtusername.text.toString())) {
                usingVolley(edtusername.text.toString()) //enable this Volley API will Call
               // usingRetroFit(edtusername.text.toString()) //enable this Retrofit API will Call
            } else {
                edtusername?.error = "Enter username"
            }

        }
        btnclear.setOnClickListener {
            edtusername.setText("")
        }
    }

    private fun usingVolley(username: String) {
        getDatafromAPIVolley(username, object : responseApiVolley {
            override fun getAPIresponse(bean: gitPojo) {
                Glide
                    .with(this@MainActivity)
                    .load(bean.avatar_url)
                    .centerCrop()
                    .placeholder(R.drawable.loading_image)
                    .into(profile);
                with(bean) {
                    text_bio.text =
                        "Name:- $name \nRepos Url:- $repos_url \nLogin Id:- $login \nLocation:-  $location \nUpdated at:- ${Constants.convertDate(
                            updated_at
                        )} \nCreated at:- ${Constants.convertDate(created_at)}"
                }

            }
        })

    }


    private fun usingRetroFit(username: String) {

        getDatafromAPI(username, object :
            responseAPIRetrofit {
            override fun getAPIresponse(bean: gitBean) {
                Glide
                    .with(this@MainActivity)
                    .load(bean.avatar_url)
                    .centerCrop()
                    .placeholder(R.drawable.loading_image)
                    .into(profile);
                with(bean) {
                    text_bio.text =
                        "Name:- $name \nRepos Url:- $repos_url \nLogin Id:- $login \nLocation:-  $location \nUpdated at:- ${Constants.convertDate(
                            updated_at
                        )} \nCreated at:- ${Constants.convertDate(created_at)}"
                }
            }
        })


    }

    private fun getDatafromAPI(userName: String, responseAPIRetrofit: responseAPIRetrofit) {
        val retrofitCallback = RetrofitCallback.create()
        val gituser: Call<gitBean>
        gituser = retrofitCallback.GetUser(userName)
        gituser.enqueue(object : Callback<gitBean> {
            override fun onFailure(call: Call<gitBean>, t: Throwable) {

            }

            override fun onResponse(call: Call<gitBean>, response: Response<gitBean>) {
                val data: gitBean
                if (response.isSuccessful) {
                    data = response.body()!!
                    responseAPIRetrofit.getAPIresponse(data)
                } else {
                    ToastMessager.showTopCenter(this@MainActivity, "User Not Found"); }
            }
        })
    }


    fun getDatafromAPIVolley(username: String, responseAPI: responseApiVolley) {
        var requestQueue: RequestQueue? = null
        requestQueue = Volley.newRequestQueue(this)
        // val url = "https://www.instagram.com/$username/?__a=1"
        val url = "https://api.github.com/users/$username"
        val request = JsonObjectRequest(Request.Method.GET, url, null, com.android.volley.Response.Listener { response ->
                    //  val obj = response.getJSONObject("graphql").getJSONObject("user")

                    var bean: gitPojo = gitPojo()

                    with(bean)
                    {
                        name = response.getString("name")
                        repos_url = response.getString("repos_url")
                        login = response.getString("login")
                        location = response.getString("location")
                        updated_at = response.getString("updated_at")
                        created_at = response.getString("created_at")
                        avatar_url = response.getString("avatar_url")
                    }

                    responseAPI.getAPIresponse(bean)

                    /*obj.getString("full_name")
                    obj.getJSONObject("edge_followed_by").getInt("count")
                    obj.getJSONObject("edge_follow").getInt("count")
                    obj.getString("profile_pic_url_hd")
                    obj.getString("biography")
                    obj.getString("external_url")
                    obj.getBoolean("is_private")
                    obj.getBoolean("is_verified")
                    obj.getJSONObject("edge_owner_to_timeline_media").getInt("count")
                    obj.getJSONObject("edge_felix_video_timeline").getInt("count")
                    obj.getInt("highlight_reel_count")*/

                },
                com.android.volley.Response.ErrorListener { error ->

                    ToastMessager.showCenter(this, error.toString())
                    error.printStackTrace()
                })

        requestQueue?.add(request)
    }

}