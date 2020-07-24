package com.example.bookintershala.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookintershala.R
import com.example.bookintershala.activity.DesciptionActivity
import com.example.bookintershala.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Book>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHoler>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHoler {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHoler(view)

    }

    override fun getItemCount(): Int {

        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHoler, position: Int) {
        val book = itemList[position]
        holder.textName.text = book.bookName
        holder.textAuthor.text = book.bookAuthor
        holder.textPrice.text = book.bookPrice
        holder.textRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.book_app_icon_web).into(holder.imgBook)
        holder.llContent.setOnClickListener {
            val intent = Intent(context, DesciptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }

    }

    class DashboardViewHoler(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.txtRecyclerName)
        val textAuthor: TextView = view.findViewById(R.id.txtRecyclerAuthor)
        val textPrice: TextView = view.findViewById(R.id.txtRecyclerPrice)
        val textRating: TextView = view.findViewById(R.id.txtRecyclerStar)
        val imgBook: ImageView = view.findViewById(R.id.imgRecyclerItem)
        val llContent: RelativeLayout = view.findViewById(R.id.llContent)
    }


}