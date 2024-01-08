package com.redzuandika.musiqlo.murid.midtrans

import com.google.gson.annotations.SerializedName

data class ResponseMidtrans(

	@field:SerializedName("status_message")
	val statusMessage: String? = null,

	@field:SerializedName("transaction_id")
	val transactionId: String? = null,

	@field:SerializedName("fraud_status")
	val fraudStatus: String? = null,

	@field:SerializedName("transaction_status")
	val transactionStatus: String? = null,

	@field:SerializedName("charge_type")
	val chargeType: String? = null,

	@field:SerializedName("status_code")
	val statusCode: String? = null,

	@field:SerializedName("pdf_url")
	val pdfUrl: String? = null,

	@field:SerializedName("merchant_id")
	val merchantId: String? = null,

	@field:SerializedName("gross_amount")
	val grossAmount: String? = null,

	@field:SerializedName("va_numbers")
	val vaNumbers: List<VaNumbersItem?>? = null,

	@field:SerializedName("payment_type")
	val paymentType: String? = null,

	@field:SerializedName("finish_200_redirect_url")
	val finish200RedirectUrl: String? = null,

	@field:SerializedName("bca_va_number")
	val bcaVaNumber: String? = null,

	@field:SerializedName("transaction_time")
	val transactionTime: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("bca_expiration")
	val bcaExpiration: String? = null,

	@field:SerializedName("expiry_time")
	val expiryTime: String? = null,

	@field:SerializedName("order_id")
	val orderId: String? = null,

	@field:SerializedName("bca_expiration_raw")
	val bcaExpirationRaw: String? = null,

	@field:SerializedName("finish_redirect_url")
	val finishRedirectUrl: String? = null
)

data class VaNumbersItem(

	@field:SerializedName("bank")
	val bank: String? = null,

	@field:SerializedName("va_number")
	val vaNumber: String? = null
)
