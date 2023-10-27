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
import com.ncs.tradezy.BuyerLocation
import com.ncs.tradezy.EachAdResponse
import com.ncs.tradezy.HomeScreenState
import com.ncs.tradezy.ImageMessage
import com.ncs.tradezy.MessageResponse
import com.ncs.tradezy.NotificationContent
import com.ncs.tradezy.PromotionalNotification

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RealtimeDBRepository @Inject constructor(
    private val db:DatabaseReference
): RealtimeRepository {
    private var storageReference=Firebase.storage

    private val currentUserID=FirebaseAuth.getInstance().currentUser?.uid
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
                            trySend(ResultState.Success("Posted Successfully"))
                            close()
                        }
                    }
                }
            }
            awaitClose {
                close()
            }
        }



    override fun getAd(): Flow<ResultState<List<EachAdResponse>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items=snapshot.children.map {
                    EachAdResponse(
                        it.getValue(EachAdResponse.EachItem::class.java),
                        key = it.key
                    )
                }
                trySend(ResultState.Success(items))
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
    override fun getbuyer(): Flow<ResultState<List<BuyerLocation>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items=snapshot.children.map {
                    BuyerLocation(
                        it.getValue(BuyerLocation.BuyerLocationItem::class.java),
                        key = it.key
                    )
                }
                trySend(ResultState.Success(items))
            }


            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }
        db.child("buyerlocation").addValueEventListener(valueEvent)
        awaitClose{
            db.child("buyerlocation").removeEventListener(valueEvent)
            close()
        }
    }

    override fun updateAd(res: AdContent): Flow<ResultState<String>>  = callbackFlow {
        trySend(ResultState.Loading)
        val map=HashMap<String,Any>()
        map["trendingViewCount"]= res.item?.trendingViewCount!!
        map["viewCount"]= res.item.viewCount!!



        db.child("Ads").child(res.key!!).updateChildren(
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

    override fun updateADstatus(res: AdContent): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val map=HashMap<String,Any>()
        map["sold"]=res.item?.sold!!
        db.child("Ads").child(res.key!!).updateChildren(
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

    override fun deleteAd(key: String): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        db.child("Ads").child(key).removeValue()
            .addOnCompleteListener{
                trySend(ResultState.Success("Ad Deleted"))
            }
            .addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    override fun insertNotification(item: NotificationContent.NotificationItem): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            db.child("notifications").push().setValue(
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

    override fun getNotification(): Flow<ResultState<List<NotificationContent>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items=snapshot.children.map {
                    NotificationContent(
                        it.getValue(NotificationContent.NotificationItem::class.java),
                        key = it.key
                    )
                }
                trySend(ResultState.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }
        db.child("notifications").addValueEventListener(valueEvent)
        awaitClose{
            db.child("notifications").removeEventListener(valueEvent)
            close()
        }
    }
    override fun getpromoNotification(): Flow<ResultState<List<NotificationContent>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items=snapshot.children.map {
                    NotificationContent(
                        it.getValue(NotificationContent.NotificationItem::class.java),
                        key = it.key
                    )
                }
                trySend(ResultState.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }

        }
        db.child("promotional").addValueEventListener(valueEvent)
        awaitClose{
            db.child("promotional").removeEventListener(valueEvent)
            close()
        }
    }
    override fun updateNotification(res: NotificationContent): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val map=HashMap<String,Any>()
        map["read"]=res.item?.read!!

        db.child("notifications").child(res.key!!).updateChildren(
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
    override fun updatePromoNoti(res: NotificationContent): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val map=HashMap<String,Any>()
        map["msgread"]=res.item?.msgread!!

        db.child("promotional").child(res.key!!).updateChildren(
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


    override fun insertuser(item: RealTimeUserResponse.RealTimeUsers): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            db.child("users").child(currentUserID!!).setValue(
                item
            ).addOnCompleteListener {
                if(it.isSuccessful)
                    trySend(ResultState.Success("Welcome"))
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
        map["fcmToken"]=res.item.fcmToken!!


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
    override fun insertMessage(item: MessageResponse.MessageItems): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            db.child("messages").push().setValue(
                item
            ).addOnCompleteListener {
                if(it.isSuccessful)
                    trySend(ResultState.Success(""))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
            awaitClose {
                close()
            }
        }

    override fun insertImages(images: List<Uri>,otherdetails: MessageResponse.MessageItems): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            storageReference = FirebaseStorage.getInstance()
            val totalImages = images.size
            val imageUrls = mutableListOf<String>()
            for (i in 0 until totalImages) {
                val imageRef = storageReference.getReference("images").child("messages").child(System.currentTimeMillis().toString())
                imageRef.putFile(images[i]).addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { imageUrl ->
                        imageUrls.add(imageUrl.toString())
                        if (imageUrls.size == totalImages) {
                            val itemRef = db.child("messages").push()
                            itemRef.setValue(otherdetails)
                            itemRef.child("images").setValue(imageUrls)
                            trySend(ResultState.Success("Sent Successfully"))
                            close()
                        }
                    }
                }
            }

            awaitClose {
                close()
            }
        }

    override fun getMessage(): Flow<ResultState<List<MessageResponse>>> = callbackFlow{
        trySend(ResultState.Loading)

        val valueEvent=object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val items=snapshot.children.map {
                    MessageResponse(
                        it.getValue(MessageResponse.MessageItems::class.java),
                        key = it.key
                    )
                }
                val chatList= ArrayList<MessageResponse>()
                chatList.clear()
                for (i in 0 until items.size){
                    chatList.add(items[i])
                }
                trySend(ResultState.Success(chatList))

            }
            override fun onCancelled(error: DatabaseError) {
                trySend(ResultState.Failure(error.toException()))
            }
        }
        db.child("messages").addValueEventListener(valueEvent)
        awaitClose{
            db.child("messages").removeEventListener(valueEvent)
            close()
        }
    }

    override fun updateMessage(res: MessageResponse): Flow<ResultState<String>>  = callbackFlow {
        trySend(ResultState.Loading)
        val map=HashMap<String,Any>()
        map["read"]=res.item?.read!!

        db.child("messages").child(res.key!!).updateChildren(
            map
        ).addOnCompleteListener{
            trySend(ResultState.Success(""))
        }
            .addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        awaitClose {
            close()
        }
    }

}