import UIKit
import ComposeApp
import Firebase
import FirebaseCore
import FirebaseMessaging

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    var window: UIWindow?
    private var appLifecycleObserver: AppLifecycleObserver?

    override init() {
        super.init()
        KoinHelperKt.doInitKoin()
    }

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        FirebaseApp.configure() // Инициализация Firebase

        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }

        requestNotificationAuthorization(application)

        appLifecycleObserver = ComposeApp.AppLifecycleObserver_iosKt.getAppLifecycleObserver()

        // Инициализация NotifierManager с добавлением параметра askNotificationPermissionOnStart
        NotifierManager.shared.initialize(configuration: NotificationPlatformConfigurationIos(showPushNotification: true, askNotificationPermissionOnStart: true))

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
        print("Токен устройства APNS: \(deviceToken.map { String(format: "%02.2hhx", $0) }.joined())")
        
        // Получение FCM-токена и регистрация в Firebase
        Messaging.messaging().token { fcmToken, error in
            if let error = error {
                print("Ошибка получения FCM токена: \(error)")
            } else if let fcmToken = fcmToken {
                print("FCM токен: \(fcmToken)")
                // Отправьте fcmToken на ваш сервер, чтобы использовать его для отправки уведомлений
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

    // Реализация методов UNUserNotificationCenterDelegate, если необходимо
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("Получено уведомление при активном приложении: \(notification.request.content.userInfo)")
        completionHandler([.alert, .sound, .badge])
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        print("Пользователь открыл уведомление: \(response.notification.request.content.userInfo)")
        NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: response.notification.request.content.userInfo)
        completionHandler()
    }
}
