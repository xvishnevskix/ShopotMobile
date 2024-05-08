import androidx.compose.ui.window.ComposeUIViewController
import org.videotrade.shopot.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
