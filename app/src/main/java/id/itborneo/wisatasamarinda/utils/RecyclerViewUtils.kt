package id.itborneo.wisatasamarinda.utils

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import id.itborneo.week9_samarindaoutlite.utils.SwipeToDeleteCallback

object RecyclerViewUtils {
    var swipeable = true

    //attach on swipe to delete
    fun attachOnSwipe(
        context: Context,
        recyclerView: RecyclerView,
        onSwipeCalled: (RecyclerView.ViewHolder) -> Unit
    ) {


        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                onSwipeCalled(viewHolder)
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return swipeable
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}