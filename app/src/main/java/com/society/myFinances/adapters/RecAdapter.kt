package com.society.myFinances.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.*
import com.society.myFinances.firebase.Costs
import com.society.myFinances.R


class RecAdapter(private val costsList: ArrayList<Costs>) : RecyclerView.Adapter<RecAdapter.MyViewHolder>() {

    private var mList = costsList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]
        val j = currentItem.cost!!
        if (j>0){
            holder.mCost.text = "+$j"
        }
        else if (j<0){
            holder.mCost.text = "$j"
        }
        holder.mComment.text = currentItem.comment
        val date = currentItem.date
        if (date != null) {
            holder.mDate.text = date.substring(0, 10)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCostsList(mList: List<Costs>){
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    @SuppressLint("MissingInflatedId", "InflateParams")
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mCost: TextView = itemView.findViewById(R.id.cost)
        val mComment: TextView = itemView.findViewById(R.id.comment)
        val mDate: TextView = itemView.findViewById(R.id.mDate)
        val mCard: CardView = itemView.findViewById(R.id.rootCard)
        val mContext = itemView.context

        init {
            itemView.setOnLongClickListener { it ->

                val dialog = BottomSheetDialog(it.context)
                val view = LayoutInflater.from(it.context).inflate(R.layout.rec_bottom_sheet_dialog, null)
                val remove: TextView = view.findViewById(R.id.removeItem)
                val edit: TextView = view.findViewById(R.id.editItem)
                remove.setOnClickListener {
                    val uid = mList[adapterPosition].uid!!
                    delFromDB(uid)
                    dialog.dismiss()
                    if (itemCount==1){
                        mList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    }
                    notifyItemRemoved(adapterPosition)
                }
                edit.setOnClickListener { it ->
                    val editDialog = BottomSheetDialog(it.context)
                    val editView = LayoutInflater.from(it.context).inflate(R.layout.bottom_sheet_dialog, null)
                    val editCost: EditText = editView.findViewById(R.id.editCost)
                    val editComment: EditText = editView.findViewById(R.id.editComment)
                    val mFab: ExtendedFloatingActionButton = editView.findViewById(R.id.closeBtn)
                    val mCost = mList[adapterPosition].cost
                    val mComment = mList[adapterPosition].comment
                    editCost.setText(mCost.toString())
                    editComment.setText(mComment)
                    val uid = mList[adapterPosition].uid
                    mFab.setOnClickListener {
                        val mCost = editCost.text.toString().toInt()
                        val mComment = editComment.text.toString()
                        updateData(mCost, mComment, uid!!)
                        editDialog.dismiss()
                        dialog.dismiss()
                    }
                    editDialog.setCancelable(true)
                    editDialog.setContentView(editView)
                    editDialog.show()
                }
                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
                return@setOnLongClickListener true
            }

        }
    }

    fun updateData(mCost: Int, mComment: String, mUID: Int){
        val dbRef: DatabaseReference
        val fireDb: FirebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = fireDb.getReference("/User")
        val query: Query = dbRef.orderByChild("uid").equalTo(mUID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val data =
                        Costs(mCost, mComment, mUID)
                    //                    mData.child("title").setValue(openTitle.getText());
                    val gg = dataSnapshot.key
                    if (gg != null) {
                        dbRef.child(gg).setValue(data)
                    }
                    notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun delFromDB(mUID: Int){
        val dbRef: DatabaseReference
        val fireDb: FirebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = fireDb.getReference("/User")
        val query: Query = dbRef.orderByChild("uid").equalTo(mUID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    dataSnapshot.ref.removeValue()
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}