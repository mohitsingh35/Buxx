package com.ncs.tradezy.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ncs.tradezy.ResultState
import com.ncs.tradezy.AdContent

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RealtimeDBRepository @Inject constructor(
    private val db:DatabaseReference
): RealtimeRepository {
    private var storageReference=Firebase.storage

    val currentUserID=FirebaseAuth.getInstance().currentUser?.uid
    override fun insertAd(item: AdContent.AdContentItem, images: List<Uri>): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            storageReference = FirebaseStorage.getInstance()
            val totalImages = images.size
            val imageUrls = mutableListOf<String>()
            for (i in 0 until totalImages) {
                val imageRef = storageReference.getReference("images").child(System.currentTimeMillis().toString())
                imageRef.putFile(images[i]).addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { imageUrl ->
                        imageUrls.add(imageUrl.toString())

                        if (imageUrls.size == totalImages) {
                            val itemRef = db.child("Ads").push()
                            itemRef.setValue(item)
                            itemRef.child("images").setValue(imageUrls)
                            trySend(ResultState.Success("Inserted Successfully"))
                            close()
                        }
                    }
                }
            }

            awaitClose {
                close()
            }
        }



    override fun getAd(): Flow<ResultState<List<AdContent>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items=snapshot.children.map {
                    AdContent(
                        it.getValue(AdContent.AdContentItem::class.java),
                        key = it.key
                    )
                }
                trySend(ResultState.Success(items))
                Log.d("pari",items.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }
        db.child("Ads").addValueEventListener(valueEvent)
        awaitClose{
            db.child("Ads").removeEventListener(valueEvent)
            close()
        }
    }
    override fun insertuser(item: RealTimeUserResponse.RealTimeUsers): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            db.child("users").child(currentUserID!!).setValue(
                item
            ).addOnCompleteListener {
                if(it.isSuccessful)
                    trySend(ResultState.Success("Successfully"))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
            awaitClose {
                close()
            }
        }
    override fun update(res: RealTimeUserResponse): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val map=HashMap<String,Any>()
        map["name"]=res.item?.name!!
        map["email"]=res.item.email!!
        map["phNumber"]=res.item.phNumber!!


        db.child("users").child(res.key!!).updateChildren(
            map
        ).addOnCompleteListener{
            trySend(ResultState.Success("Updated Successfully"))
        }
            .addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    override fun getUser(): Flow<ResultState<List<RealTimeUserResponse>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items=snapshot.children.map {
                    RealTimeUserResponse(
                        it.getValue(RealTimeUserResponse.RealTimeUsers::class.java),
                        key = it.key
                    )
                }
                trySend(ResultState.Success(items))
                Log.d("mohit",items.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }
        db.child("users").addValueEventListener(valueEvent)
        awaitClose{
            db.child("users").removeEventListener(valueEvent)
            close()
        }
    }

}