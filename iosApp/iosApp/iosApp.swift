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
        FirebaseApp.configure()
        NotifierManager.shared.initialize(configuration: NotificationPlatformConfigurationIos(showPushNotification: true, askNotificationPermissionOnStart: true))
        KoinHelperKt.doInitKoin()
    }
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        
        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }

        requestNotificationAuthorization(application)
        
        appLifecycleObserver = ComposeApp.AppLifecycleObserver_iosKt.getAppLifecycleObserver()
        
        return true
    }
    
    func requestNotificationAuthorization(_ application: UIApplication) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    application.registerForRemoteNotifications()
                }
            } else {
                print("Notification permission denied: \(String(describing: error?.localizedDescription))")
            }
        }
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
        print("APNS device token: \(deviceToken.map { String(format: "%02.2hhx", $0) }.joined())")
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error.localizedDescription)")
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        appLifecycleObserver?.onAppForegrounded()
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        appLifecycleObserver?.onAppBackgrounded()
    }
    
    // Implement UNUserNotificationCenterDelegate methods if needed
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.alert, .sound, .badge])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
    }
}
