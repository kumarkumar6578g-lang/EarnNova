package com.example.data.repository

import android.util.Log

enum class OperationType {
  CREATE, READ, UPDATE, DELETE, LIST, BATCH
}

fun handleFirestoreError(e: Exception, operation: OperationType, path: String) {
  Log.e(
    "FirestoreError",
    """{"operation":"${operation.name}","path":"$path","error":"${e.javaClass.simpleName}","message":"${e.message}"}""".trimIndent(),
    e
  )
}
