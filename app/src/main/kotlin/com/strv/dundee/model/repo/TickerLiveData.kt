package com.strv.dundee.model.repo

import com.strv.dundee.model.api.bitfinex.BitfinexApi
import com.strv.dundee.model.api.bitstamp.BitstampApi
import com.strv.dundee.model.cache.BitcoinCache
import com.strv.dundee.model.entity.BitcoinSource
import com.strv.dundee.model.entity.Ticker
import com.strv.ktools.NetworkBoundResource
import com.strv.ktools.ResourceLiveData
import com.strv.ktools.inject

class TickerLiveData : ResourceLiveData<Ticker>() {
	val cache by inject<BitcoinCache>()
	val bitstampApi by inject<BitstampApi>()
	val bitfinexApi by inject<BitfinexApi>()

	fun refresh(source: String, coin: String, currency: String) {
		val api = when (source) {
			BitcoinSource.BITSTAMP -> bitstampApi
			BitcoinSource.BITFINEX -> bitfinexApi
			else -> bitstampApi
		}

		setupCached(object : NetworkBoundResource.Callback<Ticker> {
			override fun saveCallResult(item: Ticker) {
				cache.putTicker(item)
			}

			override fun shouldFetch(dataFromCache: Ticker?) = true

			override fun loadFromDb() = cache.getTicker(source, currency, coin)

			override fun createNetworkCall() = api.getTicker(coin, currency)

		})
	}
}