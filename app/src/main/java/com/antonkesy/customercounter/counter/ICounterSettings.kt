package com.antonkesy.customercounter.counter

interface ICounterSettings {
    fun getMaxCustomer(): Int
    fun getCustomerAmount(): Int
    fun putCustomerAmount(amountOfCustomers: Int)
    fun getCustomerKey(): String
    fun getMaxCustomerKey(): String
}