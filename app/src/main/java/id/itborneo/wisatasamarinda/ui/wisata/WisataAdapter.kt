package id.itborneo.wisatasamarinda.ui.wisata

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.itborneo.wisatasamarinda.R
import id.itborneo.wisatasamarinda.data.model.WiPlace
import kotlinx.android.synthetic.main.item_place.view.*


class WisataAdapter(
    var context: Context, val clickListener: (WiPlace) -> Unit
) : RecyclerView.Adapter<WisataAdapter.ViewHolder>() {

    private val TAG = "WisataAdapter"
    private var wiPlaces = listOf<WiPlace>()



    fun setWiPlaces(wiPlaces: List<WiPlace>) {
        notifyDataSetChanged()
        this.wiPlaces = wiPlaces
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_place, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = wiPlaces.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bind(wiPlaces[p1])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(wiPlace: WiPlace) {

            itemView.tvName.text = wiPlace.name
            itemView.tvAddress.text = wiPlace.address

//            Log.d(TAG, "imagepath ${wiPlace.imagePath}")
            Glide.with(itemView.context)
                .load(wiPlace.imagePath)
                .centerCrop()
                .placeholder(context.getDrawable(R.drawable.loading_image))
                .into(itemView.ivPlace)

//            Picasso.get()
//                .load(wiPlace.imagePath)
//                .placeholder(R.drawable.loading_image)
//                .fit()
//                .into(itemView.ivPlace)

            itemView.setOnClickListener {
                clickListener(wiPlace)
            }

        }


    }


}
