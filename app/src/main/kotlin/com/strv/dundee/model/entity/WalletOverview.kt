package com.strv.dundee.model.entity

import android.os.Parcel
import com.strv.dundee.model.repo.ExchangeRatesLiveData
import com.strv.ktools.KParcelable
import com.strv.ktools.parcelableCreator

data class WalletOverview(
	var coin: String,
	var amount: Double = 0.0,
	val boughtPrices: MutableList<Pair<String, Double>> = mutableListOf()
) : KParcelable {

	fun getBoughtPrice(currency: String?, exchangeRates: ExchangeRatesLiveData?): Double =
		boughtPrices.sumByDouble { exchangeRates?.value?.data?.calculate(it.first, currency, it.second) ?: 0.0 }

	fun getProfit(currency: String?, exchangeRates: ExchangeRatesLiveData?, ticker: Ticker?): Double =
		(exchangeRates?.value?.data?.calculate(ticker?.currency, currency, ticker?.getValue(amount)) ?: 0.0) - getBoughtPrice(currency, exchangeRates)

	private constructor(parcel: Parcel) : this(
		coin = parcel.readString(),
		amount = (parcel.readValue(Double::class.java.classLoader) as? Double)!!) {
		parcel.readList(boughtPrices, null)
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(coin)
		parcel.writeValue(amount)
		parcel.writeList(boughtPrices)
	}

	companion object {
		@JvmField
		val CREATOR = parcelableCreator(::WalletOverview)
	}
}
