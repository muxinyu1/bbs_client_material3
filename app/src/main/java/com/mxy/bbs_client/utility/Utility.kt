package com.mxy.bbs_client.utility

import com.mxy.bbs_client.program.ProgramState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val MinLength = 16
private const val MaxLength = 64

object Utility {
    val IOCoroutineScope = CoroutineScope(Dispatchers.IO)
    val UICoroutineScope = CoroutineScope(Dispatchers.Main)
    fun getRandomString(): String {
        val length = (MinLength..MaxLength).random()
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}