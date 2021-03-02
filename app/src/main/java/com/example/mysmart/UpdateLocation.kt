package com.example.mysmart

import kotlin.math.roundToInt

class UpdateLocation {
    var lat=0
    var lng=0
    constructor(lat:Double,lng:Double){
        this.lat= lat.roundToInt()
        this.lng= lng.roundToInt()
    }
}