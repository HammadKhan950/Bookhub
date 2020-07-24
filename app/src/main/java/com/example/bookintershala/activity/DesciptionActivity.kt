package com.example.bookintershala.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookintershala.R
import com.example.bookintershala.database.BookDatabase
import com.example.bookintershala.database.BookEntity
import com.example.bookintershala.util.ConnectionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_desciption.*
import org.json.JSONException
import org.json.JSONObject

class DesciptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar
    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desciption)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDemo)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBars)
        toolbar = findViewById(R.id.toolbar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayouts)
        progressLayout.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"


        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
            Log.i("Message", bookId.toString())
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(this, "Some error occured!", Toast.LENGTH_SHORT).show()

        }
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)
        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.book_app_icon_web).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )
                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()
                            if (isFav) {
                                btnAddToFav.text = "Remove from favourites"
                                val favColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorFavourites
                                )
                                btnAddToFav.setBackgroundColor(favColor)

                            } else {
                                btnAddToFav.text = "Add to favourites"
                                val favColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(favColor)
                            }
                            btnAddToFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this,
                                            "Book added to favourites",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        btnAddToFav.text = "Remove from favourites"
                                        val favcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFavourites
                                        )
                                        btnAddToFav.setBackgroundColor(favcolor)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Some error occured!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this,
                                            "Book removed from favourites",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        btnAddToFav.text = "Add to favourites"
                                        val noFavColor =
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.colorPrimary
                                            )
                                        btnAddToFav.setBackgroundColor(noFavColor)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Some error occured!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                }

                            }


                        } else {
                            Toast.makeText(this, "Some error occured!", Toast.LENGTH_SHORT).show()

                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this, "Some error occured!", Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {


                    Toast.makeText(this, "Some error occured!", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "7648815128647f"
                    return headers
                }
            }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()

        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookByID(bookEntity.book_id.toString())
                    db.close()
                    return book != null

                }
                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true

                }

                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }

            return false
        }
    }
}