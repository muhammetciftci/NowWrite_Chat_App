package com.mtc.nowwrite.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mtc.nowwrite.repository.auth.IAuthRepository
import com.mtc.nowwrite.repository.auth.AuthRepositoryImpl
import com.mtc.nowwrite.repository.database.FirebaseDBRepositoryImpl
import com.mtc.nowwrite.repository.database.IFirebaseDBRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun injectFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun injectFirebaseDatabaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun injectFirebaseStroageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun injectStroageReferenceInstance(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    @Provides
    @Singleton
    fun injectAuthRepository(
        auth: FirebaseAuth,
    ): IAuthRepository {
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun injectFirebaseDBRepositoryImpl(
        firebaseDatabase: FirebaseDatabase,
        firebaseStorage: FirebaseStorage,
        storageReference: StorageReference,
        auth: FirebaseAuth
    ): IFirebaseDBRepository {
        return FirebaseDBRepositoryImpl(firebaseDatabase, firebaseStorage, storageReference, auth)
    }

}