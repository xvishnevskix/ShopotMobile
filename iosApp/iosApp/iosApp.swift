import UIKit
import ComposeApp
import Firebase
import FirebaseCore
import FirebaseMessaging
import PushKit
import os.log
import AVFoundation // ✅ Добавляем импорт

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    var window: UIWindow?
    private var appLifecycleObserver: AppLifecycleObserver?
    private var pushRegistry: PKPushRegistry!
    
    override init() {
        super.init()

        // Инициализация Koin
        KoinHelperKt.doInitKoin(
            cipherInterface: IOChecker() as CipherInterface,
            appComponent: IosApplicationComponent(
                networkHelper: IosNetworkHelper() as NetworkHelper
            ),
            swiftFuncs: SwiftFuncsIos(
                  swiftFuncsHelper: IosSwiftFuncsHelper() as SwiftFuncsHelper // ✅ Передаём callManager
              ),
            additionalModules: SharedModulesKt.getSharedModules(),
            appDeclaration: { _ in }
        )




    }

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        FirebaseApp.configure()
        Messaging.messaging().delegate = nil // <-- Отключаем обработку FCM

        window = UIWindow(frame: UIScreen.main.bounds)
        let voipViewController = VoIPViewController()
        window?.rootViewController = voipViewController
        window?.makeKeyAndVisible()

        Logger.readLogs()
        requestNotificationAuthorization(application)

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
    
    // Реализация методов UNUserNotificationCenterDelegate, если необходимо
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
//        print("Получено уведомление при активном приложении: \(notification.request.content.userInfo)")
        completionHandler([.alert, .sound, .badge])
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        //        print("Пользователь открыл уведомление: \(response.notification.request.content.userInfo)")
        NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: response.notification.request.content.userInfo)
        completionHandler()
    }
}
