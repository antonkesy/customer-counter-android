package com.antonkesy.customercounter.counter

interface ICounterSettings {
    fun getMaxCustomer(): Int
    fun getCustomerAmount(): Int
    fun setCustomerAmount(amountOfCustomers: Int)
    fun getCustomerKey(): String
    fun getMaxCustomerKey(): String
}