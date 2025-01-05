package ir.rezajax.socketchat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.rezajax.socketchat.ui.theme.SocketChatTheme
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        /*
        // تغییر رنگ Status Bar
        window.statusBarColor = android.graphics.Color.BLACK // پس‌زمینه مشکی
        window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS) // برای اطمینان از ظاهر مناسب آیکون‌ها

        // تنظیم ظاهر آیکون‌ها
        window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)*/

        // تغییر رنگ Status Bar
        window.statusBarColor = android.graphics.Color.BLACK // پس‌زمینه مشکی
        window.decorView.systemUiVisibility = 0 // برای اطمینان از ظاهر مناسب آیکون‌ها

        // تنظیم ظاهر آیکون‌ها
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        setContent {
            var txt by remember { mutableStateOf("Socks5 running on port 1122") }


            Thread {
                val serverSocket = ServerSocket(1122)
                println("SOCKS5 Server is running on host:port give this to friends: 'nc localhost ${serverSocket.localPort}' ...")

                // rez: After disconnect waiting for new connection
                while (true) {
                    val clientSocket = serverSocket.accept()
                    println("Accepted a connection from ${clientSocket.toString()}")
                    handleClient(clientSocket) { log ->
                        txt += "\n$log"
                        println(log)
                    }
                }
            }.start()

//            setContent {
//                TerminalUI(txt = txt, onTextChange = { txt = it })
//            }


            SocketChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = Color.Black) // پس‌زمینه سیاه
                            .fillMaxSize()
                    ) {
                        Text(
                            text = txt, // متن مورد نظر
                            modifier = Modifier
                                .padding(16.dp) // حاشیه داخلی
                                .fillMaxWidth(),
                            color = Color.White, // رنگ متن سبز
                            fontFamily = FontFamily.Monospace, // فونت مونواسپیس برای شبیه‌سازی ترمینال
                            fontSize = 16.sp // سایز فونت
                        )
                    }
                }
            }

        }


    }
}


@Composable
fun TerminalUI(txt: String, onTextChange: (String) -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = Color.Black) // پس‌زمینه سیاه
                .fillMaxSize()
        ) {
            // Displaying the terminal text
            Text(
                text = txt, // متن مورد نظر
                modifier = Modifier
                    .padding(16.dp) // حاشیه داخلی
                    .fillMaxWidth(),
                color = Color.Green, // رنگ متن سبز
                fontFamily = FontFamily.Monospace, // فونت مونواسپیس برای شبیه‌سازی ترمینال
                fontSize = 16.sp // سایز فونت
            )

            // TextField for user input
            /*            TextField(
                            value = txt.toString(), // Use TextFieldValue here
                            onValueChange = onTextChange, // Handle changes in the text field
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textStyle = TextStyle(
                                color = Color.Green,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            ),
                            singleLine = true,

                            cursorBrush = SolidColor(Color.Green)
                        )*/
        }
    }
}




fun handleClient(socket: Socket, rezLog: (String) -> Unit) {
    socket.use { // Ensures the socket is closed after handling
        val input = socket.getInputStream()
        val output = socket.getOutputStream()

        rezLog("Client connected: ${socket.remoteSocketAddress}")

        // Read and print client data
        try {
            val reader = BufferedReader(InputStreamReader(input))
            val writer = PrintWriter(OutputStreamWriter(output), true)

            // Send initial server response (if required)
            writer.println("Hello! You are connected to the SOCKS5 Server.")

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                rezLog("Received from client: $line")
                // Echo the data back to the client (or implement SOCKS5 protocol logic here)
                writer.println("Server received: $line")
            }
        } catch (e: IOException) {
            rezLog("Error handling client: ${e.message}")
        }

        rezLog("Client disconnected: ${socket.remoteSocketAddress}")
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SocketChatTheme {
        Greeting("Android")
    }
}