package com.redzuandika.musiqlo


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Logika untuk memproses pesan yang diterima di sini
    }

    override fun onNewToken(token: String) {
        // Logika untuk menyimpan atau memperbarui token perangkat pengguna di sini
    }
}
