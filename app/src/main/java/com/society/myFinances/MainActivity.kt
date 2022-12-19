package com.society.myFinances

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.society.myFinances.adapters.RecAdapter
import com.society.myFinances.adapters.ViewPagerAdapter
import com.society.myFinances.firebase.Costs
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : FragmentActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager2: ViewPager2
    private lateinit var fragAdapter: ViewPagerAdapter
    private lateinit var sumText: TextView
    private lateinit var upCosts: TextView
    private lateinit var downCosts: TextView
    private lateinit var fab: FloatingActionButton


    private lateinit var dbRef: DatabaseReference
    private lateinit var fireDb: FirebaseDatabase
    var sumProfit = 0
    var sumExpenses = 0
    var sum =0
    @SuppressLint("MissingInflatedId", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fireDb = FirebaseDatabase.getInstance()
        fireDb.setPersistenceEnabled(true)
        find()
        bottomNav()
        initial()
        getSum()

        fab.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val closeBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.closeBtn)
            val editCost = view.findViewById<EditText>(R.id.editCost)
            val editComment = view.findViewById<EditText>(R.id.editComment)
            closeBtn.setOnClickListener {
                try {
                    val mCost = editCost.text.toString().toInt()
                    val mComment = editComment.text.toString()
                    val uid = (0..10000000).random()
                    pushData(mCost, mComment, uid)
                    dialog.dismiss()
                }catch (e: java.lang.Exception){
                    Toast.makeText(this, "Error $e", Toast.LENGTH_LONG).show()
                }


            }
            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

    }

    private fun initial(){
        fragAdapter = ViewPagerAdapter(this)
        viewPager2.adapter = fragAdapter
    }

    private fun find() {
        bottomNavigationView = findViewById(R.id.bottomNav)
        viewPager2 = findViewById(R.id.myViewPager)
        sumText = findViewById(R.id.sumText)
        upCosts = findViewById(R.id.upCosts)
        downCosts = findViewById(R.id.downCosts)
        fab = findViewById(R.id.myFab)
    }

    private fun bottomNav(){
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(1).isEnabled = false
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.trendingUp ->{
                    viewPager2.setCurrentItem(0, true)

                }
                R.id.trendingDown ->{
                    viewPager2.setCurrentItem(1, true)
                }
            }
            true
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if (position == 1){
                    bottomNavigationView.menu.getItem(0).isChecked = true
                    bottomNavigationView.menu.getItem(2).isChecked = false

                }
                else if (position == 0){
                    bottomNavigationView.menu.getItem(2).isChecked = true
                    bottomNavigationView.menu.getItem(0).isChecked = false

                }
                super.onPageSelected(position)
            }
        })
    }

    private fun getSum(){
        sum = 0
        dbRef = fireDb.getReference("/User")
        dbRef.keepSynced(true)
        dbRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                sum = 0
                sumProfit = 0
                sumExpenses = 0
                if (snapshot.exists()){
                    for (costSnap in snapshot.children){
                        val costData = costSnap.getValue<Costs>()
                        val m = costData?.cost!!
                        if (m<0){
                            sumExpenses+=m
                        } else if(m>0){
                            sumProfit+=m
                        }
                        sum+=m
                    }
                }
                sumText.text = sum.toString()
                upCosts.text = "+$sumProfit"
                downCosts.text = sumExpenses.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error $error", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun pushData(cost: Int, comment: String, uid: Int){
        dbRef = fireDb.getReference("/User")
        val date = LocalDate.now()
        val time = LocalDateTime.now()
        val mList = Costs(cost, comment, uid, time.toString())
        dbRef.push().setValue(mList)
        val mListik: ArrayList<Costs> = arrayListOf()
        val adapter = RecAdapter(mListik)
        adapter.notifyDataSetChanged()
    }

}