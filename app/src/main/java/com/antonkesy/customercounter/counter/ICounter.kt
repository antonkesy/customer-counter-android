package com.antonkesy.customercounter.counter

interface ICounter {
    fun getCurrentAmount(): Int
    fun getMax(): Int
    fun setMax(max: Int)
    fun increment()
    fun decrement()
    fun reset()
    fun setCurrent(current: Int)
}