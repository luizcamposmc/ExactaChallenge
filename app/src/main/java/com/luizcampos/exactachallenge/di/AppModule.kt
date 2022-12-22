package com.luizcampos.exactachallenge.di

import android.content.Context
import android.widget.Toast
import androidx.room.Room
import com.luizcampos.exactachallenge.api.CardsRetrofitApi
import com.luizcampos.exactachallenge.helper.Constants
import com.luizcampos.exactachallenge.helper.Constants.TABLE_NAME
import com.luizcampos.exactachallenge.helper.ToastDurationProvider
import com.luizcampos.exactachallenge.model.database.ExpenseDatabase
import com.luizcampos.exactachallenge.model.database.ExpenseEntity
import com.luizcampos.exactachallenge.model.database.dao.ExpenseDao
import com.luizcampos.exactachallenge.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, ExpenseDatabase::class.java, TABLE_NAME)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: ExpenseDatabase) = db.expenseDao()

    @Provides
    fun provideEntity() = ExpenseEntity()

    @Provides
    fun provideExpenseRepository(dao: ExpenseDao) : ExpenseRepository = ExpenseDatabaseSource(dao)

    @Provides
    @Singleton
    fun provideToastRepository(
        @ApplicationContext context: Context,
        toastDurationProvider: ToastDurationProvider
    ) : ToastRepository = ToastyDataSource(context, toastDurationProvider)

    @Provides
    @Singleton
    fun provideToastDuration() : ToastDurationProvider = object : ToastDurationProvider {
        override val LENGTH_SHORT: Int
            get() = Toast.LENGTH_SHORT

        override val LENGTH_LONG: Int
            get() = Toast.LENGTH_LONG

        override val LENGTH_1S: Int
            get() = 1000

        override val LENGTH_500MS: Int
            get() = 500
    }

    @Provides
    @Singleton
    fun provideCardsRetrofit() : CardsRetrofitApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL_CARDS)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getClientOkHttp())
        .build()
        .create(CardsRetrofitApi::class.java)

    @Provides
    @Singleton
    fun provideCardRepository(cardsRetrofitApi: CardsRetrofitApi) : CardRepository =
        CardDataSource(cardsRetrofitApi)

    private fun getClientOkHttp() : OkHttpClient {
        val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        connectionSpec.tlsVersions(TlsVersion.TLS_1_2).build()

        return OkHttpClient.Builder()
            .readTimeout(Constants.TIMEOUT_HTTP, TimeUnit.SECONDS)
            .connectTimeout(Constants.TIMEOUT_HTTP, TimeUnit.SECONDS)
            .callTimeout(Constants.TIMEOUT_HTTP, TimeUnit.SECONDS)
            .apply {
                interceptors().add(Interceptor { chain ->
                    val request = chain.request()
                    chain.proceed(
                        request.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .build()
                    )
                })

                if (Constants.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    interceptors().add(interceptor)
                }
            }
            .connectionSpecs(Collections.singletonList(connectionSpec.build()))
            .build()
    }
}