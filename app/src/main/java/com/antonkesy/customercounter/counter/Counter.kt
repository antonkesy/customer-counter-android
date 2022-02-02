package com.antonkesy.customercounter.counter

class Counter(
    private var current: Int,
    private var max: Int,
    private var counterListener: () -> Unit
) : ICounter {

    override fun getCurrentAmount(): Int {
        return current
    }

    override fun getMax(): Int {
        return max
    }

    override fun setMax(max: Int) {
        this.max = max
    }

    override fun increment() {
        ++current
        counterListener()
    }

    override fun decrement() {
        if (current > 0) {
            --current
            counterListener()
        }
    }

    override fun reset() {
        current = 0
        counterListener()
    }

    override fun setCurrent(current: Int) {
        this.current = current
    }
}