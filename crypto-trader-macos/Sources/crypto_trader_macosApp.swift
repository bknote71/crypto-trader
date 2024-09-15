import SwiftUI

@main
struct crypto_trader_macosApp: App {
  @NSApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
  
  var body: some Scene {
    WindowGroup {
      MainView()
        .fixedSize()
        .environmentObject(CryptoViewModel.shared)
        .environmentObject(CandleViewModel())
        .environmentObject(OrderViewModel())
        .environmentObject(UserViewModel.shared)
    }
    .windowResizability(.contentSize)
  }
}

// AppDelegate 정의
class AppDelegate: NSObject, NSApplicationDelegate {
  func applicationDidFinishLaunching(_ notification: Notification) {
    // 초기화 작업 수행
    initConfiguration()
  }
  
  private func initConfiguration() {
    CryptoViewModel.shared.fetchTicker()
  }
}
