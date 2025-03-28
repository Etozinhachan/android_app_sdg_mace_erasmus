package me.erasmusteam.odsmaceerasmusapp.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import me.erasmusteam.odsmaceerasmusapp.R
import me.erasmusteam.odsmaceerasmusapp.data.ActivityData
import me.erasmusteam.odsmaceerasmusapp.interfaces.RecyclerViewEvent
import java.io.ByteArrayOutputStream


class ActivityRecyclerViewAdapter(private val dataList: List<ActivityData>, private val listener: RecyclerViewEvent): RecyclerView.Adapter<ActivityRecyclerViewAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_activity_recycler_view_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        if (currentItem.images?.isNotEmpty() == true) {
            val imageBytes = currentItem.images[0]
            Glide.with(holder.rvImage.context)
                .clear(holder.rvImage)

            /*Glide.with(holder.rvImage.context)
                .asBitmap()
                .load(imageBytes)
                .override(800, 800) // Resize before displaying
                .into(holder.rvImage)*/
            Glide.with(holder.rvImage.context)
                .asBitmap()
                .load(imageBytes)
                .override(holder.rvImage.width, holder.rvImage.height) // Resize before displaying
                .into(object: CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val compressedSize = resource.allocationByteCount
                        Log.i("ImageSize", "Compressed size: $compressedSize")

                        holder.rvImage.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            Log.i("ImageSize", imageBytes.size.toString())
        }

        holder.rvCountry.text = currentItem.country
        holder.rvSDG.text = currentItem.ods
        holder.rvType.text = currentItem.type
        holder.rvExplanation.text = currentItem.explanation

        if (currentItem.activity_state == 1){
            //println("GRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR")
            holder.rvConstraintLayout.setBackgroundColor(Color.parseColor("#400099FF"))
        }else if(currentItem.activity_state == 0){
            holder.rvConstraintLayout.setBackgroundColor(Color.parseColor("#40FF0000"))
        }

    }

    inner  class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rvImage: ImageView = itemView.findViewById(R.id.activityImage)
        val rvCountry: TextView = itemView.findViewById(R.id.activityCountry)
        val rvSDG: TextView = itemView.findViewById(R.id.activitySDG)
        val rvType: TextView = itemView.findViewById(R.id.activityType)
        val rvExplanation: TextView = itemView.findViewById(R.id.activityExplanation)
        val rvCard: CardView = itemView.findViewById(R.id.activityCard)
        val rvConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.activityConstraintLayout)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }

}