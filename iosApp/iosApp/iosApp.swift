import UIKit
import ComposeApp
import Firebase
import FirebaseCore
import FirebaseMessaging
import PushKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    var window: UIWindow?
    private var appLifecycleObserver: AppLifecycleObserver?
    private var pushRegistry: PKPushRegistry!
    private var pushKitHandler: PushKitHandler!
    private var callManager: CallManager!

    override init() {
        super.init()
        KoinHelperKt.doInitKoin(
            cipherInterface: IOChecker() as CipherInterface,
            appComponent: IosApplicationComponent(
                networkHelper: IosNetworkHelper() as NetworkHelper
            ),
            swiftFuncs: SwiftFuncsIos(
                swiftFuncsHelper: IosSwiftFuncsHelper() as SwiftFuncsHelper
            ),
            additionalModules: [],
            appDeclaration: { _ in }
        )
    }

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        FirebaseApp.configure()

        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }

        requestNotificationAuthorization(application)

        // Инициализация CallManager и PushKitHandler
        callManager = CallManager()
        pushKitHandler = PushKitHandler(callManager: callManager)

        setupPushKit()

        appLifecycleObserver = ComposeApp.AppLifecycleObserver_iosKt.getAppLifecycleObserver()

        NotifierManager.shared.initialize(
            configuration: NotificationPlatformConfigurationIos(showPushNotification: true, askNotificationPermissionOnStart: true)
        )

        return true
    }

    func requestNotificationAuthorization(_ application: UIApplication) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    application.registerForRemoteNotifications()
                }
            } else {
                print("Разрешение на уведомления отклонено: \(String(describing: error?.localizedDescription))")
            }
        }
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
        Messaging.messaging().token { fcmToken, error in
            if let error = error {
                print("Ошибка получения FCM токена: \(error)")
            } else if let fcmToken = fcmToken {
                print("FCM токен: \(fcmToken)")
            }
        }
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Не удалось зарегистрироваться для удаленных уведомлений: \(error.localizedDescription)")
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        appLifecycleObserver?.onAppForegrounded()
    }

    func applicationWillResignActive(_ application: UIApplication) {
        appLifecycleObserver?.onAppBackgrounded()
    }

    private func setupPushKit() {
        pushRegistry = PKPushRegistry(queue: DispatchQueue.main)
        pushRegistry.delegate = pushKitHandler
        pushRegistry.desiredPushTypes = [.voIP]
    }
}
