package me.erasmusteam.odsmaceerasmusapp.adapters

import android.animation.ObjectAnimator
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.BuildConfig
import me.erasmusteam.odsmaceerasmusapp.R
import me.erasmusteam.odsmaceerasmusapp.data.SDGData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class SDGAdapter(private val SDGs: List<SDGData>, private val context: Context) : RecyclerView.Adapter<SDGAdapter.SDGViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SDGViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return SDGViewHolder(view)
    }

    override fun getItemCount(): Int {
        return SDGs.size
    }

    override fun onBindViewHolder(holder: SDGViewHolder, position: Int) {
        val SDG = SDGs[position]
        holder.goalImage.setImageResource(SDG.imageResId)
        holder.arrow.setImageResource(R.drawable.baseline_arrow_forward_ios_24)
        if (holder.isMenuVisible){
            holder.slidingMenu.visibility = View.GONE
            ObjectAnimator.ofFloat(holder.slidingMenu, "translationX", 100f).apply{
                duration = 300
            }.start()
            ObjectAnimator.ofFloat(holder.slidingMenu, "alpha", 1f, 0f).apply {
                duration = 300
            }.start()
        }
        holder.isMenuVisible = false

        val environmentPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.path.toString()
        else
            Environment.getExternalStorageDirectory().path.toString()

        val fileBrochure = File(environmentPath + "/" + context.resources.getResourceName(SDG.fileName).split("/")[1] + ".pdf")
        if (!fileBrochure.exists()){
            CopyAssetsbrochure(SDG.fileName, environmentPath + "/" + context.resources.getResourceName(SDG.fileName).split("/")[1] + ".pdf")
        }

        val pdfFile: File = File(environmentPath + "/" + context.resources.getResourceName(SDG.fileName).split("/")[1] + ".pdf")

        val pdfUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        holder.goalImage.setOnClickListener{
            if (holder.isMenuVisible){
                holder.arrow.setImageResource(R.drawable.baseline_arrow_forward_ios_24)
                ObjectAnimator.ofFloat(holder.slidingMenu, "translationX", 100f).apply{
                    duration = 300
                }.start()
                ObjectAnimator.ofFloat(holder.slidingMenu, "alpha", 1f, 0f).apply {
                    duration = 300
                }.start()
                holder.slidingMenu.postDelayed({ holder.slidingMenu.visibility = View.GONE}, 300)
            }else{
                holder.arrow.setImageResource(R.drawable.baseline_arrow_back_ios_24)
                holder.slidingMenu.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(holder.slidingMenu, "translationX", 0f).apply {
                    duration = 300
                }.start()
                ObjectAnimator.ofFloat(holder.slidingMenu, "alpha", 0f, 1f).apply {
                    duration = 300
                }.start()
            }
            holder.isMenuVisible = !holder.isMenuVisible
        }

        holder.menuButton2.setOnClickListener{
            try {
                context.startActivity(intent)
                /*val intentToShare = Intent(Intent.ACTION_SEND).apply{
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, pdfUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intentToShare, "Share PDF"))*/
            } catch (e: ActivityNotFoundException) {
                Log.e("PDF Viewer", "No application found to open PDF", e)
                Toast.makeText(context, "No PDF Viewer found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class SDGViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val goalImage: ImageButton = itemView.findViewById(R.id.goalImage)
        val slidingMenu: LinearLayout = itemView.findViewById(R.id.slidingMenu)
        val menuButton1: ImageButton = itemView.findViewById(R.id.menuButton1)
        val menuButton2: ImageButton = itemView.findViewById(R.id.menuButton2)

        val arrow : ImageView = itemView.findViewById(R.id.testArrow)

        var isMenuVisible = false
    }

    private fun CopyAssetsbrochure(file_id: Int, file_path: String) {
        val res = context.resources
        val `in` = res.openRawResource(file_id)
        var out: OutputStream? = null
        try {
            out = FileOutputStream(
                file_path
            )
            copyFile(`in`, out)
            `in`.close()
            out.flush()
            out.close()
        } catch (e: Exception) {
            Log.e("tag", e.message!!)
        }


    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while ((`in`.read(buffer).also { read = it }) != -1) {
            out.write(buffer, 0, read)
        }
    }
}