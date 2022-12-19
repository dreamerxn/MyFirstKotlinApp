package com.society.myFinances.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.society.myFinances.adapters.RecAdapter
import com.society.myFinances.firebase.Costs
import com.society.myFinances.R


const val ARG_OBJECT = "object"

private lateinit var costsRecyclerView: RecyclerView
class PageFragment : Fragment() {
    private lateinit var dbref: DatabaseReference
    private lateinit var fBase: FirebaseDatabase
    var isExpenses = false
    var sum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fBase = FirebaseDatabase.getInstance()
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        costsRecyclerView = view.findViewById(R.id.recProfit)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        costsRecyclerView.layoutManager = layoutManager
        getData()
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            when(getInt(ARG_OBJECT)){
                1-> {
                    isExpenses = false
                }
                2-> {
                    isExpenses = true
                }
            }
        }

    }


    private fun getData(){
        val path = "/User"
        val prBar: ProgressBar = view?.findViewById(R.id.progressBar)!!
        val emptyText: TextView = view?.findViewById(R.id.emptyText)!!
        dbref = fBase.getReference(path)
        dbref.keepSynced(true)
        val mList: ArrayList<Costs> = arrayListOf()
        costsRecyclerView.setHasFixedSize(false)

        val adapter = RecAdapter(mList)
        costsRecyclerView.adapter = adapter
        val query: Query = dbref.orderByChild("date")
        query.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                if (snapshot.exists()){
                    for (costSnap in snapshot.children){
                        val costData = costSnap.getValue<Costs>()
                        val sss = costData?.cost!!
                        if (isExpenses){
                            if (sss<0){
                                mList.add(costData)
                            }
                        } else if (sss>0){
                            mList.add(costData)
                        }
                        mList.sortedWith(compareBy { it.comment })

                        adapter.notifyDataSetChanged()
                    }
                    prBar.visibility = View.GONE
                    emptyText.visibility = View.GONE
                } else{
                    prBar.visibility = View.GONE
                    emptyText.text = "Пусто"
                    emptyText.visibility = View.VISIBLE
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}