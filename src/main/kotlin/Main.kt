package org.example
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import kotlin.random.Random

fun main() = runBlocking {
//    runSimulatedClient()
    runManualChatroom()
}

suspend fun runManualChatroom() = coroutineScope {
    val client = withContext(Dispatchers.IO){Socket("127.0.0.1", 8080)}
//    val client = Socket("127.0.0.1", 8080)
//    val output = PrintWriter(client.outputStream, true)
//    val testOutput = client.outputStream
    val input = BufferedReader(InputStreamReader(client.getInputStream()))
    val output = BufferedWriter(OutputStreamWriter(client.getOutputStream()))

    runBlocking {
        launch(Dispatchers.IO) {
            writeMessages(output)
        }
        launch(Dispatchers.IO) {
            readMessages(input)
        }
    }
    client.close()
}

suspend fun readMessages(input: BufferedReader) = coroutineScope {
    while (true) {
        val serverResult = input.readLine()!!
        println("Response from the server: $serverResult")
    }
}

suspend fun writeMessages(output: BufferedWriter) = coroutineScope {
    output.use{
        while (true) {
            val consoleInput = readLine()!!
            println("Sending $consoleInput to the server")
            output.write(consoleInput)
            output.newLine()
            output.flush()
//            output.println(consoleInput)
        }
    }
}

suspend fun runSimulatedClient() = coroutineScope {
    Socket("127.0.0.1", 8080).use {  client ->
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.getInputStream()))
        val testMessagesArray = arrayListOf("hello world", "there it is", "It was nice to meet you", "Thanks for the talks", "wtf man")

        for (i in 0..5) {
            launch(Dispatchers.IO) {
                while (true) {
                    val consoleInput = testMessagesArray.random()
                    println("Sending $consoleInput to the server")
                    delay(1000)

                    output.println(consoleInput)

                    val serverResult = input.readLine()
                    println("Response from the server: $serverResult")
                }
            }
        }
        while (true) {}
    }
}
