package com.example.flushit.model

interface AllToiletsChangedListener {
    fun onAllToiletsChanged(toilets: List<Toilet>)
}