package pl.temomuko.autostoprace.data.remote

import java.io.IOException

class ApiException(val httpCode: Int) : IOException()