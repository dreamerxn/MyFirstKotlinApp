package com.society.myFinances.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.society.myFinances.firebase.Costs

class CostRepository {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Costs")

    @Volatile private var INSTANCE: CostRepository ?= null

    fun getInstance(): CostRepository{
        return INSTANCE ?: synchronized(this){
            val instance = CostRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadCosts(costsList: MutableLiveData<List<Costs>>){
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _costsList: List<Costs> = snapshot.children.map {
                        dataSnapshot ->
                        dataSnapshot.getValue(Costs::class.java)!!
                    }

                    costsList.postValue(_costsList)

                }catch (e: java.lang.Exception){

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}