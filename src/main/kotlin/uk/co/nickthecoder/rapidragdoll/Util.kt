package uk.co.nickthecoder.rapidragdoll

fun timeString(seconds: Int) = "${seconds / 60}:${String.format("%02d", (seconds % 60))}"
